package standardtacticalknight.blastin.item;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;

public class ItemAmmo extends Item {
	public ItemAmmo(String name, String namespaceId, int id) {
		super(name, namespaceId, id);
		//this.setHasSubtypes(true);
		//this.setMaxDamage(0);
	}

	@Override
	public String getLanguageKey(ItemStack itemstack) {
		return  "item.blastin.ammo.charge.blasting";
	}
}
