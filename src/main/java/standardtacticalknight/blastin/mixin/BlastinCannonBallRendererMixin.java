package standardtacticalknight.blastin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.CannonballRenderer;
import net.minecraft.core.entity.projectile.EntityCannonball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import standardtacticalknight.blastin.entity.EntityBlastingCannonball;

@Mixin(value = CannonballRenderer.class, remap = false)


public class BlastinCannonBallRendererMixin {
	@ModifyArg(method = "RenderCannonball", at =  @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/CannonballRenderer;loadTexture(Ljava/lang/String;)V"))
	private String injectString(String par1, @Local(argsOnly = true)EntityCannonball ent) {
		if(ent instanceof EntityBlastingCannonball) return "/assets/blastin/textures/entity/blasting_cannonball.png";
		return par1;
	}
}
