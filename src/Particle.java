import java.util.List;

public class Particle {

	public double rho; // Density
	public double p; // Pressure
	public double x; // x coordinate
	public double y; // y coordinate
	public double u; // u velocity
	public double v; // v velocity of
	public double a_x; // Acceleration in x direction
	public double a_y; // Acceleration in y direction
	public Boolean isDummy; // Is particle a dummy particle?
	public Boolean isHighlighted = false;
	public Boolean dummyMoving; // Is particle a dummy particle that is moving?

	public int gridx; // x-coordinate of the cell the particle is in
	public int gridy; // y-coordinate of the cell the particle is in

	public double W(double r) {
		// Calculate the kernel function (cubic-spline kernel)
		double R=r/SPHPoiseuille.H;
		double W = 0;
		if (R <= 1) W = 15/(7*Math.PI*Math.pow(SPHPoiseuille.H,2))*(2d/3d-Math.pow(R,2)+1d/2d*Math.pow(R, 3));
		else if (R <= 2) W = 15/(7*Math.PI*Math.pow(SPHPoiseuille.H,2))*(1/6d*Math.pow(2-R,3));
		return W;
	}

	public double WGrad(double r) {
		// Calculate the gradient of the kernel function (cubic-spline method)
		double R=r/SPHPoiseuille.H;
		double W = 0d;
		if (R <= 1) W = 15d/(7d*Math.PI*Math.pow(SPHPoiseuille.H,2))*(-2d*R+3d/2d*Math.pow(R, 2));
		else if (R <= 2) W = 15d/(7*Math.PI*Math.pow(SPHPoiseuille.H,2))*(-1d/2d*Math.pow(2-R,2));
		return W;
	}

	public double dist(Particle A, Particle B)
	{
		// Calculate the distance between particle A and particle B, this
		// takes into account the looping nature of the particles.
		double distY = Math.min(Math.abs(A.y-B.y), GenerateParticles.BOX_HEIGHT-Math.abs(A.y-B.y));
		return Math.sqrt(Math.pow(A.x-B.x, 2) + Math.pow(distY, 2));
	}
	
	public void updateDensity(Grid grid){
		// Update density of the particle using summation density approach
		double density = 0;
		List<Particle> particles = grid.listParticles(this);
		int len = particles.size();
		for (int i = 0; i < len; i++) {	
			density += SPHPoiseuille.PARTICLE_MASS*W(dist(this, particles.get(i)));
		}
		this.rho = density;
	}

	public void updatePressure(){
		// Update pressure
		this.p = Math.pow(SPHPoiseuille.C, 2)*this.rho;
	}	

	void updateAcceleration(Grid grid) {
		// Update acceleration of particle using laminar flow method.
		double mi = SPHPoiseuille.PARTICLE_MASS;
		double nu = SPHPoiseuille.KINEMATIC_VISCOSITY;
		double h = SPHPoiseuille.H;
		double pressureGradient = 0;
		double xViscosityTerm = 0;
		double yViscosityTerm = 0;
		double xNewAcceleration = 0;
		double yNewAcceleration = 0;
		List<Particle> particles = grid.listParticles(this);
		int len = particles.size();
		for (int i = 0; i < len; i++) {
			Particle particle = particles.get(i);
			if (this==particle) continue;
			else if(particle.isDummy & SPHPoiseuille.PLATE_VELOCITY==0) {
				particle.u = -this.u;
				particle.v = -this.v;
			}
			//Fortran                 DUVIS(I)=DUVIS(I)+PMB*&
			//Fortran                          (MUA+MUB)*(UA-UB)/(RHOA*RHOB*R*H)*DWDQ
			double xpressureGradient = (x-particle.x)/(h*dist(this,particle))*(this.p/Math.pow(this.rho,2) + particle.p/Math.pow(particle.rho,2));
			xViscosityTerm = mi*4.d*nu*(u-particle.u)/((rho+particle.rho)*h*dist(this,particle)); 
			yViscosityTerm = mi*4.d*nu*(v-particle.v)/((rho+particle.rho)*h*dist(this,particle)); 
			xNewAcceleration += (-xpressureGradient+xViscosityTerm)*WGrad(dist(this, particle));
			yNewAcceleration += (-pressureGradient+yViscosityTerm)*WGrad(dist(this, particle));
		}
		a_x=xNewAcceleration;
		a_y=yNewAcceleration+SPHPoiseuille.GRAVITY;
	}

	void updateVelocity() {
		// Update velocity
		u += a_x*SPHPoiseuille.TIME_STEP;
		v += a_y*SPHPoiseuille.TIME_STEP;
	}

	void updatePosition() {
		// Update position
		x += u*SPHPoiseuille.TIME_STEP;		
		y += v*SPHPoiseuille.TIME_STEP;
		
		// Loop particle round if required
		if(y>=(GenerateParticles.BOX_HEIGHT)) {
			this.y=this.y%(GenerateParticles.BOX_HEIGHT);
		}
	}


	void updateGrid(Grid grid) {
		// Change cell of particle if required
		gridx = (int) Math.floor((double) x/(2*SPHPoiseuille.H));
		gridy = (int) Math.floor((double) y/(2*SPHPoiseuille.H));
		grid.addToCell(this);
	}


	public Particle(double density, double x, double y, double u, double v, Boolean isDummy, Boolean isHighlighted, Boolean dummyMoving){
		this.rho = density;
		updatePressure();
		this.x = x;
		this.y = y;
		this.u = u;
		this.v = v;
		this.isDummy = isDummy;
		this.isHighlighted = isHighlighted;
		this.dummyMoving = dummyMoving;

		this.gridx = (int) Math.floor((double) x/(2*SPHPoiseuille.H));
		this.gridy = (int) Math.floor((double) y/(2*SPHPoiseuille.H));

		System.out.println("Creating particle at "+x+", "+y+" Grid: " +gridx+", "+gridy);
	}

}