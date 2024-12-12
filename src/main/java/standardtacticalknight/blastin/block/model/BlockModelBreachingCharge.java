package standardtacticalknight.blastin.block.model;

import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import standardtacticalknight.blastin.util.BBTex;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BlockModelBreachingCharge<T extends BlockLogic> extends BlockModelStandard<T> {
	List<BBTex> list = new ArrayList<>();
	AABB bounds = AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
	IconCoordinate primedTexture;

	public BlockModelBreachingCharge(Block block) {
		super(block);
		setBoundList();//setup all of the quads needed to draw shape
	}
	@Override
	public boolean render(Tessellator tessellator, int x, int y, int z) {
		bounds = this.block.getBlockBoundsFromState(renderBlocks.blockAccess, x, y, z);//////////TODO
		int blockMetadata = renderBlocks.blockAccess.getBlockMetadata(x, y, z);
		int blockFace = blockMetadata & 15;        //first 4 bits of meta contains block facing direction
		boolean primed = (blockMetadata & 16) > 0; //5th bit contains activation state
		boolean canOverrideTextures = renderBlocks.overrideBlockTexture == null; //not sure why this check is needed but it's in the vanilla code

		for (int i = 0; i < list.size()-1; i++) {//loop over the quads and draw them all, except the last one where the primed texture gets calculated
			BBTex temp = list.get(i);
			calculateShape(temp.height,temp.width,temp.depth, blockFace);
			renderBlocks.overrideBlockTexture = canOverrideTextures ? list.get(i).texture : null;
			this.renderStandardBlock(tessellator, bounds, x, y, z);
		}

		BBTex temp = list.get(list.size()-1);//last in list
		calculateShape(temp.height,temp.width,temp.depth, blockFace);
		//if charge is primed use primed texture
		renderBlocks.overrideBlockTexture = primed ? this.primedTexture : list.get(list.size()-1).texture;
		this.renderStandardBlock(tessellator, bounds, x, y, z);

		renderBlocks.overrideBlockTexture = null;//reset, is this needed?
		return true;
	}
	@Override
	public void renderBlockOnInventory(Tessellator tessellator, int metadata, float brightness, float alpha, @Nullable Integer lightmapCoordinate) {
		if (BlockModelStandard.renderBlocks.useInventoryTint) {
			int color = BlockColorDispatcher.getInstance().getDispatch(this.block).getFallbackColor(metadata);
			float r = (float)(color >> 16 & 0xFF) / 255.0f;
			float g = (float)(color >> 8 & 0xFF) / 255.0f;
			float b = (float)(color & 0xFF) / 255.0f;
			GL11.glColor4f(r * brightness, g * brightness, b * brightness, alpha);
		} else {
			GL11.glColor4f(brightness, brightness, brightness, alpha);
		}
		GL11.glTranslatef(-0.5f, 0.0f, -0.5f);
		IconCoordinate iconCoordinate = this.getBlockTextureFromSideAndMetadata(Side.BOTTOM, metadata);
        for (BBTex temp : list) {
            calculateShape(temp.height, temp.width, temp.depth, 5);
            if (temp.texture != null) iconCoordinate = temp.texture;
			renderAllFaces(tessellator,iconCoordinate);//draw each face of the quad
		}
		GL11.glTranslatef(0.5f, 0.0f, 0.5f);//reset pos
		bounds.set(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
	}
	@Override
	public boolean shouldItemRender3d() {
		return true;
	}

	@Override
	public float getItemRenderScale() {
		return 0.5F;
	}

	/**
	 * Draw cuboid whose origin is centered on the middle of a given face
	 */
	void calculateShape(double height, double width, double depth, int face){
		if (face == 7) {
			bounds.set(0.5D - width, 1.0D - depth, 0.5D - height, 0.5D + width, 1.0, 0.5D + height);
		} else if (face == 8) {
			bounds.set(0.5D - height, 1.0D - depth, 0.5D - width, 0.5D + height, 1.0, 0.5D + width);
		} else if (face == 5) {
			bounds.set(0.5D - width, 0.0, 0.5D - height, 0.5D + width, depth, 0.5D + height);
		} else if (face == 6) {
			bounds.set(0.5D - height, 0.0, 0.5D - width, 0.5D + height, depth, 0.5D + width);
		} else if (face == 4) {
			bounds.set(0.5D - width, 0.5D - height, 1.0D - depth, 0.5D + width, 0.5D + height, 1.0);
		} else if (face == 3) {
			bounds.set(0.5D - width, 0.5D - height, 0.0, 0.5D + width, 0.5D + height, depth);
		} else if (face == 2) {
			bounds.set(1.0D - depth, 0.5D - height, 0.5D - width, 1.0, 0.5D + height, 0.5D + width);
		} else if (face == 1) {
			bounds.set(0.0, 0.5D - height, 0.5D - width, depth, 0.5D + height, 0.5D + width);
		}
	}
	void setBoundList(){
		this.primedTexture = TextureRegistry.getTexture("minecraft:block/cobbled_netherrack_igneous_overlay");
		//TextureRegistry.getTexture("minecraft:block/cake_inner")
		list.add(new BBTex(0.5D,0.5D,0.0625D));
		list.add(new BBTex(0.0625D,0.4375D,0.1875D));
		list.add(new BBTex(0.4375D,0.0625D,0.1875D));
		list.add(new BBTex(0.375D,0.375D,0.125D,BlockModelDispatcher.getInstance().getDispatch(Blocks.TNT).getBlockTextureFromSideAndMetadata(Side.SOUTH, 0)));
		list.add(new BBTex(0.1875D,0.1875D,0.250D, TextureRegistry.getTexture("minecraft:block/obsidian")));
	}
	private void renderFace(Tessellator tessellator, float normalX, float normalY, float normalZ, Consumer<Tessellator> renderFunction) {
		tessellator.startDrawingQuads();
		tessellator.setNormal(normalX, normalY, normalZ);
		renderFunction.accept(tessellator);
		tessellator.draw();
	}

	private void renderAllFaces(Tessellator tessellator, IconCoordinate iconCoordinate) {
		renderFace(tessellator, 0.0f, -1.0f, 0.0f, t -> this.renderBottomFace(t, bounds, 0.0, 0.0, 0.0, iconCoordinate));
		renderFace(tessellator, 0.0f, 1.0f, 0.0f, t -> this.renderTopFace(t, bounds, 0.0, 0.0, 0.0, iconCoordinate));
		renderFace(tessellator, 0.0f, 0.0f, -1.0f, t -> this.renderNorthFace(t, bounds, 0.0, 0.0, 0.0, iconCoordinate));
		renderFace(tessellator, 0.0f, 0.0f, 1.0f, t -> this.renderSouthFace(t, bounds, 0.0, 0.0, 0.0, iconCoordinate));
		renderFace(tessellator, -1.0f, 0.0f, 0.0f, t -> this.renderWestFace(t, bounds, 0.0, 0.0, 0.0, iconCoordinate));
		renderFace(tessellator, 1.0f, 0.0f, 0.0f, t -> this.renderEastFace(t, bounds, 0.0, 0.0, 0.0, iconCoordinate));
	}
}
