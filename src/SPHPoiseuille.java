import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SPHPoiseuille {

	public static List<Particle> particleList;

	public static final double H = 1.5d; // Smoothing length
	public static final double PARTICLE_MASS = 1d; // Mass of particle
	public static final double C = 30; // Speed of sound in the fluid
	public static final double TIME_STEP = 0.0025d;  // Time step
	public static final double GRAVITY = 0d;	 // Acceleration due to gravity
	public static final double KINEMATIC_VISCOSITY = 50d; // Kinematic viscosity of fluid
	public static final double PLATE_VELOCITY = 5d; // Velocity of plate at x=BOX_WIDTH
	public static final int IMAGES_TO_OUTPUT = 80; // Number of images to output
	public static final int IMAGE_N = 100; // Output images every n iterations
	
	final static String OUTPUT = "/OUTPUT/dir/"; // Output folder

	public static void paraview(List<Particle> particleList, int iteration){
		// Generate .vtu file for viewing in ParaView
		String output;
		int particleCount = (int) (GenerateParticles.BOX_HEIGHT*GenerateParticles.BOX_WIDTH/Math.pow(GenerateParticles.PARTICLE_SPACING,2));
		output = "<?xml version=\"1.0\"?>\n<VTKFile type= \"UnstructuredGrid\"  version= \"0.1\"  byte_order= \"BigEndian\">\n"
				+ "<UnstructuredGrid>\n"
				+ "<Piece NumberOfPoints=\""+particleCount+"\" NumberOfCells=\""+particleCount+"\">\n"
				+ "<PointData Scalars=\"Pressure\" Vectors=\"Velocity\">\n"
				+ "<DataArray type=\"Float32\" Name=\"Pressures\" format=\"ascii\">\n";

		int len = particleList.size();
		for (int i = 0; i < len; i++) {
			if(particleList.get(i).isDummy) continue;
			output += "" + particleList.get(i).p + "\n";
		} 

		output += "</DataArray>\n"
				+"<DataArray type=\"Float32\" Name=\"Density\" format=\"ascii\">\n";

		for (int i = 0; i < len; i++) {
			if(particleList.get(i).isDummy) continue;
			output += "   " + particleList.get(i).rho + "\n";
		} 

		output += "</DataArray>\n"
				+ "<DataArray type=\"Float32\" Name=\"u-Velocity\" format=\"ascii\">\n";

		for (int i = 0; i < len; i++) {
			if(particleList.get(i).isDummy) continue;
			output += "   " + particleList.get(i).u + "\n";
		} 

		output += "</DataArray>\n"
				+ "<DataArray type=\"Float32\" Name=\"v-Velocity\" format=\"ascii\">\n";

		for (int i = 0; i < len; i++) {
			if(particleList.get(i).isDummy) continue;
			output += particleList.get(i).v + "\n";
		}

		output += "</DataArray>\n"
				+ "<DataArray type=\"Float32\" Name=\"x-Acceleration\" format=\"ascii\">\n";

		for (int i = 0; i < len; i++) {
			if(particleList.get(i).isDummy) continue;
			output += "   " + particleList.get(i).a_x + "\n";
		} 

		output += "</DataArray> \n"
				+ "<DataArray type=\"Float32\" Name=\"y-Acceleration\" format=\"ascii\">\n";

		for (int i = 0; i < len; i++) {
			if(particleList.get(i).isDummy) continue;
			output += particleList.get(i).a_y + "\n";
		}

		output += "</DataArray>\n"
				+ "<DataArray type=\"Float32\" Name=\"Scalarplot\" format=\"ascii\">\n";

		for (int i = 0; i < len; i++) {
			if(particleList.get(i).isDummy) continue;
			output += "1" + "\n";
		} 	

		output += "</DataArray>\n"
				+ "</PointData>\n"
				+ "<Points>\n"
				+ "<DataArray type=\"Float32\" NumberOfComponents=\"3\" format=\"ascii\">\n"; 

		for (int i = 0; i < len; i++) {
			if(particleList.get(i).isDummy) continue;
			output += "\t" + particleList.get(i).x + "\t" + particleList.get(i).y + "\t" + "0.0000000" + "\n";
		} 	

		output += "</DataArray>\n"
				+ "</Points>\n "
				+ "<Cells>\n"
				+ "<DataArray type=\"Int32\" Name=\"connectivity\" format=\"ascii\">\n";

		int n = 0;
		for (int i = 0; i < len; i++) {
			if(particleList.get(i).isDummy) continue;
			output += n + "\n";
			n++;
		} 	

		output += "</DataArray>\n"
				+ "<DataArray type=\"Int32\" Name=\"offsets\" format=\"ascii\">\n";

		n = 0;
		for (int i = 0; i < len; i++) {
			if(particleList.get(i).isDummy) continue;
			n++;
			output += n + "\n";
		} 

		output += "</DataArray> \n"
				+ "<DataArray type=\"Int32\" Name=\"types\" format=\"ascii\"> \n";

		for (int i = 0; i < len; i++) {
			if(particleList.get(i).isDummy) continue;
			output += 1 + "\n";
		} 

		output += "</DataArray>\n"
				+ "</Cells>\n"
				+ "</Piece>\n"
				+ "</UnstructuredGrid>\n"
				+ "</VTKFile>";

		BufferedWriter file;
		try {
			file = new BufferedWriter(new FileWriter(OUTPUT+"/part"+iteration+".vtu"));
			file.write(output);
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String removeLastChar(String str) {
		return str.substring(0,str.length()-2);
	}

	public static void main(String[] args)
	{
		// Main loop of method
		particleList = new GenerateParticles().generate();

		int len = particleList.size();

		Grid grid = new Grid(particleList);

		for (int n = 0; n < IMAGES_TO_OUTPUT*IMAGE_N; n++) {
			for (int i = 0; i < len; i++) {
				particleList.get(i).updateGrid(grid);
			}
			for (int i = 0; i < len; i++) {

				if(particleList.get(i).isDummy) continue;
				particleList.get(i).updateDensity(grid);
				particleList.get(i).updatePressure();
			}

			for (int i = 0; i < len; i++) {
				if(particleList.get(i).isDummy) continue;
				particleList.get(i).updateAcceleration(grid);
			}

			for (int i = 0; i < len; i++) {
				if(particleList.get(i).isDummy) continue;
				particleList.get(i).updateVelocity();
				particleList.get(i).updatePosition();
			}

			grid.clean();

			if ((n+1)%IMAGE_N==0 || n==0) {
				// Output ParaView file
				SPHPoiseuille.paraview(particleList, n+1);
			}
		}



	}
}
