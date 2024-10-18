package b100.custombiomecolors.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import b100.custombiomecolors.CustomBiomeColorsMod;
import b100.custombiomecolors.colormap.Colormap;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.CubicSampler.RgbFetcher;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;

@Mixin(value = ClientWorld.class, priority = 999)
public abstract class ClientWorldMixin extends World {

	/**
	 * do not
	 */
	protected ClientWorldMixin() {
		super(null, null, null, null, null, false, false, 0, 0);
	}

	@ModifyArg(
			method = "getSkyColor",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/CubicSampler;sampleColor(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/CubicSampler$RgbFetcher;)Lnet/minecraft/util/math/Vec3d;"
			),
			index = 1
	)
	private RgbFetcher customSkyColor(Vec3d pos, RgbFetcher rgbFetcher) {
		return (x, y, z) -> {
			BiomeAccess biomeAccess = getBiomeAccess();
			RegistryEntry<Biome> biome = biomeAccess.getBiomeForNoiseGen(x, y, z);
			Colormap colormap = CustomBiomeColorsMod.SKY.getColormap(biome);
			if(colormap != null) {
				return Vec3d.unpackRgb(colormap.getColor(x, z));
			}
			return rgbFetcher.fetch(x, y, z);
		};
	}

}
