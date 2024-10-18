package b100.custombiomecolors.mixin.sodium;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.bawnorton.mixinsquared.TargetHandler;

import b100.custombiomecolors.CustomBiomeColorsMod;
import b100.custombiomecolors.colormap.Colormap;
import net.caffeinemc.mods.sodium.client.util.color.FastCubicSampler.ColorFetcher;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;

@Mixin(value = ClientWorld.class, priority = 1001)
public abstract class ClientLevelMixinMixin extends World {

	/**
	 * do not
	 */
	protected ClientLevelMixinMixin() {
		super(null, null, null, null, null, false, false, 0, 0);
	}

	@TargetHandler(
			mixin = "net.caffeinemc.mods.sodium.mixin.features.render.world.sky.ClientLevelMixin",
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
	private ColorFetcher customSkyColorSodium(Vec3d pos, ColorFetcher rgbFetcher, Function<?, ?> someFunction) {
		return (x, y, z) -> {
			@SuppressWarnings("resource")
			ClientWorld clientWorld = (ClientWorld)(Object)this;
			BiomeAccess biomeAccess = clientWorld.getBiomeAccess();
			RegistryEntry<Biome> biome = biomeAccess.getBiomeForNoiseGen(x, y, z);
			Colormap colormap = CustomBiomeColorsMod.SKY.getColormap(biome);
			if(colormap != null) {
				return colormap.getColor(x, z);
			}
			return rgbFetcher.fetch(x, y, z);
		};
	}
	
}
