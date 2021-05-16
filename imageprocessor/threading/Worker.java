package imageprocessor.threading;

import java.lang.Thread;

import imageprocessor.RGB;
import imageprocessor.Printer;

public class Worker extends Thread {
	
	private final int pixelIndex;
	private final int pixelColor;
	private String pixelString;

	public Worker(int pixelIndex, int color) {
		this.pixelIndex = pixelIndex;
		this.pixelColor = color;
		pixelString = "";
	}

	public void run() {

		RGB rgb = new RGB(pixelColor);
		int index = Printer.color256(rgb);

		pixelString += "\u001b[38;5;" + index + "m";
		pixelString += Printer.blockChar;

	}

	public int getPixelIndex() {
		return pixelIndex;
	}

	public String getPixelString() {
		return pixelString;
	}

}
