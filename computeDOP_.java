import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;

//Adding a comment to attempt a commit

public class computeDOP_ implements PlugIn {
	
	public void run(String arg) {
		IJ.showMessage("DOP Plugin Status","Load the Phantom Image");		
		ImagePlus impPhantom = IJ.openImage();
		impPhantom.show();
	
		
		//Phantom image
		WaitForUserDialog wait = new WaitForUserDialog("Phantom Loaded, Select a Rectangular ROI", "Please press OK when done.");
		wait.show();
		
		Roi userROI = impPhantom.getRoi();
		ProfilePlot pPlotPhantom = new ProfilePlot(impPhantom, true);
		double[] phanProfile = pPlotPhantom.getProfile();
		
		//Air Image
		IJ.showMessage("DOP Plugin Status","Load the Air Image");
		ImagePlus impNoise = IJ.openImage();
		impNoise.show();
		impNoise.setRoi(userROI);
		ProfilePlot pPlotNoise = new ProfilePlot(impNoise, true);
		double[] noiseProfile = pPlotNoise.getProfile();

		double[] depthAxis = new double[noiseProfile.length];
		
		for(int i=0; i<noiseProfile.length; i++){
			depthAxis[i] = i;
			noiseProfile[i] *= 1.4;		
		}

		//Create profile plots and find their intersection point
				
		Plot dopPlot = new Plot("Mean Pixel Values", "Depth (mm)", "Mean Pixel Value", depthAxis, phanProfile);
		dopPlot.setColor(Color.RED);
		dopPlot.draw();
		
		dopPlot.addPoints( depthAxis, noiseProfile, Plot.LINE);
		dopPlot.setColor(Color.BLUE);
		dopPlot.draw();
		
		dopPlot.show();


	}

}
