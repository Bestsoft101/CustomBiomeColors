package b100.custombiomecolors;

import static b100.custombiomecolors.CustomBiomeColorsMod.*;

import java.util.List;

import b100.custombiomecolors.colorizer.Colorizer;
import b100.custombiomecolors.colormap.Colormap;
import b100.custombiomecolors.colormap.SimpleColormap;
import b100.json.element.JsonElement;
import b100.json.element.JsonEntry;
import b100.json.element.JsonObject;

public class ConfigLoader {
	
	public JsonObject root;
	
	private boolean fileContainsInvalidColorizer = false;
	
	public ConfigLoader(JsonObject root) throws ConfigLoadException {
		this.root = root;

		debugPrint("Loading " + CONFIG_FILE_NAME);
		
		loadColorizers();
		
		if(fileContainsInvalidColorizer) {
			debugPrint("Available Colorizers: ");
			for(Colorizer colorizer : allColorizers) {
				debugPrint("  " + colorizer.getName());
			}
		}
	}
	
	private void loadColorizers() throws ConfigLoadException {
		JsonObject colorizers = root.getObject("colorizer");
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
			
			String biomeName = entry.name;
			
			debugPrint("    Biome: " + biomeName);
			
			Colormap colormap = parseColormap(entry.value);
			
			colorizer.put(biomeName, colormap);
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
