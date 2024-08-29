package standardtacticalknight.blastin.block.model;

import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;

public class BlockModelLandMine<T extends Block> extends BlockModelBreachingCharge<T>{
	public BlockModelLandMine(Block block) {
		super(block);
	}
	@Override
	public boolean render(Tessellator tessellator, int x, int y, int z) {
		this.block.setBlockBoundsBasedOnState(renderBlocks.blockAccess, x, y, z);
		int blockMetadata = renderBlocks.blockAccess.getBlockMetadata(x, y, z);
		int blockFace = blockMetadata & 15;        //first 4 bits of meta contains block facing direction
		boolean primed = (blockMetadata & 16) > 0; //5th bit contains activation state
		boolean canOverrideTextures = renderBlocks.overrideBlockTexture == null; //not sure why this check is needed but it's in the vanilla code
		//draw slate faces
		calculateShape(0.375D,0.375D,0.0625D, blockFace);
		this.renderStandardBlock(tessellator, this.block, x, y, z);
//		calculateShape(0.0625D,0.4375D,0.1875D, blockFace);
//		this.renderStandardBlock(tessellator, this.block, x, y, z);
//		calculateShape(0.4375D,0.0625D,0.1875D, blockFace);
//		this.renderStandardBlock(tessellator, this.block, x, y, z);
		//draw tnt face
		if (canOverrideTextures) {
			renderBlocks.overrideBlockTexture = BlockModelDispatcher.getInstance().getDispatch(Block.tnt).getBlockTextureFromSideAndMetadata(Side.SOUTH, blockMetadata);
		}
		calculateShape(0.3125D,0.3125,0.125D, blockFace);
		this.renderStandardBlock(tessellator, this.block, x, y, z);
		//draw fuse face
		if (canOverrideTextures) {
			if(primed){
				renderBlocks.overrideBlockTexture = BlockModelDispatcher.getInstance().getDispatch(Block.motionsensorActive).getBlockTextureFromSideAndMetadata(Side.SOUTH, 0);
			}else renderBlocks.overrideBlockTexture = BlockModelDispatcher.getInstance().getDispatch(Block.motionsensorIdle).getBlockTextureFromSideAndMetadata(Side.SOUTH, 0);
		}
		calculateShape(0.25D,0.25D,0.1875D, blockFace);
		this.renderStandardBlock(tessellator, this.block, x, y, z);
		//reset, is this needed?
		if (canOverrideTextures) {
			renderBlocks.overrideBlockTexture = null;
		}
		return true;
	}
}
