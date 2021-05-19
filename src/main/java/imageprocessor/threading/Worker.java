package imageprocessor.threading;

import java.lang.Thread;

import imageprocessor.RGB;
import imageprocessor.Printer;

public class Worker extends Thread {
	
	private final int pixelIndex;
	private final int rangeStart;
	private final Integer[] pixelColors;
	private final int width;
	private String pixelString;

	public Worker(int pixelIndex, int rangeStart, int width, Integer[] colors) {
		this.pixelIndex = pixelIndex;
		this.rangeStart = rangeStart;
		this.pixelColors = colors;
		this.width = width;
		pixelString = "";
	}

	public void run() {
	
		for (int i = 0; i < pixelColors.length; i++) {
			RGB rgb = new RGB(pixelColors[i].intValue());
			if ((i + rangeStart) % (width - 1) == 0) pixelString += '\n';
			int rgbIndex = 0;
			
			if (rgb.red - rgb.blue <= 10 && rgb.red - rgb.green <= 10) {
				
				rgbIndex = ((rgb.red - 8) / 10) + 232;  
				// grey indecies are between 232-255 darkest to brightest, but 231 is pure white  
				if (rgbIndex > 255) rgbIndex = 231; 
			
			} else {
				int redIndex = getColorIndex(rgb.red);
				int greenIndex = getColorIndex(rgb.green);
				int blueIndex = getColorIndex(rgb.blue);
				rgbIndex = redIndex * 36 + greenIndex * 6 + blueIndex + 16;
			}
			
			pixelString += "\u001b[38;5;" + rgbIndex + "m";
			pixelString += Printer.blockChar;

		}


	}

	/**
	*
	* Colors are stored in an array from 0-255
	* the indexes from 16-231 contains most of the colors in increasing values of red, then green, then blue
	*/
	private int getColorIndex(int color) {
		
		if (color >= 155) {
			if (color <= 195) return 3;
			if (color <= 235) return 4;
			return color <= 255 ? 5 : -1;
		}
		
		if (color < 47) return 0;
		if (color < 115) return 1;
		return 2;

	}

	public int getPixelIndex() {
		return pixelIndex;
	}

	public String getPixelString() {
		return pixelString;
	}

}
