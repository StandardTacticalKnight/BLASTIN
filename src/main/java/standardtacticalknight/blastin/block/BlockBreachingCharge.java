package standardtacticalknight.blastin.block;

import net.minecraft.core.block.BlockLever;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemFirestriker;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.Explosion;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import standardtacticalknight.blastin.world.ExplosionBreachingCharge;

import java.util.Random;

public class BlockBreachingCharge extends BlockLever {
	private static final float explosionSize = 4.0F;
	@Override
	public int tickRate() {
		return 40;
	}
	public BlockBreachingCharge(String key, int id) {
		super(key, id);
	}
	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, EntityPlayer player, Side side, double xHit, double yHit) {

		if (world.isClientSide) {
			if (player != null && player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemFirestriker) {
				player.inventory.getCurrentItem().damageItem(1, player);
			}
			return true;
		}

		if (player != null && player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemFirestriker) {
			int metadata = world.getBlockMetadata(x, y, z);
			int facing = metadata & 0xF;
			int primed = 16 - (metadata & 0x10);

			player.inventory.getCurrentItem().damageItem(1, player);
			if(primed > 0) world.playSoundEffect(player, SoundCategory.WORLD_SOUNDS, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "random.fuse", 1.0f, 0.1f);
			world.setBlockMetadataWithNotify(x, y, z, facing + primed);
			world.playSoundEffect(player, SoundCategory.WORLD_SOUNDS, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "random.click", 0.3f, primed <= 0 ? 0.5f : 0.6f);
			world.notifyBlocksOfNeighborChange(x, y, z, this.id);
			world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
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
		Explosion explosion = new ExplosionBreachingCharge(world, (Entity) null, (double)x+0.5f, (double)y+0.5f, (double)z+0.5f, explosionSize, side);
		explosion.doExplosionA();
		explosion.doExplosionB(true);
	}

	private Side getSide(int meta) {
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
	public boolean canProvidePower() {
		return false;
	}
	@Override
	public void onBlockRemoved(World world, int x, int y, int z, int data) {
	}
	@Override
	public boolean isPoweringTo(WorldSource blockAccess, int x, int y, int z, int side) {
		return false;
	}
	@Override
	public boolean isIndirectlyPoweringTo(World world, int x, int y, int z, int side) {
		return false;
	}
}
