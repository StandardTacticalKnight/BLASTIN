package standardtacticalknight.blastin.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicMotionSensor;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemFireStriker;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.NotNull;
import standardtacticalknight.blastin.Blastin;
import standardtacticalknight.blastin.entity.TileEntityLandMine;
import standardtacticalknight.blastin.world.ExplosionBreachingCharge;

import java.util.Random;

public class BlockLandMine extends BlockLogicMotionSensor {

	public BlockLandMine(Block block) {
		super(block, false);
		block.withEntity(TileEntityLandMine::new);
	}
	@Override
	public int tickDelay() {
		return 10;
	}
	@Override
	public boolean isCubeShaped() {
		return false;
	}
	@Override
	public boolean isSolidRender() {
		return false;
	}
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
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
				Blastin.LOGGER.warn("pop off");
				this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null,null);
				world.setBlockWithNotify(x, y, z, 0);
			}
		}
	}
	@Override
	public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
		return null;
	}
	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, Side side) {
		//return true;
		Side checkSide = side.getOpposite();
		return world.isBlockNormalCube(x + checkSide.getOffsetX(), y + checkSide.getOffsetY(), z + checkSide.getOffsetZ());
	}
	@Override
	public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xHeight, double yHeight) {
		onBlockPlacedByMob(world,x,y,z,side,null,xHeight,yHeight);
	}
	public boolean checkIfAttachedToBlock(World world, int x, int y, int z) {//todo revert
		return true;
	}

    @Override
	public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
		int meta = world.getBlockMetadata(x, y, z);
		//side = side.getOpposite();
		boolean isPowered = (meta & 0x10) == 0x10;
		int rotation = -1;
		switch (side) {
			case BOTTOM:
				if (world.isBlockNormalCube(x, y + 1, z)) {
					rotation = 7;
				}
				break;
			case TOP:
				if (world.isBlockNormalCube(x, y - 1, z)) {
					rotation = 5;
				}
				break;
			case NORTH:
				if (world.isBlockNormalCube(x, y, z + 1)) {
					rotation = 4;
				}
				break;
			case SOUTH:
				if (world.isBlockNormalCube(x, y, z - 1)) {
					rotation = 3;
				}
				break;
			case WEST:
				if (world.isBlockNormalCube(x + 1, y, z)) {
					rotation = 2;
				}
				break;
			case EAST:
				if (world.isBlockNormalCube(x - 1, y, z)) {
					rotation = 1;
				}
		}

		if (rotation == -1) {
			Blastin.LOGGER.info(side.name());
			this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
			world.setBlockWithNotify(x, y, z, 0);
		} else {
			world.setBlockMetadataWithNotify(x, y, z, rotation | (isPowered ? 16 : 0));
		}
	}

	public void updateTick(World world, int x, int y, int z, Random rand){
		if (world.isClientSide) {
			return;
		}
		int blockMetadata = world.getBlockMetadata(x, y, z);
		if ((blockMetadata & 16) == 0) {
			return;
		}
		world.setBlockWithNotify(x, y, z, 0);
		Side side = BlockLeverInterface.getSide(blockMetadata);
		ExplosionBreachingCharge explosion = new ExplosionBreachingCharge(world, null, (double)x+0.5f, (double)y+0.5f, (double)z+0.5f, 4.0f, side.getOpposite());
		explosion.explode();
		if (this.id() == Blastin.incendiaryMine.id()){
			explosion.createBlocks(Blocks.FIRE);
		}else if(this.id() == Blastin.spiderMine.id()){
			explosion.createBlocks(Blocks.COBWEB);
		}
		if (!world.isClientSide) {
			world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, x,y,z, "random.explode", 4.0f, (1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f) * 0.7f);
		}

		world.spawnParticle("largesmoke", x,y,z, 0.0, 1.0, 0.0, 0);
		world.spawnParticle("explode", x,y,z, 0.0, 1.0, 0.0, 0);
		//world.spawnParticle("smoke", x,y,z, 0.0, 0.0, 0.0, 0);
		//explosion.doExplosionB(true);
	}
	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
		if (world.isClientSide) {
			if (player != null && player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemFireStriker) {
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
			world.notifyBlocksOfNeighborChange(x, y, z, this.id());
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
		Blastin.LOGGER.warn("unknown side"+face);
		return AABB.getTemporaryBB(0,0,0,1,1,1);
	}
}
