package standardtacticalknight.blastin.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicLever;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemFireStriker;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.Explosion;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import standardtacticalknight.blastin.world.ExplosionBreachingCharge;

import java.util.Random;

public class BlockBreachingCharge extends BlockLogicLever {
	private static final float explosionSize = 4.0F;
	@Override
	public int tickDelay() {
		return 40;
	}
	public BlockBreachingCharge(Block<?> block) {
		super(block);
	}
	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {

		if (world.isClientSide) {
			if (player != null && player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemFireStriker) {
				player.inventory.getCurrentItem().damageItem(1, player);
			}
			return true;
		}

		if (player != null && player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemFireStriker) {
			int metadata = world.getBlockMetadata(x, y, z);
			int facing = metadata & 0b00001111; //first 4 bits face direction
			int primed = 0b00010000 - (metadata & 0b00010000); //5th bit isPrimed

			player.inventory.getCurrentItem().damageItem(1, player);
			if(primed > 0) world.playSoundEffect(player, SoundCategory.WORLD_SOUNDS, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "random.fuse", 1.0f, 0.1f);
			world.setBlockMetadataWithNotify(x, y, z, facing + primed);
			world.playSoundEffect(player, SoundCategory.WORLD_SOUNDS, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "random.click", 0.3f, primed <= 0 ? 0.5f : 0.6f);
			world.notifyBlocksOfNeighborChange(x, y, z, this.id());
			world.scheduleBlockUpdate(x, y, z, this.id(), this.tickDelay());
		}
		return true;
	}
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (world.isClientSide) {
			return;
		}
		int blockMetadata = world.getBlockMetadata(x, y, z);
		if ((blockMetadata & 16) == 0) {
			return;
		}
		world.setBlockWithNotify(x, y, z, 0);
		Side side = getSide(blockMetadata);
		Explosion explosion = new ExplosionBreachingCharge(world, null, (double)x+0.5f, (double)y+0.5f, (double)z+0.5f, explosionSize, side);
		explosion.explode();
		explosion.addEffects(true);
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

	@Override
	public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
		int face = world.getBlockMetadata(x, y, z) & 0xF;
		double height = 0.5d;
		double width = 0.5d;
		double depth = 0.25d;
		if (face == 7) {
			return AABB.getTemporaryBB(0.5D - width, 1.0D - depth, 0.5D - height, 0.5D + width, 1.0, 0.5D + height);
		} else if (face == 8) {
			return AABB.getTemporaryBB(0.5D - height, 1.0D - depth, 0.5D - width, 0.5D + height, 1.0, 0.5D + width);
		} else if (face == 5) {
			return AABB.getTemporaryBB(0.5D - width, 0.0, 0.5D - height, 0.5D + width, depth, 0.5D + height);
		} else if (face == 6) {
			return AABB.getTemporaryBB(0.5D - height, 0.0, 0.5D - width, 0.5D + height, depth, 0.5D + width);
		} else if (face == 4) {
			return AABB.getTemporaryBB(0.5D - width, 0.5D - height, 1.0D - depth, 0.5D + width, 0.5D + height, 1.0);
		} else if (face == 3) {
			return AABB.getTemporaryBB(0.5D - width, 0.5D - height, 0.0, 0.5D + width, 0.5D + height, depth);
		} else if (face == 2) {
			return AABB.getTemporaryBB(1.0D - depth, 0.5D - height, 0.5D - width, 1.0, 0.5D + height, 0.5D + width);
		} else if (face == 1) {
			return AABB.getTemporaryBB(0.0, 0.5D - height, 0.5D - width, depth, 0.5D + height, 0.5D + width);
		}
		return null;
	}

	@Override
	public boolean isSignalSource() {
		return false;
	}
	@Override
	public void onBlockRemoved(World world, int x, int y, int z, int data) {
	}

	@Override
	public boolean getSignal(WorldSource worldSource, int x, int y, int z, Side side) {
		return false;
	}

	@Override
	public boolean getDirectSignal(World world, int x, int y, int z, Side side) {
		return false;
	}
}
