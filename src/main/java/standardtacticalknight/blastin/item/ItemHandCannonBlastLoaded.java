package standardtacticalknight.blastin.item;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.entity.projectile.EntityCannonball;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemHandCannonLoaded;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import standardtacticalknight.blastin.entity.EntityBlastingCannonball;

public class ItemHandCannonBlastLoaded extends ItemHandCannonLoaded {
	public ItemHandCannonBlastLoaded(String name, int id) {
		super(name, id);
	}
	@Override
	public ItemStack onUseItem(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		world.playSoundAtEntity(entityplayer, entityplayer, "random.bow", 0.3f, 1.0f / (itemRand.nextFloat() * -0.2f + -0.4f));
		if (!world.isClientSide) {
			itemstack.damageItem(1, entityplayer);
			world.entityJoinedWorld(new EntityBlastingCannonball(world, entityplayer));
			if (itemstack.stackSize <= 0) {
				return null;
			}
			return new ItemStack(Item.handcannonUnloaded, 1, itemstack.getMetadata(), itemstack.getData());
		}
		return itemstack;
	}
}
