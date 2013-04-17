import java.util.ArrayList;
import java.util.List;


class Cell {

	// This class contains information of each cell of the grid

	private List<Particle> content = new ArrayList<Particle>();


	public void add(Particle particle) {
		// Add particle to cell
		content.add(particle);
	}

	public void clean()
	{
		// Clear all contents of cell if required
		content.clear();
	}


	public List<Particle> contents()
	{
		// Return contents of cell
		return content;
	}

	public Cell(){
		// Initialise cell
		content = new ArrayList<Particle>();
	}
}