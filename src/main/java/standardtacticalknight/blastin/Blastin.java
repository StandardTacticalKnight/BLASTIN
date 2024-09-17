package standardtacticalknight.blastin;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.item.model.ItemModelDoorPainted;
import net.minecraft.client.render.item.model.ItemModelDye;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tag.ItemTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import standardtacticalknight.blastin.block.BlockBreachingCharge;
import standardtacticalknight.blastin.block.BlockLandMine;
import standardtacticalknight.blastin.block.model.BlockModelBreachingCharge;
import standardtacticalknight.blastin.block.model.BlockModelLandMine;
import standardtacticalknight.blastin.entity.TileEntityLandMine;
import standardtacticalknight.blastin.item.ItemHandCannonBlastLoaded;
import standardtacticalknight.blastin.item.model.ItemModelBlastBall;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;


public class Blastin implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "blastin";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Block breachingCharge;
	public static Block landMine;
	public static Block incendiaryMine;
	public static Block spiderMine;
	public static Item ammoChargeExplosive;
	public static Item itemHandCannonBlastLoaded;
    @Override
    public void onInitialize() {
        LOGGER.info("Blastin initialized.");
    }

	@Override
	public void beforeGameStart() {
		int startingBlockId = 2750;
		int itemID = 18775;

		EntityHelper.createTileEntity(TileEntityLandMine.class, "LandMineTile");

		ammoChargeExplosive = new ItemBuilder(MOD_ID)
			.setIcon("minecraft:item/ammo_charge_explosive")
			.setItemModel(ItemModelBlastBall::new)
			.setStackSize(16)
			.build(new Item("ammo.charge.blasting", itemID++));
		itemHandCannonBlastLoaded = new ItemBuilder(MOD_ID)
			.addTags(ItemTags.NOT_IN_CREATIVE_MENU)
			.setIcon("minecraft:item/handcannon_loaded")
			.build(new ItemHandCannonBlastLoaded("handcannon.blasting", itemID++));


		breachingCharge = new BlockBuilder(MOD_ID)
			.setTextures("minecraft:block/slate_side")
			.setBlockModel(BlockModelBreachingCharge::new)
			.build(new BlockBreachingCharge("breachingcharge",startingBlockId++));
		landMine = new BlockBuilder(MOD_ID)
			.setTextures("minecraft:block/slate_side")
			.setBlockModel(block1 -> new BlockModelLandMine<>(block1, BlockModelLandMine.Type.NORMAL))
			.build(new BlockLandMine("landmine",startingBlockId++));
		incendiaryMine = new BlockBuilder(MOD_ID)
			.setTextures("minecraft:block/slate_side")
			.setBlockModel(block -> new BlockModelLandMine<>(block,BlockModelLandMine.Type.FIRE))
			.build(new BlockLandMine("incendiarymine",startingBlockId++));
		spiderMine = new BlockBuilder(MOD_ID)
			.setTextures("minecraft:block/slate_side")
			.setBlockModel(block -> new BlockModelLandMine<>(block, BlockModelLandMine.Type.WEB))
			.build(new BlockLandMine("spidermine",startingBlockId++));
	}

	@Override
	public void afterGameStart() {

	}

	@Override
	public void onRecipesReady() {
		RecipeBuilder.Shaped(MOD_ID)
			.setShape("STS", "SBS", " S ")
			.addInput('T', Block.tnt)
			.addInput('B', Block.obsidian)
			.addInput('S', Block.layerSlate)
			.create("toBreachingCharge", breachingCharge.getDefaultStack());
		RecipeBuilder.Shaped(MOD_ID)
			.setShape(" S ", "BTB", " B ")
			.addInput('T', Block.tnt)
			.addInput('B', Item.ingotIron)
			.addInput('S', Item.string)
			.create("toBlasingBall", new ItemStack(ammoChargeExplosive,2));
	}

	@Override
	public void initNamespaces() {
		RecipeBuilder.initNameSpace(MOD_ID);
	}
}
