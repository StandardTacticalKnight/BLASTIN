package standardtacticalknight.blastin.mixin;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemHandCannonLoaded;
import net.minecraft.core.item.ItemHandCannonUnloaded;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import standardtacticalknight.blastin.Blastin;

@Mixin(value = ItemHandCannonUnloaded.class, remap = false)
public class BlastinItemHandCannonUnloadedMixin {
	@Inject(method = "onUseItem", at =  @At("HEAD"), cancellable = true)
	private void inject(ItemStack itemstack, World world, EntityPlayer entityplayer, CallbackInfoReturnable<ItemStack> cir){
		if (entityplayer.inventory.consumeInventoryItem(Blastin.ammoChargeExplosive.id)) {
			world.playSoundAtEntity(entityplayer, entityplayer, "random.click", 1.0f, 1.9f / (world.rand.nextFloat() * 0.2f + 0.4f));
			cir.setReturnValue( new ItemStack(Blastin.itemHandCannonBlastLoaded, 1, itemstack.getMetadata(), itemstack.getData()));
		}
		//cir.setReturnValue(itemstack);
	}
}
