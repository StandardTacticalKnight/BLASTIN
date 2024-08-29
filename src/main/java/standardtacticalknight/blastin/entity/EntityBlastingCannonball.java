package standardtacticalknight.blastin.entity;

import net.minecraft.core.HitResult;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.projectile.EntityCannonball;
import net.minecraft.core.entity.projectile.EntityCannonballBouncy;
import net.minecraft.core.world.World;

public class EntityBlastingCannonball extends EntityCannonball {
	public EntityBlastingCannonball(World world, EntityLiving owner) {
		super(world, owner);
	}
	@Override
	public void onHit(HitResult hitResult) {
		if (hitResult.hitType == HitResult.HitType.TILE) {
			this.world.newExplosion(this.owner, this.x, this.y, this.z, 3f, false, false);
			this.remove();
		}
	}
}
