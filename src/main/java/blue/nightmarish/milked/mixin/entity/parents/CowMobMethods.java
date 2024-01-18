package blue.nightmarish.milked.mixin.entity.parents;

import blue.nightmarish.milked.MilkableEntity;
import blue.nightmarish.milked.Util;
import blue.nightmarish.milked.networking.MilkMessages;
import blue.nightmarish.milked.particle.MilkedModParticles;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static blue.nightmarish.milked.MilkedMod.PARTICLE_SPAWN_OFFSET;
import static blue.nightmarish.milked.MilkedMod.PARTICLE_SPAWN_SPREAD;

@Mixin(Mob.class)
public abstract class CowMobMethods extends LivingEntity {
    protected CowMobMethods(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    /**
     * Restores the cow's milkability, and ages it up slightly. This function is used in the
     * {@code GenericEatBlockGoal}.
     */
    @Inject(method = "ate", at = @At("TAIL"))
    public void milked$ate(CallbackInfo ci) {
        if (!((Mob) (Object) this instanceof Cow)) return;
        MilkableEntity milkable = (MilkableEntity) this;
        milkable.milked$setMilk(true);
        if (this.isBaby()) {
            milkable.milked$ageUp(20);
        }
    }

    // TODO: somewhere in here, we have to sync the data to the client.
    @Inject(method = "tick", at = @At("TAIL"))
    public void milked$tick(CallbackInfo ci) {
        if (!((Mob) (Object) this instanceof Cow)) return;
        MilkableEntity milkable = (MilkableEntity) this;
        if (this.level().isClientSide()) {
            if (!this.isBaby() && milkable.milked$hasMilk() && this.random.nextFloat() < 0.025F) {
                if (!milkable.milked$shouldWeSpawnDrips()) return;
                // calculate the angle of its body and offset it by some amount.
                float angleRad = (this.yBodyRot - 90) * ((float) Math.PI / 180F);
                double x = this.getX() + Mth.cos(angleRad) * PARTICLE_SPAWN_OFFSET;
                double z = this.getZ() + Mth.sin(angleRad) * PARTICLE_SPAWN_OFFSET;
                for (int i = 0; i < this.random.nextInt(2) + 1; ++i) {
                    Util.spawnFluidParticle(this.level(), x - PARTICLE_SPAWN_SPREAD, x + PARTICLE_SPAWN_SPREAD, z - PARTICLE_SPAWN_SPREAD, z + PARTICLE_SPAWN_SPREAD, this.getY(0.5D), MilkedModParticles.DRIPPING_MILK.get());
                }
            }
        } else {
            if (this.tickCount % 200 == 0) {
                MilkMessages.updateEntity(this, milkable.milked$hasMilk());
            }
        }
    }
}
