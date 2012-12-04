import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class SPHPoiseuille {

	public static List<particle> particleList;

	public static final double H = 1.2;
	public static final double PARTICLE_MASS = 1;
	public static final double C = 10;
	public static final double TIME_STEP = 0.0025d;

	public static void plot(List<particle> particleList, int iteration){
		int imageHeight = 1000;
		BufferedImage image = new BufferedImage((int) (generateParticles.BOX_WIDTH*generateParticles.PARTICLE_SPACING*20+100), imageHeight, BufferedImage.TYPE_INT_RGB);   
		Graphics g = image.getGraphics(); 
	
		Graphics2D    graphics = image.createGraphics();

		graphics.setPaint ( new Color ( 55, 55, 55 ) );
		graphics.fillRect ( 0, 0, image.getWidth(), image.getHeight() );
		
		int len = particleList.size();
		for (int i = 0; i < len; i++) {
			g.drawLine((int)((10+particleList.get(i).x*20)*generateParticles.PARTICLE_SPACING), (int)((10+particleList.get(i).y*20)*generateParticles.PARTICLE_SPACING), (int) ((10+particleList.get(i).x*20)*generateParticles.PARTICLE_SPACING), (int) ((10+particleList.get(i).y*20)*generateParticles.PARTICLE_SPACING));
		}

		try {    
			ImageIO.write(image, "jpg", new File("/home/$USERNAME/"+iteration+".jpg"));   
		} catch (IOException e) {    
			e.printStackTrace();   
		}  
	}

	public static void main(String[] args)
	{
		particleList = new generateParticles().generate();
		int len = particleList.size();
		SPHPoiseuille.plot(particleList, 0);

		for (int n = 0; n < 600; n++) {
			for (int i = 0; i < len; i++) {
				if(particleList.get(i).isDummy) continue;
				particleList.get(i).updateDensity(particleList);
				particleList.get(i).updatePressure();
			}

			for (int i = 0; i < len; i++) {
				if(particleList.get(i).isDummy) continue;
				particleList.get(i).updateAcceleration(particleList, i);
			}

			for (int i = 0; i < len; i++) {
				if(particleList.get(i).isDummy) continue;
				particleList.get(i).updateVelocity();
				particleList.get(i).updatePosition();
			}

			if (n%10==0) SPHPoiseuille.plot(particleList, n+1);
		}
	}
}
