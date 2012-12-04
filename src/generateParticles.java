import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class generateParticles extends ArrayList {

	public static double PARTICLE_SPACING = 0.5;
	public static int BOX_WIDTH = 24; // How many particles wide box
	public static int BOX_HEIGHT = 32; // How many particle high is box

	public List<particle> particleList;
	public List<particle> dummyarticleList;

	public List<particle> generate() {
		particleList = new ArrayList<particle>();

		for(double i = 0; i < BOX_WIDTH+6; i++){
			for(double j = 0; j < BOX_HEIGHT+100*PARTICLE_SPACING; j++) {
				System.out.println("Creating particle at "+i+", "+(j-2*PARTICLE_SPACING));
				if(i<3 || i>=BOX_WIDTH+3) {
					particleList.add(new particle(1f, Math.pow(SPHPoiseuille.C, 2)*1f, i, j, 0f, 0f, true));
				}
				else if(j>BOX_HEIGHT) {}
				else {
					particleList.add(new particle(1f, 1f, i, j+5, 0f, 0f, false));
				}		 
			}
		}
		return particleList;
	}

	public generateParticles() {
	}
}