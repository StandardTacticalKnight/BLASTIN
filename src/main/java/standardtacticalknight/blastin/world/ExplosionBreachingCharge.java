package standardtacticalknight.blastin.world;

import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.Explosion;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkPosition;

import java.util.ArrayList;

public class ExplosionBreachingCharge extends Explosion {
	private final Side side;
	public ExplosionBreachingCharge(World world, Entity entity, double x, double y, double z, float explosionSize, Side side) {
		super(world, entity, x, y, z, explosionSize);
		this.side = side;
	}

	@Override
	protected void calculateBlocksToDestroy() {
		int radius = 19; //search radius
		float[] mask = calcExplosionMask();
		//see mc wiki for explosion mechanics explanation e.g. see https://minecraft.wiki/w/File:ExplosionRay.png
		//this is modded vanilla code but stretches the cube along the wanted axis and chops half the rays off
		for (int i = 0; i < radius; ++i) {
			for (int j = 0; j < radius; ++j) {
				for (int k = 0; k < radius; ++k) {
					//if (!(i == 0 || i == radius - 1 || j == 0 || j == radius - 1 || k == 0 || k == radius - 1)) continue //this way makes WAAAAY more sense in my head but hey
					if (i != 0 && i != radius - 1 && j != 0 && j != radius - 1 && k != 0 && k != radius - 1) continue; //create a cube of rays radius X radius

					//only check for blocks to destroy in front of the mine
					int num = (radius-1)/2;
					if (side==Side.TOP && j>num) continue;
					if (side==Side.BOTTOM && j<num) continue;
					if (side==Side.NORTH && k<num) continue;
					if (side==Side.SOUTH && k>num) continue;
					if (side==Side.EAST && i>num) continue;
					if (side==Side.WEST && i<num) continue;
					if (side==Side.NONE) continue; //should probably just return

					double rayX = (float)i / ((float)radius - 1.0f) * 2.0f - 1.0f;
					double rayY = (float)j / ((float)radius - 1.0f) * 2.0f - 1.0f;
					double rayZ = (float)k / ((float)radius - 1.0f) * 2.0f - 1.0f;
					double len = Math.sqrt(rayX * rayX + rayY * rayY + rayZ * rayZ);

					//bias the explosion in the appropriate direction using masks found above
					rayX /= len/mask[0];
					rayY /= len/mask[1];
					rayZ /= len/mask[2];
					double x = this.explosionX;
					double y = this.explosionY;
					double z = this.explosionZ;
					float travelRate = 0.3f;
					float dissipationRate = 0.01f;

					//look along the ray for blocks to destroy until power runs out
					for (float boomPower = this.explosionSize * (0.7f + this.worldObj.rand.nextFloat() * 0.6f); !(boomPower <= 0.0f); boomPower -= travelRate * 0.75f) {
						int iz = MathHelper.floor_double(z);
						int iy = MathHelper.floor_double(y);
						int ix = MathHelper.floor_double(x);
						int blockId = this.worldObj.getBlockId(ix, iy, iz);
						if (blockId > 0) {
							boomPower -= (Block.blocksList[blockId].getBlastResistance(this.exploder) + 0.3f) * dissipationRate;
						}
						if (boomPower > 0.0f) {
							this.destroyedBlockPositions.add(new ChunkPosition(ix, iy, iz));
						}
						//travel along the ray to find the next block
						x += rayX * (double)travelRate;
						y += rayY * (double)travelRate;
						z += rayZ * (double)travelRate;
					}
				}
			}
		}
	}
	@Override
	protected void damageEntities() {
		float explosionSize2 = this.explosionSize * 2.0f;
		float[] mask = calcExplosionMask();//smoosh explosion damage in direction on face
		int x1 = MathHelper.floor_double(this.explosionX - (double)(explosionSize2 * mask[0]) - 1.0);
		int x2 = MathHelper.floor_double(this.explosionX + (double)(explosionSize2 * mask[0]) + 1.0);
		int y1 = MathHelper.floor_double(this.explosionY - (double)(explosionSize2 * mask[1]) - 1.0);
		int y2 = MathHelper.floor_double(this.explosionY + (double)(explosionSize2 * mask[1]) + 1.0);
		int z1 = MathHelper.floor_double(this.explosionZ - (double)(explosionSize2 * mask[2]) - 1.0);
		int z2 = MathHelper.floor_double(this.explosionZ + (double)(explosionSize2 * mask[2]) + 1.0);
		switch (side) {//don't hurt behind the mine
			case BOTTOM:
				y1 = MathHelper.floor_double(this.explosionY+0.5d);
				break;
			case TOP:
				y2 = MathHelper.floor_double(this.explosionY-0.5d);
				break;
			case NORTH:
				z1 = MathHelper.floor_double(this.explosionZ+0.5d);
				break;
			case SOUTH:
				z2 = MathHelper.floor_double(this.explosionZ-0.5d);
				break;
			case WEST:
				x1 = MathHelper.floor_double(this.explosionX+0.5d);
				break;
			case EAST:
				x2 = MathHelper.floor_double(this.explosionX-0.5d);
				break;
			default:
				break;
		}
		ArrayList<Entity> list = new ArrayList<Entity>(this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, AABB.getBoundingBoxFromPool(x1, y1, z1, x2, y2, z2)));
		Vec3d vec3d = Vec3d.createVector(this.explosionX, this.explosionY, this.explosionZ);
		for (Entity entity : list) {
			double proximity = entity.distanceTo(this.explosionX, this.explosionY, this.explosionZ) / (double)explosionSize2;
			if (!(proximity <= 1.0)) continue;//distance must be less than 1 to damage entity.  0 is full damage 1 is no damage
			double xDist = entity.x - this.explosionX;
			double yDist = entity.y - this.explosionY;
			double zDist = entity.z - this.explosionZ;
			double distFromExpl2 = MathHelper.sqrt_double(xDist * xDist + yDist * yDist + zDist * zDist);
			xDist /= distFromExpl2;//dist to ratio/component of dist
			yDist /= distFromExpl2;
			zDist /= distFromExpl2;
			double dampingFactor = this.worldObj.func_675_a(vec3d, entity.bb);//check for blocks between entity and explosion point
			double damageMult = (1.0 - proximity) * dampingFactor; //ex: point-blank expl would be (1.0f - 0.0f) * 1.0f? || min damage/blocks in way would be (1.0f - 0.99f) * 0.0f?
			entity.hurt(this.exploder, (int)((damageMult * damageMult + damageMult) / 2.0 * 8.0 * (double)explosionSize2 + 1.0), DamageType.BLAST);
			double d14 = damageMult;
			entity.xd += xDist * d14;
			entity.yd += yDist * d14;
			entity.zd += zDist * d14;
		}
	}
	float[] calcExplosionMask(){
		float xMask, yMask, zMask;
		//bias the explosion based on where the mine gets placed
		switch (side){
			case TOP:
			case BOTTOM:
				xMask=0.25f;
				yMask=1.0f;
				zMask=0.25f;
				break;
			case NORTH:
			case SOUTH:
				xMask=0.25f;
				yMask=0.25f;
				zMask=1.0f;
				break;
			case EAST:
			case WEST:
				xMask=1.0f;
				yMask=0.25f;
				zMask=0.25f;
				break;
			default:
				xMask=0.25f;
				yMask=0.25f;
				zMask=0.25f;
				break;
		}
        return new float[]{xMask,yMask,zMask};
    }
}
