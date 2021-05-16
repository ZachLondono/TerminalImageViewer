package imageprocessor;

public class RGB {

	public int alpha;
	public int red;
	public int green;
	public int blue;

	public RGB(int alpha, int red, int green, int blue) {
		this.alpha = alpha;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public RGB(int rgb) {
		this.alpha = (rgb >> 24) & 0xFF;
		this.red = (rgb >> 16) & 0xFF; 
		this.green = (rgb >> 8) & 0xFF; 
		this.blue = (rgb) & 0xFF; 
	}

	public int getRGB() {
		// TODO: add bitmasks
		int rgb = (alpha << 24);
		rgb += (red << 16);
		rgb += (green << 8);
		rgb += (blue);
		return rgb;
	}

	public String toString() {
		return "(R:" + red + ", G:" + green + ", B:" + blue + ", A:" + alpha + ")";
	}

}
