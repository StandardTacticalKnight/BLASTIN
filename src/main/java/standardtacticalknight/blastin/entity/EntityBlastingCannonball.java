package standardtacticalknight.blastin.entity;

import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.projectile.ProjectileCannonball;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;

public class EntityBlastingCannonball extends ProjectileCannonball {
	public EntityBlastingCannonball(World world, Mob owner) {
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
