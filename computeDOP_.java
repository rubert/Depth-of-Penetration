import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.measure.Calibration;


public class computeDOP_ implements PlugIn {
	
	public void run(String arg) {
		IJ.showMessage("DOP Plugin Status","Load the Phantom Image.");		
		ImagePlus impPhantom = IJ.openImage();
		impPhantom.show();
	
		
		//Phantom image
		WaitForUserDialog wait = new WaitForUserDialog("Press OK when done.", "Phantom Loaded.\n Set the pixel spacing if necessary.\n\n Select a Rectangular ROI.");
		wait.show();
		
		//Find the intersection point
		Calibration cal = impPhantom.getCalibration(); 
		double delta = cal.pixelWidth;
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
			depthAxis[i] = delta*i;
			noiseProfile[i] *= 1.4;		
		}
		
		
		//Find the first intersection point following
		//a depth 20 mm 				
		int iDepthPixels = (int)(20/delta);
		float intersectionDepth = 0;		
		boolean keepLooping = true;
		
		while(keepLooping){
			if(phanProfile[iDepthPixels] < noiseProfile[iDepthPixels]){
				keepLooping = false;
				intersectionDepth = (float)(iDepthPixels*delta);
					}
			iDepthPixels++;
			}

		//Create profile plots 
		Plot dopPlot = new Plot("Mean Pixel Values", "Depth (mm)", "Mean Pixel Value", depthAxis, phanProfile);
		dopPlot.setColor(Color.RED);
		dopPlot.draw();
		
		dopPlot.addPoints( depthAxis, noiseProfile, Plot.LINE);
		dopPlot.setColor(Color.BLUE);
		dopPlot.draw();
		
		dopPlot.show();

		//Display intersection depth
		String DOPString = Float.toString(intersectionDepth);
		WaitForUserDialog DOPDisplay = new WaitForUserDialog("Record DOP", DOPString);   
		DOPDisplay.show();
		
	}
}
