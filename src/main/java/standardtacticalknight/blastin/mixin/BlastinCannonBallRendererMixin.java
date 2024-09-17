package standardtacticalknight.blastin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.CannonballRenderer;
import net.minecraft.core.entity.projectile.EntityCannonball;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import standardtacticalknight.blastin.entity.EntityBlastingCannonball;

@Mixin(value = CannonballRenderer.class, remap = false)


public class BlastinCannonBallRendererMixin {
	@Inject(method = "RenderCannonball", at =  @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/CannonballRenderer;loadTexture(Ljava/lang/String;)V"))
	private void injectString(EntityCannonball entity, double d, double d1, double d2, float f, CallbackInfo ci, @Local(argsOnly = true)EntityCannonball ent) {
		if(ent instanceof EntityBlastingCannonball){
			GL11.glColor4f(1.0f,0.2f,0.2f,1.0f);//Tint the cannonball texture red
		}
	}
}
