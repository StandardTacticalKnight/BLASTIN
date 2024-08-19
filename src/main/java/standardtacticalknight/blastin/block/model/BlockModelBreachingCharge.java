package standardtacticalknight.blastin.block.model;

import net.minecraft.client.render.block.color.BlockColor;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.stitcher.IconCoordinate;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class BlockModelBreachingCharge<T extends Block> extends BlockModelStandard<T> {
	public BlockModelBreachingCharge(Block block) {
		super(block);
	}
	public boolean render(Tessellator tessellator, int x, int y, int z) {
		this.block.setBlockBoundsBasedOnState(renderBlocks.blockAccess, x, y, z);
		int blockMetadata = renderBlocks.blockAccess.getBlockMetadata(x, y, z);
		int blockFace = blockMetadata & 15;        //first 4 bits of meta contains block facing direction
		boolean primed = (blockMetadata & 16) > 0; //5th bit contains activation state
		boolean canOverrideTextures = renderBlocks.overrideBlockTexture == null; //not sure why this check is needed but it's in the vanilla code
		//draw slate faces
		calculateShape(0.5D,0.5D,0.0625D, blockFace);
		this.renderStandardBlock(tessellator, this.block, x, y, z);
		calculateShape(0.0625D,0.4375D,0.1875D, blockFace);
		this.renderStandardBlock(tessellator, this.block, x, y, z);
		calculateShape(0.4375D,0.0625D,0.1875D, blockFace);
		this.renderStandardBlock(tessellator, this.block, x, y, z);
		//draw tnt face
		if (canOverrideTextures) {
			renderBlocks.overrideBlockTexture = BlockModelDispatcher.getInstance().getDispatch(Block.tnt).getBlockTextureFromSideAndMetadata(Side.SOUTH, blockMetadata);
		}
		calculateShape(0.375D,0.375D,0.125D, blockFace);
		this.renderStandardBlock(tessellator, this.block, x, y, z);
		//draw fuse face
		if (canOverrideTextures) {
			if(primed){
				renderBlocks.overrideBlockTexture = BlockModelDispatcher.getInstance().getDispatch(Block.netherrackIgneous).getBlockTextureFromSideAndMetadata(Side.SOUTH, blockMetadata);
			}else renderBlocks.overrideBlockTexture = BlockModelDispatcher.getInstance().getDispatch(Block.obsidian).getBlockTextureFromSideAndMetadata(Side.SOUTH, blockMetadata);
		}
		calculateShape(0.1875D,0.1875D,0.250D, blockFace);
		this.renderStandardBlock(tessellator, this.block, x, y, z);
		//reset, is this needed?
		if (canOverrideTextures) {
			renderBlocks.overrideBlockTexture = null;
		}
		return true;
	}
	@Override
	public void renderBlockOnInventory(Tessellator tessellator, int metadata, float brightness, float alpha, @Nullable Integer lightmapCoordinate) {
		if (BlockModelStandard.renderBlocks.useInventoryTint) {
			int color = ((BlockColor) BlockColorDispatcher.getInstance().getDispatch(this.block)).getFallbackColor(metadata);
			float r = (float)(color >> 16 & 0xFF) / 255.0f;
			float g = (float)(color >> 8 & 0xFF) / 255.0f;
			float b = (float)(color & 0xFF) / 255.0f;
			GL11.glColor4f(r * brightness, g * brightness, b * brightness, alpha);
		} else {
			GL11.glColor4f(brightness, brightness, brightness, alpha);
		}
		GL11.glTranslatef(-0.5f, 0.0f, -0.5f);
		IconCoordinate iconCoordinate = this.getBlockTextureFromSideAndMetadata(Side.BOTTOM, metadata);
		for (int cube = 0; cube < 5; ++cube) {
			if (cube == 0) {
				calculateShape(0.5D,0.5D,0.0625D, 5);
			}
			if (cube == 1) {
				calculateShape(0.0625D,0.4375D,0.1875D, 5);
			}
			if (cube == 2) {
				calculateShape(0.4375D,0.0625D,0.1875D, 5);
			}
			if (cube == 3) {
				calculateShape(0.375D,0.375D,0.125D, 5);
				iconCoordinate = BlockModelDispatcher.getInstance().getDispatch(Block.tnt).getBlockTextureFromSideAndMetadata(Side.SOUTH, metadata);
			}
			if (cube == 4) {
				calculateShape(0.1875D,0.1875D,0.250D, 5);
				iconCoordinate = BlockModelDispatcher.getInstance().getDispatch(Block.obsidian).getBlockTextureFromSideAndMetadata(Side.SOUTH, metadata);
			}

			//draw each face of the quad
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0f, -1.0f, 0.0f);
			this.renderBottomFace(tessellator, this.block, 0.0, 0.0, 0.0, iconCoordinate);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0f, 1.0f, 0.0f);
			this.renderTopFace(tessellator, this.block, 0.0, 0.0, 0.0, iconCoordinate);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0f, 0.0f, -1.0f);
			this.renderNorthFace(tessellator, this.block, 0.0, 0.0, 0.0, iconCoordinate);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0f, 0.0f, 1.0f);
			this.renderSouthFace(tessellator, this.block, 0.0, 0.0, 0.0, iconCoordinate);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(-1.0f, 0.0f, 0.0f);
			this.renderWestFace(tessellator, this.block, 0.0, 0.0, 0.0, iconCoordinate);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(1.0f, 0.0f, 0.0f);
			this.renderEastFace(tessellator, this.block, 0.0, 0.0, 0.0, iconCoordinate);
			tessellator.draw();
		}
		GL11.glTranslatef(0.5f, 0.0f, 0.5f);
		this.block.setBlockBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
	}
	public boolean shouldItemRender3d() {
		return true;
	}

	public float getItemRenderScale() {
		return 0.5F;
	}

	/**
	 * Draw cuboid whose origin is centered on the middle of a given face
	 */
	private void calculateShape(double height, double width, double depth, int face){
		if (face == 7) {
			this.block.setBlockBounds(0.5D - width, 1.0D - depth, 0.5D - height, 0.5D + width, 1.0, 0.5D + height);
		} else if (face == 8) {
			this.block.setBlockBounds(0.5D - height, 1.0D - depth, 0.5D - width, 0.5D + height, 1.0, 0.5D + width);
		} else if (face == 5) {
			this.block.setBlockBounds(0.5D - width, 0.0, 0.5D - height, 0.5D + width, depth, 0.5D + height);
		} else if (face == 6) {
			this.block.setBlockBounds(0.5D - height, 0.0, 0.5D - width, 0.5D + height, depth, 0.5D + width);
		} else if (face == 4) {
			this.block.setBlockBounds(0.5D - width, 0.5D - height, 1.0D - depth, 0.5D + width, 0.5D + height, 1.0);
		} else if (face == 3) {
			this.block.setBlockBounds(0.5D - width, 0.5D - height, 0.0, 0.5D + width, 0.5D + height, depth);
		} else if (face == 2) {
			this.block.setBlockBounds(1.0D - depth, 0.5D - height, 0.5D - width, 1.0, 0.5D + height, 0.5D + width);
		} else if (face == 1) {
			this.block.setBlockBounds(0.0, 0.5D - height, 0.5D - width, depth, 0.5D + height, 0.5D + width);
		}
	}
}
