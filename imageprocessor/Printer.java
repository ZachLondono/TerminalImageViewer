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

	public static int[] colors = new int[] {
					0, 8388608, 32768, 8421376, 128, 8388736, 32896, 12632256, 8421504, 
					16711680, 65280, 16776960, 255, 16711935, 65535, 16777215, 0, 
					95, 135, 175, 215, 255, 24320, 24415, 24455, 
					24495, 24535, 24575, 34560, 34655, 34695, 34735, 34775, 
					34815, 44800, 44895, 44935, 44975, 45015, 45055, 55040, 
					55135, 55175, 55215, 55255, 55295, 65280, 65375, 65415, 
					65455, 65495, 65535, 6225920, 6226015, 6226055, 6226095, 6226135, 
					6226175, 6250240, 6250335, 6250375, 6250415, 6250455, 6250495, 6260480, 
					6260575, 6260615, 6260655, 6260695, 6260735, 6270720, 6270815, 6270855, 
					6270895, 6270935, 6270975, 6280960, 6281055, 6281095, 6281135, 6281175, 
					6281215, 6291200, 6291295, 6291335, 6291375, 6291415, 6291455, 8847360, 
					8847455, 8847495, 8847535, 8847575, 8847615, 8871680, 8871775, 8871815, 
					8871855, 8871895, 8871935, 8881920, 8882015, 8882055, 8882095, 8882135, 
					8882175, 8892160, 8892255, 8892295, 8892335, 8892375, 8892415, 8902400, 
					8902495, 8902535, 8902575, 8902615, 8902655, 8912640, 8912735, 8912775, 
					8912815, 8912855, 8912895, 11468800, 11468895, 11468935, 11468975, 11469015, 
					11469055, 11493120, 11493215, 11493255, 11493295, 11493335, 11493375, 11503360, 
					11503455, 11503495, 11503535, 11503575, 11503615, 11513600, 11513695, 11513735, 
					11513775, 11513815, 11513855, 11523840, 11523935, 11523975, 11524015, 11524055, 
					11524095, 11534080, 11534175, 11534215, 11534255, 11534295, 11534335, 14090240, 
					14090335, 14090375, 14090415, 14090455, 14090495, 14114560, 14114655, 14114695, 
					14114735, 14114775, 14114815, 14124800, 14124895, 14124935, 14124975, 14125015, 
					14125055, 14135040, 14135135, 14135175, 14135215, 14135255, 14135295, 14145280, 
					14145375, 14145415, 14145455, 14145495, 14145535, 14155520, 14155615, 14155655, 
					14155695, 14155735, 14155775, 16711680, 16711775, 16711815, 16711855, 16711895, 
					16711935, 16736000, 16736095, 16736135, 16736175, 16736215, 16736255, 16746240, 
					16746335, 16746375, 16746415, 16746455, 16746495, 16756480, 16756575, 16756615, 
					16756655, 16756695, 16756735, 16766720, 16766815, 16766855, 16766895, 16766935, 
					16766975, 16776960, 16777055, 16777095, 16777135, 16777175, 16777215, 526344, 
					1184274, 1842204, 2500134, 3158064, 3815994, 4473924, 5131854, 5789784, 
					6316128, 6710886, 7763574, 8421504, 9079434, 9737364, 10395294, 11053224, 
					11711154, 12369084, 13027014, 13684944, 14342874, 15000804, 15658734};

}
