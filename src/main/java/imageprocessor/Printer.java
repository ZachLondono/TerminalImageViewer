package imageprocessor;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import imageprocessor.threading.Manager;
import imageprocessor.argreader.ArgReader;

public class Printer {

	public static final char backChar = '\b';
	public static final char blockChar = '\u2588';
	public static final String lineClear = "\033[F";

	private int scaledWidth = -1;
	private int scaledHeight = -1;
	private String path = null;
	private String outputPath = null;
	private boolean displayImage = true;
	private int colorFilter = -1;
	private int threadCount = 1;
	public boolean verbose = false;

	public static void main(String[] args) {
		Printer printer = new Printer();
		parseArgs(printer, args);
		printer.processImage();
	}
	
	public static void parseArgs(Printer printer, String[] args) {

		ArgReader reader = new ArgReader();

		reader.addArg("w", false, (arg) -> {
			if (arg == null) throw new Exception("'w' requires integer parameter ");
			try {
				printer.scaledWidth = Integer.parseInt(arg);		
			} catch (Exception e) {
				throw new Exception("'w' requires integer parameter");
			}
		});

		reader.addArg("h", false, (arg) -> {
			if (arg == null) throw new Exception("'h' requires integer parameter ");
			try {
				printer.scaledHeight = Integer.parseInt(arg);		
			} catch (Exception e) {
				throw new Exception("'h' requires integer parameter");
			}
		});

		reader.addArg("p", true, (arg) -> {
			if (arg == null) throw new Exception("'p' requires parameter ");
			printer.path = arg;
		});
		
		reader.addArg("o", false, (arg) -> {
			if (arg == null) throw new Exception("'o' requires parameter ");
			printer.outputPath = arg;
		});

		reader.addArg("c", false, (arg) -> {
			if (arg == null) throw new Exception("'c' requires either 6,8,256 as parameter");
			try {
				printer.colorFilter = Integer.parseInt(arg);
				if (printer.colorFilter != 5 && printer.colorFilter != 8 && printer.colorFilter != 256)
					throw new Exception();
			} catch (Exception e) {
				throw new Exception("'c' requires either 5,8,256 as parameter");
			}
		});

		reader.addArg("t", false, (arg) -> {
			if (arg == null) throw new Exception("'t' requires positive integer parameter");
			try {
				printer.threadCount = Integer.parseInt(arg);
				if (printer.threadCount < 1) 
					throw new Exception(); 
			} catch (Exception e) {
				throw new Exception("'t' requires positive integer parameter"); 
			}
		});

		reader.addArg("q", false, (arg) -> printer.displayImage = false); 

		reader.addArg("rgb", false, (arg) -> printer.colorFilter = 6);

		reader.addArg("v", false, (arg) -> printer.verbose = true);

		try {
			reader.parseArgs(args);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}


	public void processImage() {

		if (verbose) System.out.println("Opening Image");
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(path));
		} catch (Exception e) {
			System.err.println("Failed to read image file");
			System.exit(-1);
		}
	
		if (scaledWidth != -1 && scaledHeight != -1) {
			if (verbose) System.out.println("Scaling Image");
			image = ImageScaler.scaleImage(image, scaledWidth, scaledHeight);
		}

		if (colorFilter != -1) {
			if (verbose) System.out.println("Applying Color Filter");
			applyColorFilter(image);
		}

		if (outputPath != null) {
			if (verbose) System.out.println("Saving Image");
			saveImage(image);
		}

		if (verbose) System.out.println("Converting Image to Text");
		String s = imageToString(image);
		if (displayImage) System.out.println(s);

	}

	public void saveImage(BufferedImage image) {
		try {
			ImageIO.write(image, "jpg", new File(outputPath));
		} catch (Exception e) {
			System.err.println("Unable to save image to path '" + outputPath + "'");
		}
	}

	public void applyColorFilter(BufferedImage image) {

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				
				// Get pixel color value from image
				RGB color = new RGB(image.getRGB(x,y));
				
				// Apply filter to pixel 
				if (colorFilter == 5) color5(color); 
				else if (colorFilter == 8) color8(color);
				else if (colorFilter == 256) color256(color);

				// Set color value in image
				image.setRGB(x,y,color.getRGB());
			}
		}

	}

	public String imageToString(BufferedImage image) {

		String imageString = "";
		image = ImageScaler.scaleImage(image, image.getWidth(), image.getHeight() / 2);

		int columns = image.getWidth();
	
		// wrong size, doesn't get all pixels
		int[] imgColors = image.getRGB(0, 0,
						image.getWidth() - 1, image.getHeight() - 1,
						null, 0, image.getWidth() - 1);
		
		long startTime = System.nanoTime();
		Manager manager = new Manager(imgColors, threadCount, columns);
		for (String line: manager.getResult())
				imageString += line;
		long endTime = System.nanoTime();
		if (verbose) System.out.println("Elapsed (ms) : " + ((endTime - startTime) / 1000000)); 
		
		return imageString;
	
	}

	public void color5(RGB color) {
		
		if (color.red - color.blue < 10 && color.red - color.green < 10) {

			if (color.red < 255/2) {
				color.red = 0;
				color.blue = 0;
				color.green = 0;
			} else {
				color.red = 255;
				color.blue = 255;
				color.green = 255;
			}

		} else if (color.red > color.blue && color.red > color.green) {
			color.red = 255;
			color.green = 0;
			color.blue = 0;
		} else if (color.blue > color.red && color.blue > color.green) {
			color.blue = 255;
			color.green = 0;
			color.red = 0;
		} else if (color.green > color.red && color.green > color.blue) {
			color.green = 255;
			color.blue = 0;
			color.red = 0;
		} else {
			System.out.println(color);
		}

	}

	public void color8(RGB color) {
		
		if (color.red > (255/2))
			color.red = 255;
		else color.red = 0;
			
		if (color.blue > (255/2))
			color.blue = 255;
		else color.blue = 0;

		if (color.green > (255/2))
			color.green = 255;
		else color.green = 0;

	}

	public static int color256(RGB color) {
		
		int alphatemp = color.alpha;
		color.alpha = 0;

		RGB closest = null;
		int diffsum = 0;
		int index = 0;
		for (int c = 0; c < colors.length; c++) {
			
			RGB temp = new RGB(colors[c]);
			temp.alpha = 0;

			if (closest == null) {
				closest = temp;
				int rdiff = Math.abs(closest.red - color.red);
				int gdiff = Math.abs(closest.green - color.green);
				int bdiff = Math.abs(closest.blue - color.blue);
				diffsum = rdiff + gdiff + bdiff;
				index = c;
			} else {
				
				int rtemp = Math.abs(temp.red - color.red);
				int gtemp = Math.abs(temp.green - color.green);
				int btemp = Math.abs(temp.blue - color.blue);
				int tempsum = rtemp + gtemp + btemp;
				
				if (tempsum == 0) {
					index = c;
					break;
				}

				if (diffsum > tempsum ) {
					diffsum = tempsum;
					closest = temp;
					index = c;
				}

			}

		}
		
		if (closest != null) {
			color.red = closest.red;
			color.green = closest.green;
			color.blue = closest.blue;
		}

		color.alpha = alphatemp;
		return index;

	}
	
}
