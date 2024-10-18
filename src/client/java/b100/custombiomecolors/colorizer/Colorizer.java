package b100.custombiomecolors.colorizer;

import java.util.HashMap;
import java.util.Map;

import b100.custombiomecolors.CustomBiomeColorsMod;
import b100.custombiomecolors.colormap.Colormap;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public class Colorizer {
	
	private final String name;
	private Colormap defaultColormap;
	private final Map<String, Colormap> colormaps = new HashMap<>();
	
	public Colorizer(String name) {
		this.name = name;
	}
	
	public void put(String biomeId, Colormap colormap) {
		colormaps.put(biomeId, colormap);
	}
	
	public Colormap getColormap(String biomeId) {
		Colormap colormap = colormaps.get(biomeId);
		if(colormap != null) {
			return colormap;
		}
		return defaultColormap;
	}
	
	public Colormap getColormap(Biome biome) {
		return getColormap(CustomBiomeColorsMod.getBiomeId(biome));
	}
	
	public Colormap getColormap(RegistryEntry<Biome> biome) {
		return getColormap(biome.getIdAsString());
	}
	
	public void setDefaultColormap(Colormap defaultColormap) {
		this.defaultColormap = defaultColormap;
	}
	
	public void reset() {
		colormaps.clear();
		defaultColormap = null;
	}
	
	public String getName() {
		return name;
	}

}
