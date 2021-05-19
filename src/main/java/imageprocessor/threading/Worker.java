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
			int index = Printer.color256(rgb);

			if ((i + rangeStart) % (width - 1) == 0) pixelString += '\n';
			pixelString += "\u001b[38;5;" + index + "m";
			pixelString += Printer.blockChar;
		}

	}

	public int getPixelIndex() {
		return pixelIndex;
	}

	public String getPixelString() {
		return pixelString;
	}

}
