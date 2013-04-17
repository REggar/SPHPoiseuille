import java.util.ArrayList;
import java.util.List;

public class GenerateParticles {

	// This class generates the initial box of particles

	public static double PARTICLE_SPACING = 1.25; // Distance between particles
	public static int BOX_WIDTH = 30; // Width of box
	public static int BOX_HEIGHT = 30; // Height of box
	public static int DUMMY_WIDTH = 2; // How many dummy particles at edges

	public List<Particle> particleList; 


	// Function to generate particles

	public List<Particle> generate() {
		particleList = new ArrayList<Particle>();

		for(double i = 0; i < BOX_WIDTH/PARTICLE_SPACING+2*DUMMY_WIDTH; i++){
			for(double j = 0; j < BOX_HEIGHT/PARTICLE_SPACING; j++){
				// Adding all the particles to the list of particles
				if(i < DUMMY_WIDTH) particleList.add(new Particle(1f, i*PARTICLE_SPACING , j*PARTICLE_SPACING ,0f, 0f, true, false, false));
				else if(i >= (BOX_WIDTH/PARTICLE_SPACING+DUMMY_WIDTH)) particleList.add(new Particle(1f, i*PARTICLE_SPACING , j*PARTICLE_SPACING ,0f, SPHPoiseuille.PLATE_VELOCITY, true, false, SPHPoiseuille.PLATE_VELOCITY!=0));
				else if(j == DUMMY_WIDTH) particleList.add(new Particle(1f, i*PARTICLE_SPACING, j*PARTICLE_SPACING, 0f, 0f, false, true, false));		
				else particleList.add(new Particle(1f, i*PARTICLE_SPACING, j*PARTICLE_SPACING, 0f, 0f, false, false, false));		
			}
		}

		return particleList;
	}

	public GenerateParticles() {
	}

}