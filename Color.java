public enum Color {
	
	BLACK("[30m"),	// R0		G0		B0
	RED("[31m"),	// R255		G0		B0
	GREEN("[32m"),	// R0		G255	B0
	YELLOW("[33m"),	// R255		G255	B0
	BLUE("[34m"),   // R0		G0		B255
	MAGENTA("[35m"),// R255		G0		B255
	CYAN("[36m"),	// R0		G255	B255
	WHITE("[37m"),	// R255		G255	B255
	RESET("[0m");

	private String code;

	private Color(String code) {
		this.code = (char)27 + code;
	}

	public static Color getRGBColor(int rgb) {
		
		RGB color = new RGB(rgb);

		if (color.red == 255) {
		
			if (color.blue == 255) { 
				if (color.green == 255) {
					return Color.WHITE;
				} else { 
					return Color.MAGENTA;
				}
			}
	
			if (color.green == 255) {
				return Color.YELLOW;
			} else {
				return Color.RED;
			}
		
		} else if (color.green == 255) {

			if (color.blue == 255) {
				return Color.CYAN;
			} else {
				return Color.GREEN;
			}

		} else if (color.blue == 255) {
			return Color.BLUE;
		}

		return Color.BLACK;

	}

	public String toString() {
		return code;
	}

}


