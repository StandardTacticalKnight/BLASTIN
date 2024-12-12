package standardtacticalknight.blastin.block.model;

import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.Side;
import standardtacticalknight.blastin.util.BBTex;

public class BlockModelLandMine<T extends BlockLogic> extends BlockModelBreachingCharge<T>{
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
		this.primedTexture = TextureRegistry.getTexture("minecraft:block/motion_sensor/active_front");
		list.add(new BBTex(0.4375D,0.4375D,0.0625D));
		list.add(new BBTex(0.0625D,0.4375D,0.1875D));
		list.add(new BBTex(0.4375D,0.0625D,0.1875D));
		if(type == Type.FIRE){
			list.add(new BBTex(0.4374D,0.4374D,0.0626D, TextureRegistry.getTexture("minecraft:block/cobbled_netherrack_igneous_overlay")));
		}else if(type == Type.WEB){
			list.add(new BBTex(0.4374D,0.4374D,0.1874D,TextureRegistry.getTexture("minecraft:block/cobweb")));
		}
		list.add(new BBTex(0.3125D,0.3125,0.125D,BlockModelDispatcher.getInstance().getDispatch(Blocks.TNT).getBlockTextureFromSideAndMetadata(Side.SOUTH, 0)));
		list.add(new BBTex(0.25D,0.25D,0.25D,TextureRegistry.getTexture("minecraft:block/motion_sensor/idle_front")));
	}
}
