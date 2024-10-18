package b100.custombiomecolors.mixin.sodium;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.sugar.Local;

import b100.custombiomecolors.CustomBiomeColorsMod;
import b100.custombiomecolors.colormap.Colormap;
import net.caffeinemc.mods.sodium.client.util.color.FastCubicSampler.ColorFetcher;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;

@Mixin(value = BackgroundRenderer.class, priority = 1001)
public class FogRendererMixinMixin {

	@TargetHandler(
			mixin = "net.caffeinemc.mods.sodium.mixin.features.render.world.sky.FogRendererMixin",
			name = "redirectSampleColor"
	)
	@ModifyArg(
			method = "@MixinSquared:Handler",
			at = @At(
					value = "INVOKE",
					target = "Lnet/caffeinemc/mods/sodium/client/util/color/FastCubicSampler;sampleColor(Lnet/minecraft/util/math/Vec3d;Lnet/caffeinemc/mods/sodium/client/util/color/FastCubicSampler$ColorFetcher;Ljava/util/function/Function;)Lnet/minecraft/util/math/Vec3d;"
			),
			index = 1
	)
	private static ColorFetcher customFogColorSodium(Vec3d pos, ColorFetcher rgbFetcher, Function<?, ?> someFunction, @Local(argsOnly = true) ClientWorld world) {
		return (x, y, z) -> {
			BiomeAccess biomeAccess = world.getBiomeAccess();
			RegistryEntry<Biome> biome = biomeAccess.getBiomeForNoiseGen(x, y, z);
			Colormap colormap = CustomBiomeColorsMod.FOG.getColormap(biome);
			if(colormap != null) {
				return colormap.getColor(x, z);
			}
			return rgbFetcher.fetch(x, y, z);
		};
	}

}
