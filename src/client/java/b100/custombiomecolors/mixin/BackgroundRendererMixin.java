package b100.custombiomecolors.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.sugar.Local;

import b100.custombiomecolors.CustomBiomeColorsMod;
import b100.custombiomecolors.colormap.Colormap;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.CubicSampler.RgbFetcher;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;

@Mixin(value = BackgroundRenderer.class)
public class BackgroundRendererMixin {

	@ModifyArg(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/CubicSampler;sampleColor(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/CubicSampler$RgbFetcher;)Lnet/minecraft/util/math/Vec3d;"
			),
			index = 1
	)
	private static RgbFetcher customFogColor(Vec3d pos, RgbFetcher rgbFetcher, @Local(argsOnly = true) ClientWorld world) {
		return (x, y, z) -> {
			BiomeAccess biomeAccess = world.getBiomeAccess();
			RegistryEntry<Biome> biome = biomeAccess.getBiomeForNoiseGen(x, y, z);
			Colormap colormap = CustomBiomeColorsMod.FOG.getColormap(biome);
			if(colormap != null) {
				return Vec3d.unpackRgb(colormap.getColor(x, z));
			}
			return rgbFetcher.fetch(x, y, z);
		};
	}

}
