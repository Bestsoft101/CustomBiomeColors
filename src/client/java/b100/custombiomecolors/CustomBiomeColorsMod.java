package b100.custombiomecolors;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import b100.custombiomecolors.ConfigLoader.ConfigLoadException;
import b100.custombiomecolors.colorizer.Colorizer;
import b100.json.JsonParser;
import b100.json.element.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class CustomBiomeColorsMod implements ClientModInitializer {
	
	public static final String MODID = "custombiomecolors";
	public static final String CONFIG_FILE_NAME = "biomecolors.json";
	
	public static final Colorizer GRASS = new Colorizer("grass");
	public static final Colorizer FOLIAGE = new Colorizer("foliage");
	public static final Colorizer WATER = new Colorizer("water");
	public static final Colorizer SKY = new Colorizer("sky");
	public static final Colorizer FOG = new Colorizer("fog");
	
	public static final List<Colorizer> allColorizers = new ArrayList<>();
	
	static {
		allColorizers.add(GRASS);
		allColorizers.add(FOLIAGE);
		allColorizers.add(WATER);
		allColorizers.add(SKY);
		allColorizers.add(FOG);
	}
	
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
	
	public static void onInit() {
		print("Init");

		SynchronousResourceReloader reloader = resourceManager -> setup(resourceManager);
		ReloadableResourceManagerImpl resourceManager = (ReloadableResourceManagerImpl) MinecraftClient.getInstance().getResourceManager();
		resourceManager.registerReloader(reloader);
		
		setup(resourceManager);
	}
	
	public static void setup(ResourceManager resourceManager) {
		print("Setup");
		
		for(Colorizer colorizer : allColorizers) {
			colorizer.reset();
		}
		
		Optional<Resource> resourceOptional = resourceManager.getResource(Identifier.of(MODID, CONFIG_FILE_NAME));
		if(!resourceOptional.isPresent()) {
			print("No biomecolors.json!");
			return;
		}
		
		try {
			Resource resource = resourceOptional.get();
			
			JsonObject root = JsonParser.instance.parseStream(resource.getInputStream());
			
			try {
				new ConfigLoader(root);
			}catch (ConfigLoadException e) {
				print("Loading failed: " + e.getMessage());
				e.printStackTrace();
				
				for(Colorizer colorizer : allColorizers) {
					colorizer.reset();
				}
			}
		}catch (Exception e) {
			print("Loading failed");
			e.printStackTrace();
		}
	}
	
	public static Colorizer getColorizer(String name) {
		for(Colorizer colorizer : allColorizers) {
			if(colorizer.getName().equalsIgnoreCase(name)) {
				return colorizer;
			}
		}
		return null;
	}
	
	public static String getBiomeId(Biome biome) {
		RegistryEntry<Biome> entry = getBiomeRegistryEntry(biome);
		if(entry != null) {
			return entry.getIdAsString();
		}
		return null;
	}
	
	@SuppressWarnings("resource")
	public static RegistryEntry<Biome> getBiomeRegistryEntry(Biome biome) {
		DynamicRegistryManager reg = MinecraftClient.getInstance().world.getRegistryManager();
		Registry<Biome> biomeRegistry = reg.get(RegistryKeys.BIOME);
		return biomeRegistry.getEntry(biome);
	}
	
	@SuppressWarnings("resource")
	public static RegistryKey<Biome> getBiomeKey(Biome biome) {
		DynamicRegistryManager reg = MinecraftClient.getInstance().world.getRegistryManager();
		Registry<Biome> biomeRegistry = reg.get(RegistryKeys.BIOME);
		Optional<RegistryKey<Biome>> biomeKey = biomeRegistry.getKey(biome);
		if(biomeKey.isPresent()) {
			return biomeKey.get();
		}
		return null;
	}
	
	public static String readStringResource(Resource resource) {
		BufferedReader br = null;
		try {
			br = resource.getReader();
			StringBuilder str = new StringBuilder();
			
			boolean firstLine = true;
			while(true) {
				String line = br.readLine();
				if(line == null) {
					break;
				}
				if(!firstLine) {
					str.append('\n');
				}
				str.append(line);
				firstLine = false;
			}
			
			return str.toString();
		}catch (Exception e) {
			throw new RuntimeException("Reading resource: " + resource, e);
		}finally {
			try {
				br.close();
			}catch (Exception e) {}
		}
	}
	
	public static void print(String string) {
		System.out.print("[CustomBiomeColors] " + string + "\n");
	}
	
	public static void debugPrint(String string) {
		System.out.print("[CustomBiomeColorsDebug] " + string + "\n");
	}
}