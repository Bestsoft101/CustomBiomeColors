package b100.custombiomecolors.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import b100.custombiomecolors.CustomBiomeColorsMod;
import b100.custombiomecolors.colorizer.Colorizer;
import b100.custombiomecolors.colormap.Colormap;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.ColorResolver;

@Mixin(BiomeColors.class)
public class BiomeColorsMixin {

	@Mutable @Shadow public static ColorResolver GRASS_COLOR;
	@Mutable @Shadow public static ColorResolver FOLIAGE_COLOR;
	@Mutable @Shadow public static ColorResolver WATER_COLOR;
	
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void onInit(CallbackInfo ci) {
		GRASS_COLOR = setupOverride(GRASS_COLOR, CustomBiomeColorsMod.GRASS);
		FOLIAGE_COLOR = setupOverride(FOLIAGE_COLOR, CustomBiomeColorsMod.FOLIAGE);
		WATER_COLOR = setupOverride(WATER_COLOR, CustomBiomeColorsMod.WATER);
	}
	
	private static ColorResolver setupOverride(final ColorResolver previous, final Colorizer colorizer) {
		return (biome, x, z) -> {
			Colormap colormap = colorizer.getColormap(biome);
			if(colormap != null) {
				int blockX = MathHelper.floor(x);
				int blockZ = MathHelper.floor(z);
				return colormap.getColor(blockX, blockZ);
			}
			return previous.getColor(biome, x, z);
		};
	}
}
