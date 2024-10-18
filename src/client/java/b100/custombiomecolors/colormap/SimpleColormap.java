package b100.custombiomecolors.colormap;

public class SimpleColormap implements Colormap {

	public int color;
	
	public SimpleColormap(int color) {
		this.color = color;
	}
	
	@Override
	public int getColor(int x, int z) {
		return color;
	}

}
