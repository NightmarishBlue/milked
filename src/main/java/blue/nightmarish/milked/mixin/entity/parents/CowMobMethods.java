package blue.nightmarish.milked.mixin.entity.parents;

import blue.nightmarish.milked.IMilkableBehavior;
import blue.nightmarish.milked.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static blue.nightmarish.milked.MilkedMod.PARTICLE_SPAWN_OFFSET;
import static blue.nightmarish.milked.MilkedMod.PARTICLE_SPAWN_SPREAD;

@Mixin(Mob.class)
public abstract class CowMobMethods extends LivingEntity implements IMilkableBehavior {
    protected CowMobMethods(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    /**
     * Restores the cow's milkability, and ages it up slightly. This function is used in the
     * {@code GenericEatBlockGoal}.
     */
    @Inject(method = "ate", at = @At("TAIL"))
    public void milked$ate(CallbackInfo ci) {
        if (!((Animal) (Object) this instanceof Cow)) return;
        IMilkableBehavior milkable = (IMilkableBehavior) this;
        milkable.milked$setMilk(true);
        if (this.isBaby()) {
            milkable.milked$ageUp(20);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void milked$tick(CallbackInfo ci) {
        if (!this.isBaby() && this.milked$hasMilk() && this.random.nextFloat() < 0.025F) {
            if (!this.milked$shouldWeSpawnDrips()) return;
            // calculate the angle of its body and offset it by some amount.
            float angleRad = (this.yBodyRot - 90) * ((float) Math.PI / 180F);
            double x = this.getX() + Mth.cos(angleRad) * PARTICLE_SPAWN_OFFSET;
            double z = this.getZ() + Mth.sin(angleRad) * PARTICLE_SPAWN_OFFSET;
            for (int i = 0; i < this.random.nextInt(2) + 1; ++i) {
                Util.spawnFluidParticle(this.level, x - PARTICLE_SPAWN_SPREAD, x + PARTICLE_SPAWN_SPREAD, z - PARTICLE_SPAWN_SPREAD, z + PARTICLE_SPAWN_SPREAD, this.getY(0.5D), milked$getMilkParticles());
            }
        }
    }
}
