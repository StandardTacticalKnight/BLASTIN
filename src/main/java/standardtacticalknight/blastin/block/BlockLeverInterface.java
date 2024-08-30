package standardtacticalknight.blastin.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.util.helper.Axis;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

import java.util.Random;


public interface BlockLeverInterface {
	public void onBlockRemoved(World world, int x, int y, int z, int data);
	public boolean onBlockRightClicked(World world, int x, int y, int z, EntityPlayer player, Side side, double xPlaced, double yPlaced);
	public void setBlockBoundsBasedOnState(WorldSource world, int x, int y, int z);
	public void dropBlockWithCause(World world, EnumDropCause cause, int x, int y, int z, int meta, TileEntity tileEntity);
	public void updateTick(World world, int x, int y, int z, Random rand);

	/**
	 * @return default: 40
	 */
	public int tickRate();
	/**
	 * @return default: false
	 */
	public default boolean renderAsNormalBlock() {
		return false;
	}
	/**
	 * @return default: false
	 */
	public default boolean isSolidRender() {
		return false;
	}
	/**
	 * @return default: null
	 */
	public default AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
		return null;
	}
	public default void onBlockLeftClicked(World world, int x, int y, int z, EntityPlayer player, Side side, double xHit, double yHit) {
		if (!Item.hasTag(player.getCurrentEquippedItem(), ItemTags.PREVENT_LEFT_CLICK_INTERACTIONS)) {
			this.onBlockRightClicked(world, x, y, z, player, null, 0.0, 0.0);
		}
	}
	default boolean checkIfAttachedToBlock(World world, int i, int j, int k) {
		return true;
	}
	public default void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		if (this.checkIfAttachedToBlock(world, x, y, z)) {
			int i1 = world.getBlockMetadata(x, y, z) & 0xF;
			boolean flag = false;
			if (!world.isBlockNormalCube(x - 1, y, z) && i1 == 1) {
				flag = true;
			}
			if (!world.isBlockNormalCube(x + 1, y, z) && i1 == 2) {
				flag = true;
			}
			if (!world.isBlockNormalCube(x, y, z - 1) && i1 == 3) {
				flag = true;
			}
			if (!world.isBlockNormalCube(x, y, z + 1) && i1 == 4) {
				flag = true;
			}
			if (!world.canPlaceOnSurfaceOfBlock(x, y - 1, z) && i1 == 5) {
				flag = true;
			}
			if (!world.canPlaceOnSurfaceOfBlock(x, y - 1, z) && i1 == 6) {
				flag = true;
			}
			if (!world.canPlaceOnSurfaceOfBlock(x, y + 1, z) && i1 == 7) {
				flag = true;
			}
			if (!world.canPlaceOnSurfaceOfBlock(x, y + 1, z) && i1 == 8) {
				flag = true;
			}
			if (flag) {
				this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null);
				world.setBlockWithNotify(x, y, z, 0);
			}
		}
	}
	public default void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		int i1 = world.getBlockMetadata(x, y, z);
		int j1 = i1 & 0x10;
		i1 &= 0xF;
		i1 = -1;
		if (side == Side.BOTTOM && world.canPlaceOnSurfaceOfBlock(x, y + 1, z)) {
			i1 = 7 + (entity.getHorizontalPlacementDirection(side).getAxis() == Axis.Z ? 1 : 0);
		}
		if (side == Side.TOP && world.canPlaceOnSurfaceOfBlock(x, y - 1, z)) {
			i1 = 5 + (entity.getHorizontalPlacementDirection(side).getAxis() == Axis.Z ? 1 : 0);
		}
		if (side == Side.NORTH && world.isBlockNormalCube(x, y, z + 1)) {
			i1 = 4;
		}
		if (side == Side.SOUTH && world.isBlockNormalCube(x, y, z - 1)) {
			i1 = 3;
		}
		if (side == Side.WEST && world.isBlockNormalCube(x + 1, y, z)) {
			i1 = 2;
		}
		if (side == Side.EAST && world.isBlockNormalCube(x - 1, y, z)) {
			i1 = 1;
		}
		if (i1 == -1) {
			this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null);
			world.setBlockWithNotify(x, y, z, 0);
		} else {
			world.setBlockMetadataWithNotify(x, y, z, i1 + j1);
		}
	}
	public default boolean canPlaceBlockAt(World world, int x, int y, int z) {
		if (world.isBlockNormalCube(x - 1, y, z)) {
			return true;
		}
		if (world.isBlockNormalCube(x + 1, y, z)) {
			return true;
		}
		if (world.isBlockNormalCube(x, y, z - 1)) {
			return true;
		}
		if (world.isBlockNormalCube(x, y, z + 1)) {
			return true;
		}
		if (world.isBlockNormalCube(x, y + 1, z)) {
			return true;
		}
		return world.canPlaceOnSurfaceOfBlock(x, y - 1, z);
	}
	public default boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
		if (side == 0 && world.isBlockNormalCube(x, y + 1, z)) {
			return true;
		}
		if (side == 1 && world.canPlaceOnSurfaceOfBlock(x, y - 1, z)) {
			return true;
		}
		if (side == 2 && world.isBlockNormalCube(x, y, z + 1)) {
			return true;
		}
		if (side == 3 && world.isBlockNormalCube(x, y, z - 1)) {
			return true;
		}
		if (side == 4 && world.isBlockNormalCube(x + 1, y, z)) {
			return true;
		}
		return side == 5 && world.isBlockNormalCube(x - 1, y, z);
	}
	public static Side getSide(int meta) {
		int side = meta & 0xF;
		switch (side){
			case 1:return Side.EAST;
			case 2:return Side.WEST;
			case 3:return Side.SOUTH;
			case 4:return Side.NORTH;
			case 5:
			case 6:
				return Side.TOP;
			case 7:
			case 8:
				return Side.BOTTOM;
		}
		return Side.NONE;
	}
}
