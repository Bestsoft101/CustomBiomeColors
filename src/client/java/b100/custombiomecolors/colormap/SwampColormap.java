package b100.custombiomecolors.colormap;

import net.minecraft.world.biome.Biome;

@SuppressWarnings("removal")
public class SwampColormap implements Colormap {
	
	public int color0;
	public int color1;
	public double treshold;
	public double scale;
	
	public SwampColormap() {
		this.color0 = 5011004;
		this.color1 = 6975545;
		this.treshold = -0.1;
		this.scale = 0.025;
	}
	
	public SwampColormap(int color0, int color1, double treshold, double scale) {
		this.color0 = color0;
		this.color1 = color1;
		this.treshold = treshold;
		this.scale = scale;
	}
	
	@Override
	public int getColor(int x, int z) {
		double noiseVal = Biome.FOLIAGE_NOISE.sample(x * scale, z * scale, false);
		return noiseVal < treshold ? color1 : color0;
	}
	
	public SwampColormap setColor0(int color0) {
		this.color0 = color0;
		return this;
	}
	
	public SwampColormap setColor1(int color1) {
		this.color1 = color1;
		return this;
	}
	
	public SwampColormap setScale(double scale) {
		this.scale = scale;
		return this;
	}
	
	public SwampColormap setTreshold(double treshold) {
		this.treshold = treshold;
		return this;
	}
	
}
