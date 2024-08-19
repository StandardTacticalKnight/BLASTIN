package standardtacticalknight.blastin.world;

import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.Explosion;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkPosition;

public class ExplosionBreachingCharge extends Explosion {
	private final Side side;
	public ExplosionBreachingCharge(World world, Entity entity, double x, double y, double z, float explosionSize, Side side) {
		super(world, entity, x, y, z, explosionSize);
		this.side = side;
	}

	@Override
	protected void calculateBlocksToDestroy() {
		int radius = 19; //search radius
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
					rayX /= len/xMask;
					rayY /= len/yMask;
					rayZ /= len/zMask;
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
}
