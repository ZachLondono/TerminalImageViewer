import java.awt.image.BufferedImage;
import java.lang.Math;
import java.util.Arrays;

public class ImageScaler {
	
	public static BufferedImage scale(BufferedImage original, int scaledWidth, int scaledHeight) {

		if (original == null) return null;
		if (scaledWidth == 0 || scaledHeight == 0) return null;

		int width = original.getWidth();
		int height = original.getHeight();

		int sectorsWide = scaledWidth < width ? scaledWidth : width;
		int sectorsHigh = scaledHeight < height ? scaledHeight : height;

		int sectorWidth = width / sectorsWide; 
		int sectorHeight = height / sectorsHigh;

		BufferedImage scaledImg = new BufferedImage(scaledWidth, scaledHeight, 
													BufferedImage.TYPE_INT_RGB);

		for (int h = 0; h < sectorsHigh; h++) {
			for (int w = 0; w < sectorsWide; w++) {
			
				int startX = (w) * sectorWidth;
				int startY = (h) * sectorHeight;

				int adjWidth = sectorWidth + startX > width ? width - startX : sectorWidth;
				int adjHeight = sectorHeight + startY > height ? height - startY : sectorHeight;

				int[] rgb = original.getRGB(startX, startY,
					adjWidth, adjHeight, 
					null, 0, adjWidth);

				if (rgb.length == 0) continue;
				
				RGB average = averageRGB(rgb);
				average.alpha = 255;

				if (scaledWidth <= width && scaledHeight <= height) {
					scaledImg.setRGB(w, h, average.getRGB());
				} else {
				
					int[] upScaledSectorColor = new int[scaledWidth * scaledHeight];
					Arrays.fill(upScaledSectorColor, average.getRGB());

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
