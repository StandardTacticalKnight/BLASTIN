package standardtacticalknight.blastin;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import standardtacticalknight.blastin.block.BlockBreachingCharge;
import standardtacticalknight.blastin.block.BlockLandMine;
import standardtacticalknight.blastin.block.model.BlockModelBreachingCharge;
import standardtacticalknight.blastin.block.model.BlockModelLandMine;
import standardtacticalknight.blastin.entity.TileEntityLandMine;
import standardtacticalknight.blastin.item.ItemAmmo;
import standardtacticalknight.blastin.item.ItemHandCannonBlastLoaded;
import standardtacticalknight.blastin.item.model.ItemModelBlastBall;
import turniplabs.halplibe.helper.*;
import turniplabs.halplibe.util.DirectoryManager;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.ModelEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;



public class Blastin implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint, ModelEntrypoint {
    public static final String MOD_ID = "blastin";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Block<BlockBreachingCharge> breachingCharge;
	public static Block<BlockLandMine> landMine;
	public static Block<BlockLandMine> incendiaryMine;
	public static Block<BlockLandMine> spiderMine;
	public static Item ammoChargeExplosive;
	public static Item itemHandCannonBlastLoaded;
    @Override
    public void onInitialize() {
		int startingBlockId = 2750;
		int itemID = 18775;

		EntityHelper.createTileEntity(TileEntityLandMine.class, NamespaceID.getPermanent(MOD_ID,"LandMineTile"));

		ammoChargeExplosive = new ItemBuilder(MOD_ID)
			//.addTags(ItemTags.NOT_IN_CREATIVE_MENU)
			//.setIcon("minecraft:item/ammo_charge_explosive")
			//.setItemModel(ItemModelBlastBall::new)
			.setStackSize(16)
			.setKey(MOD_ID+":blasting_balls")
			//.clone()
			.build(new ItemAmmo("ammo.charge.blasting",MOD_ID+":blasting_balls", itemID++));//FIXME: This is a hack, find out why name no worky

		itemHandCannonBlastLoaded = new ItemBuilder(MOD_ID)
			.addTags(ItemTags.NOT_IN_CREATIVE_MENU)
			.setKey("minecraft:item/handcannon_loaded")
			//.setKey(MOD_ID+":handcannon_loaded")
			.build(new ItemHandCannonBlastLoaded("handcannon.blasting",MOD_ID+":handcannon_loaded_blasting", itemID++));

		breachingCharge = new BlockBuilder(MOD_ID)
			.build("breachingcharge",MOD_ID+"breachingcharge",startingBlockId++, BlockBreachingCharge::new);
		landMine = new BlockBuilder(MOD_ID)
			.build("landmine",MOD_ID+"landmine",startingBlockId++,BlockLandMine::new);
		incendiaryMine = new BlockBuilder(MOD_ID)
			.build("incendiarymine",MOD_ID+"incendiarymine",startingBlockId++, BlockLandMine::new);
		spiderMine = new BlockBuilder(MOD_ID)
			.build("spidermine",MOD_ID+"spidermine",startingBlockId++,BlockLandMine::new);


        LOGGER.info("Blastin initialized.");
    }

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {

	}

	@Override
	public void onRecipesReady() {
		RecipeBuilder.Shaped(MOD_ID)
			.setShape("STS", "SBS", " S ")
			.addInput('T', Blocks.TNT)
			.addInput('B', Blocks.OBSIDIAN)
			.addInput('S', Blocks.LAYER_SLATE)
			.create("toBreachingCharge", breachingCharge.getDefaultStack());
		RecipeBuilder.Shaped(MOD_ID)
			.setShape(" S ", "BTB", " B ")
			.addInput('T', Blocks.TNT)
			.addInput('B', Items.INGOT_IRON)
			.addInput('S', Items.STRING)
			.create("toBlasingBall", new ItemStack(ammoChargeExplosive,2));
	}

	@Override
	public void initNamespaces() {
		DirectoryManager.registerKey(MOD_ID);
		DirectoryManager.refreshDirectories();
		RecipeBuilder.initNameSpace(MOD_ID);
	}

	@Override
	public void initBlockModels(BlockModelDispatcher dispatcher) {
		try {
			ModelHelper.setBlockModel(Blastin.breachingCharge, () -> new BlockModelBreachingCharge<>(Blastin.breachingCharge)
				.setAllTextures(0, "minecraft:block/slate_side")
			);
			ModelHelper.setBlockModel(Blastin.landMine, () -> new BlockModelLandMine<>(Blastin.landMine, BlockModelLandMine.Type.NORMAL)
				.setAllTextures(0, "minecraft:block/slate_side")
			);
			ModelHelper.setBlockModel(Blastin.incendiaryMine, () -> new BlockModelLandMine<>(Blastin.landMine, BlockModelLandMine.Type.FIRE)
				.setAllTextures(0, "minecraft:block/slate_side")
			);
			ModelHelper.setBlockModel(Blastin.spiderMine, () -> new BlockModelLandMine<>(Blastin.landMine, BlockModelLandMine.Type.WEB)
				.setAllTextures(0, "minecraft:block/slate_side")
			);
		} catch (Exception e) {

			Blastin.LOGGER.error("Block Models failed to initialize.", e);
		}
		Blastin.LOGGER.info("Block Models initialized.");
	}

	@Override
	public void initItemModels(ItemModelDispatcher dispatcher) {
		ModelHelper.setItemModel(Blastin.ammoChargeExplosive, () -> new ItemModelBlastBall(Blastin.ammoChargeExplosive));
		ModelHelper.setItemModel(Blastin.itemHandCannonBlastLoaded, () -> new ItemModelStandard(Items.HANDCANNON_LOADED,"minecraft"));
	}

	@Override
	public void initEntityModels(EntityRenderDispatcher dispatcher) {

	}

	@Override
	public void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {

	}

	@Override
	public void initBlockColors(BlockColorDispatcher dispatcher) {

	}
}
