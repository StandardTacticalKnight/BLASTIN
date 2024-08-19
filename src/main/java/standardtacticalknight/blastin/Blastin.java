package standardtacticalknight.blastin;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import standardtacticalknight.blastin.block.BlockBreachingCharge;
import standardtacticalknight.blastin.block.model.BlockModelBreachingCharge;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;


public class Blastin implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "blastin";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Block breachingCharge;
    @Override
    public void onInitialize() {
        LOGGER.info("Blastin initialized.");
    }

	@Override
	public void beforeGameStart() {
		int startingBlockId = 2750;
		int itemID = 18775;
		breachingCharge = new BlockBuilder(MOD_ID)
			.setTextures("minecraft:block/slate_side")
			.setBlockModel(BlockModelBreachingCharge::new)
			//.setIcon("blastin:item/breachingcharge")
			.build(new BlockBreachingCharge("breachingcharge",startingBlockId++));

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
	}

	@Override
	public void initNamespaces() {
		RecipeBuilder.initNameSpace(MOD_ID);
	}
}
