package standardtacticalknight.blastin.item.model;

import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;

public class ItemModelBlastBall extends ItemModelStandard {
	public ItemModelBlastBall(Item item) {
		super(item,null);
		this.icon = TextureRegistry.getTexture("minecraft:item/ammo_charge_explosive");
	}

	@Override
	public int getColor(ItemStack stack) {
		return this.getColorFromMeta(stack.getMetadata());
	}

	@Override
	public int getColorFromMeta(int meta) {
		return 0xFF4444;
	}
}
