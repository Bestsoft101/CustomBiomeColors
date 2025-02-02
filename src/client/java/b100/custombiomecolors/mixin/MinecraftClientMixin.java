package b100.custombiomecolors.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import b100.custombiomecolors.CustomBiomeColorsMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	
	@Inject(method = "<init>", at = @At("TAIL"))
	public void onInit(RunArgs args, CallbackInfo ci) {
		CustomBiomeColorsMod.onInit();
	}
	
}
