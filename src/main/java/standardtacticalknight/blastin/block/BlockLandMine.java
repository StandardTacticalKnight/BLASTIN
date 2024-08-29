package standardtacticalknight.blastin.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockMotionSensor;
import net.minecraft.core.block.BlockTileEntity;
import net.minecraft.core.block.BlockTileEntityRotatable;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntitySensor;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemFirestriker;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import standardtacticalknight.blastin.entity.TileEntityLandMine;

import java.util.Random;

public class BlockLandMine extends BlockMotionSensor implements BlockLeverInterface {
	public BlockLandMine(String key, int id, boolean b) {
		super(key, id, b);
	}
	@Override
	public boolean renderAsNormalBlock() {
		return BlockLeverInterface.super.renderAsNormalBlock();
	}
	@Override
	public boolean isSolidRender() {
		return BlockLeverInterface.super.isSolidRender();
	}
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		BlockLeverInterface.super.onNeighborBlockChange(world,x,y,z,blockId);
	}
	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
		return BlockLeverInterface.super.canPlaceBlockOnSide(world, x, y, z, side);
	}
	@Override
	public void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		BlockLeverInterface.super.onBlockPlaced(world, x, y, z, side, entity, sideHeight);
	}

	@Override
	public boolean checkIfAttachedToBlock(World world, int i, int j, int k) {
		return BlockLeverInterface.super.checkIfAttachedToBlock(world, i, j, k);
	}
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		world.setBlockTileEntity(x, y, z, this.getNewBlockEntity());
	}

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, EntityPlayer player, Side side, double xHit, double yHit) {
		if (world.isClientSide) {
			if (player != null && player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemFirestriker) {
				player.inventory.getCurrentItem().damageItem(1, player);
			}
			return true;
		}
		if (player != null) {
			int metadata = world.getBlockMetadata(x, y, z);
			int facing = metadata & 0b00001111; //first 4 bits face direction
			int primed = 0b00010000 - (metadata & 0b00010000); //5th bit isPrimed
			world.setBlockMetadataWithNotify(x, y, z, facing + primed);
			world.playSoundEffect(player, SoundCategory.WORLD_SOUNDS, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "random.click", 0.3f, primed <= 0 ? 0.5f : 0.6f);
			world.notifyBlocksOfNeighborChange(x, y, z, this.id);
		}
		return true;
	}
	@Override
	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int x, int y, int z, int meta, TileEntity tileEntity) {
		switch (dropCause) {
			case WORLD:
			case EXPLOSION:
			case PROPER_TOOL:
			case PICK_BLOCK:
			case SILK_TOUCH: {
				return new ItemStack[]{this.getDefaultStack()};
			}
		}
		return null;
	}
	@Override
	public void setBlockBoundsBasedOnState(WorldSource world, int x, int y, int z) {
		int face = world.getBlockMetadata(x, y, z) & 0xF;
		double height = 0.5d;
		double width = 0.5d;
		double depth = 0.25d;
		if (face == 7) {
			this.setBlockBounds(0.5D - width, 1.0D - depth, 0.5D - height, 0.5D + width, 1.0, 0.5D + height);
		} else if (face == 8) {
			this.setBlockBounds(0.5D - height, 1.0D - depth, 0.5D - width, 0.5D + height, 1.0, 0.5D + width);
		} else if (face == 5) {
			this.setBlockBounds(0.5D - width, 0.0, 0.5D - height, 0.5D + width, depth, 0.5D + height);
		} else if (face == 6) {
			this.setBlockBounds(0.5D - height, 0.0, 0.5D - width, 0.5D + height, depth, 0.5D + width);
		} else if (face == 4) {
			this.setBlockBounds(0.5D - width, 0.5D - height, 1.0D - depth, 0.5D + width, 0.5D + height, 1.0);
		} else if (face == 3) {
			this.setBlockBounds(0.5D - width, 0.5D - height, 0.0, 0.5D + width, 0.5D + height, depth);
		} else if (face == 2) {
			this.setBlockBounds(1.0D - depth, 0.5D - height, 0.5D - width, 1.0, 0.5D + height, 0.5D + width);
		} else if (face == 1) {
			this.setBlockBounds(0.0, 0.5D - height, 0.5D - width, depth, 0.5D + height, 0.5D + width);
		}
	}
	@Override
	protected TileEntity getNewBlockEntity() {
		return new TileEntityLandMine();
	}
}
