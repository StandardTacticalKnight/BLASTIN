package standardtacticalknight.blastin.entity;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import standardtacticalknight.blastin.Blastin;
import standardtacticalknight.blastin.block.BlockLeverInterface;

import java.util.List;

public class TileEntityLandMine extends TileEntity {
	@Override
	public void tick() {
		if (this.worldObj == null || this.worldObj.isClientSide) {
			return;
		}
		int id = this.worldObj.getBlockId(this.x, this.y, this.z);
		int meta = this.worldObj.getBlockMetadata(this.x, this.y, this.z);
		boolean shouldBeActive = false;
		Direction facing = BlockLeverInterface.getSide(meta).getDirection();
		int effectiveRange = this.getSightRange(this.worldObj, this.x, this.y, this.z, facing);
		if (effectiveRange > 0) {
			AABB detectionBox = this.getDetectionBox(this.x, this.y, this.z, facing, effectiveRange);
			List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(null, detectionBox);
			for (int i = 0; i < list.size(); ++i) {
				shouldBeActive = true;
			}
		}
		if (shouldBeActive && (meta & 0b10000) == 0b10000) {
			this.worldObj.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, (double)this.x + 0.5, (double)this.y + 0.5, (double)this.z + 0.5, "random.breath", 0.5f, this.worldObj.rand.nextFloat());
			this.worldObj.scheduleBlockUpdate(this.x, this.y, this.z, id, 40);
		}
        //BlockMotionSensor.updateSensorBlockState(false, this.worldObj, this.x, this.y, this.z);
    }

	private int getSightRange(World world, int x, int y, int z, Direction facing) {
		if (facing == Direction.NONE) {
			return 0;
		}
		int range = 4;
		int blockInFront = world.getBlockId(x + facing.getOffsetX(), y + facing.getOffsetY(), z + facing.getOffsetZ());
		if (Block.hasTag(blockInFront, BlockTags.EXTENDS_MOTION_SENSOR_RANGE)) {
			range = 8;
		}
		for (int i = 1; i <= range; ++i) {
			int z1;
			int y1;
			int x1 = x + facing.getOffsetX() * i;
			int id = world.getBlockId(x1, y1 = y + facing.getOffsetY() * i, z1 = z + facing.getOffsetZ() * i);
			if (!Block.solid[id] || Block.hasTag(id, BlockTags.EXTENDS_MOTION_SENSOR_RANGE)) continue;
			return i - 1;
		}
		return range;
	}
	private AABB getDetectionBox(int x, int y, int z, Direction facing, int range) {
		int x1 = x + facing.getOffsetX();
		int y1 = y + facing.getOffsetY();
		int z1 = z + facing.getOffsetZ();
		int x2 = x + facing.getOffsetX() * range;
		int y2 = y + facing.getOffsetY() * range;
		int z2 = z + facing.getOffsetZ() * range;
		int minX = Math.min(x1, x2);
		int minY = Math.min(y1, y2);
		int minZ = Math.min(z1, z2);
		int maxX = Math.max(x1, x2) + 1;
		int maxY = Math.max(y1, y2) + 1;
		int maxZ = Math.max(z1, z2) + 1;
		return AABB.getBoundingBoxFromPool(minX, minY, minZ, maxX, maxY, maxZ);
	}
}
