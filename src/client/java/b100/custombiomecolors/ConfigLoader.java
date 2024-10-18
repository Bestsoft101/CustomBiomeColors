package b100.custombiomecolors;

import static b100.custombiomecolors.CustomBiomeColorsMod.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import b100.custombiomecolors.colorizer.Colorizer;
import b100.custombiomecolors.colormap.Colormap;
import b100.custombiomecolors.colormap.SimpleColormap;
import b100.json.element.JsonArray;
import b100.json.element.JsonElement;
import b100.json.element.JsonEntry;
import b100.json.element.JsonObject;

public class ConfigLoader {
	
	public JsonObject root;
	
	private Map<String, Set<String>> biomeGroups = new HashMap<>();
	
	private boolean fileContainsInvalidColorizer = false;
	
	public ConfigLoader(JsonObject root) throws ConfigLoadException {
		this.root = root;

		debugPrint("Loading " + CONFIG_FILE_NAME);
		
		loadBiomeGroups();
		loadColorizers();
		
		if(fileContainsInvalidColorizer) {
			debugPrint("Available Colorizers: ");
			for(Colorizer colorizer : allColorizers) {
				debugPrint("  " + colorizer.getName());
			}
		}
	}
	
	private void loadBiomeGroups() throws ConfigLoadException {
		JsonObject biomeGroups = root.getObject("biome_groups");
		if(biomeGroups == null) {
			debugPrint("No biome groups");
			return;
		}
		
		debugPrint("Loading biome groups");

		List<JsonEntry> entryList = biomeGroups.entryList();
		for(int i=0; i < entryList.size(); i++) {
			JsonEntry entry = entryList.get(i);
			
			String name = entry.name;
			debugPrint("  Biome group: " + name);
			
			Set<String> biomeGroup = this.biomeGroups.get(name);
			if(biomeGroup == null) {
				biomeGroup = new HashSet<>();
				this.biomeGroups.put(name, biomeGroup);
			}
			
			JsonArray biomes = entry.value.getAsArray();
			for(JsonElement element : biomes) {
				String biome = element.getAsString().value;
				biomeGroup.add(biome);
				debugPrint("    Biome: " + biome);
			}
		}
	}
	
	private void loadColorizers() throws ConfigLoadException {
		JsonObject colorizers = root.getObject("colorizers");
		if(colorizers == null) {
			throw new ConfigLoadException("biomecolors.json is missing 'colorizer' object!");
		}
		
		debugPrint("Loading colorizers");
		
		List<JsonEntry> entryList = colorizers.entryList();
		for(int i=0; i < entryList.size(); i++) {
			JsonEntry entry = entryList.get(i);
			
			Colorizer colorizer = getColorizer(entry.name);
			if(colorizer == null) {
				print("Colorizer does not exist: '" + entry.name + "'!");
				fileContainsInvalidColorizer = true;
				return;
			}
			
			debugPrint("  Loading colorizer: " + colorizer.getName());
			
			JsonObject obj = entry.value.getAsObject();
			JsonElement defaultElement = obj.get("default");
			if(defaultElement != null) {
				colorizer.setDefaultColormap(parseColormap(defaultElement));
			}
			
			JsonElement biomesElement = obj.get("biomes");
			if(obj.has("biomes")) {
				if(!obj.isObject()) {
					throw new ConfigLoadException("'biomes' must be an object!");
				}
				loadBiomes(biomesElement.getAsObject(), colorizer);
			}
		}
	}
	
	private void loadBiomes(JsonObject biomes, Colorizer colorizer) throws ConfigLoadException {
		List<JsonEntry> entryList = biomes.entryList();
		for(int i=0; i < entryList.size(); i++) {
			JsonEntry entry = entryList.get(i);
			
			boolean isGroup = entry.name.startsWith("#");
			
			if(entry.name.startsWith("#")) {
				debugPrint("    Group: " + entry.name);
			}else {
				debugPrint("    Biome: " + entry.name);
			}
			
			Colormap colormap = parseColormap(entry.value);			
			if(isGroup) {
				Set<String> biomeGroup = biomeGroups.get(entry.name.substring(1));
				for(String biomeName : biomeGroup) {
					colorizer.put(biomeName, colormap);	
				}
			}else {
				colorizer.put(entry.name, colormap);	
			}
		}
	}
	
	private Colormap parseColormap(JsonElement element) throws ConfigLoadException {
		if(element.isNumber()) {
			return new SimpleColormap(element.getAsNumber().getInteger());
		}
		if(element.isString()) {
			String string = element.getAsString().value;
			
			if(string.startsWith("#")) {
				// Hex Color Code
				return new SimpleColormap(parseHexString(string.substring(1)));
			}
		}
		throw new ConfigLoadException("Could not parse colormap: " + element);
	}
	
	@SuppressWarnings("serial")
	public static class ConfigLoadException extends Exception {
		
		public ConfigLoadException(String message) {
			super(message);
		}
		
		public ConfigLoadException(String message, Exception e) {
			super(message, e);
		}
		
	}
	
	public static int parseHexString(String string) {
		int color = 0;
		for(int i = string.length() - 1; i >= 0; i--) {
			char c = string.charAt(i);
			int charValue;
			if(c >= '0' && c <= '9') {
				charValue = c - '0';
			}else if(c >= 'a' && c <= 'f') {
				charValue = c - 'a' + 10;
			}else if(c >= 'A' && c <= 'F') {
				charValue = c - 'A' + 10;
			}else {
				throw new NumberFormatException("Invalid character '"+c+"' at index "+i+"!");
			}
			color |= charValue << ((string.length() - i - 1) << 2);
		}
		return color;
	}

}
