import java.util.List;

public class particle {

	public double density;
	public double pressure;
	public double x;
	public double y;
	public double u;
	public double v;
	public double a_x;
	public double a_y;
	public double new_y;
	public Boolean isDummy;

	public double W(double r) {
		double R=r/SPHPoiseuille.H;
		double W = 0;
		if (R <= 1) W = 15/(7*Math.PI*Math.pow(SPHPoiseuille.H,2))*(2/3d-Math.pow(R,2)+1/2d*Math.pow(R, 3));
		else if (R <= 2) W = 15/(7*Math.PI*Math.pow(SPHPoiseuille.H,2))*(1/6d*Math.pow(2-R,3));
		return W;
	}

	public double WGrad(double r) {
		double R=r/SPHPoiseuille.H;
		double W = 0;
		if (R <= 1) W = 15/(7*Math.PI*Math.pow(SPHPoiseuille.H,2))*(-2*R+3/2d*Math.pow(R, 2));
		else if (R <= 2) W = 15/(7*Math.PI*Math.pow(SPHPoiseuille.H,2))*(-1/2d*Math.pow(2-R,2));
		return W;
	}

	public double dist(particle A, particle B)
	{
		return Math.sqrt(Math.pow(A.x-B.x, 2) + Math.pow(A.y-B.y, 2));
	}


	public void updateDensity(List<particle> particleList){
		double density = 0;
		int len = particleList.size();
		for (int i = 0; i < len; i++) {
			density += SPHPoiseuille.PARTICLE_MASS*W(dist(this, particleList.get(i)));
		}
		this.density = density;
	}

	public void updatePressure(){
		this.pressure = Math.pow(SPHPoiseuille.C, 2)*this.density;
	}	

	void updateAcceleration(List<particle> particleList, int skip) {
		double newAcceleration = 0;
		int len = particleList.size();
		for (int i = 0; i < len; i++) {
			if (i==skip) continue;
			particle particle = particleList.get(i);
			double pressureGradient = 0d;
			double changeW = (v - particle.v)/dist(this,particle)*WGrad(dist(this, particle));
			double viscosityTerm = SPHPoiseuille.PARTICLE_MASS*(10000d*(y-particle.y)*(v-particle.v))/((density+particle.density)*dist(this,particle)); 
			pressureGradient += -SPHPoiseuille.PARTICLE_MASS*(this.pressure/Math.pow(this.density,2) + particle.pressure/Math.pow(particle.density,2));
			newAcceleration += (-pressureGradient+viscosityTerm)*changeW;
		}
		a_y=newAcceleration+9.81d;
	}

	void updateVelocity() {
		v += a_y*SPHPoiseuille.TIME_STEP;
	}

	void updatePosition() {
		y += v*SPHPoiseuille.TIME_STEP;
	}

	double getPosition() {
		return x;
	}

	public void updateParticle(List<particle> particleList){
		updateDensity(particleList);
		updatePressure();
	}

	public particle(double density, double pressure, double x, double y, double u, double v, Boolean isDummy){
		this.density = density;
		this.pressure = pressure;
		this.x = x;
		this.y = y;
		this.u = u;
		this.v = v;
		this.isDummy = isDummy;
	}

}
