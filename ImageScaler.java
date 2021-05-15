import java.awt.image.BufferedImage;
import java.lang.Math;
import java.util.Arrays;

public class ImageScaler {
	
	public static BufferedImage scaleImage(BufferedImage original, int scaledWidth, int scaledHeight) {

		if (original == null) return null;
		if (scaledWidth == 0 || scaledHeight == 0) return null;

		int width = original.getWidth();
		int height = original.getHeight();

		// number of sectors wide/high in the original image
		// if scaled down,  1 sector maps to 1 pixel in scaled image
		// if scaled up,  each sector is 1 pixel mapped to a larger region in scaled image
		int sectorsWide = scaledWidth < width ? scaledWidth : width;
		int sectorsHigh = scaledHeight < height ? scaledHeight : height;

		// width / height of each sector in the original image
		int sectorWidth = width / sectorsWide; 
		int sectorHeight = height / sectorsHigh;

		// Instance of the new scaled image
		BufferedImage scaledImg = new BufferedImage(scaledWidth, scaledHeight, 
													BufferedImage.TYPE_INT_RGB);

		for (int h = 0; h < sectorsHigh; h++) {
			for (int w = 0; w < sectorsWide; w++) {
			
				int startX = w * sectorWidth;
				int startY = h * sectorHeight;

				// If sectors don't evenly divide into the original image size
				// the width/height of the last row/column of sectors is shrunk to fit
				int adjWidth = sectorWidth + startX > width ? width - startX : sectorWidth;
				int adjHeight = sectorHeight + startY > height ? height - startY : sectorHeight;

				int[] rgb = original.getRGB(startX, startY,
					adjWidth, adjHeight, 
					null, 0, adjWidth);

				if (rgb.length == 0) continue;
				
				RGB average = averageRGB(rgb);
				average.alpha = 255;

				if (scaledWidth <= width && scaledHeight <= height) {
					// Scaled down image, one sector to one pixel
					scaledImg.setRGB(w, h, average.getRGB());
				} else {
					
					// Scaled up image, one pixel to one sector in scaled image
					int[] upScaledSectorColor = new int[scaledWidth * scaledHeight];
					Arrays.fill(upScaledSectorColor, average.getRGB());

					// Size of the scaled sector in upscaled image
					int upScaledSectorWidth = scaledWidth / width;
					int upScaledSectorHeight = scaledHeight / height;
					int upScaledStartX = w * upScaledSectorWidth;
					int upScaledStartY = h * upScaledSectorHeight;

					scaledImg.setRGB(upScaledStartX, upScaledStartY, 
								upScaledSectorWidth, upScaledSectorHeight,
								upScaledSectorColor, 1, upScaledSectorWidth);  
	
				}

			}
		}
			
		return scaledImg;

	}

	private static RGB averageRGB(int[] rgb) {

		RGB average = new RGB(0);
		for (int i = 0; i < rgb.length; i++) {
			
			RGB temp = new RGB(rgb[i]);
			
			average.red += temp.red;
			average.green += temp.green;
			average.blue += temp.blue;

		}

		average.red = average.red / rgb.length;
		average.green = average.green / rgb.length;
		average.blue = average.blue / rgb.length;

		return average;	

	}


}
