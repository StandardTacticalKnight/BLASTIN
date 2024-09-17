package standardtacticalknight.blastin.item.model;

import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;

public class ItemModelBlastBall extends ItemModelStandard {
	public ItemModelBlastBall(Item item) {
		super(item,null);
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
