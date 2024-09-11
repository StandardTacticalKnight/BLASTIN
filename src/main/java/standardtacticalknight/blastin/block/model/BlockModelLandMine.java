package standardtacticalknight.blastin.block.model;

import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import standardtacticalknight.blastin.util.BBTex;

public class BlockModelLandMine<T extends Block> extends BlockModelBreachingCharge<T>{
	public enum Type {NORMAL,FIRE,WEB}
	public static Type type;

	public BlockModelLandMine(Block block, Type type) {
		super(getBlock(block,type));
    }
	public static Block getBlock(Block block, Type mineType) {
		type = mineType;
		return block;
	}
	@Override
	void setBoundList(){
		this.primedTexture = BlockModelDispatcher.getInstance().getDispatch(Block.motionsensorActive).getBlockTextureFromSideAndMetadata(Side.SOUTH, 0);
		//list.add(new BBTex(0.375D,0.375D,0.0625D));
		list.add(new BBTex(0.4375D,0.4375D,0.0625D));
		list.add(new BBTex(0.0625D,0.4375D,0.1875D));
		list.add(new BBTex(0.4375D,0.0625D,0.1875D));
		//Blastin.LOGGER.info(String.valueOf(type));
		if(type == Type.FIRE){
			list.add(new BBTex(0.4374D,0.4374D,0.0626D,TextureRegistry.getTexture("minecraft:block/netherrack_igneous")));
		}else if(type == Type.WEB){
			list.add(new BBTex(0.4374D,0.4374D,0.1874D,TextureRegistry.getTexture("minecraft:block/cobweb")));
		}
		list.add(new BBTex(0.3125D,0.3125,0.125D,BlockModelDispatcher.getInstance().getDispatch(Block.tnt).getBlockTextureFromSideAndMetadata(Side.SOUTH, 0)));
		list.add(new BBTex(0.25D,0.25D,0.25D,BlockModelDispatcher.getInstance().getDispatch(Block.motionsensorIdle).getBlockTextureFromSideAndMetadata(Side.SOUTH, 0)));
	}
}
