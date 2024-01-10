package blue.nightmarish.milked.mixin.entity.parents;

import blue.nightmarish.milked.IMilkableBehavior;
import blue.nightmarish.milked.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class CowAnimalMethods extends AgeableMob {
    protected CowAnimalMethods(EntityType<? extends AgeableMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    protected void milked$customServerAiStep(CallbackInfo ci) {
        if (!((Animal) (Object) this instanceof Cow)) return;
        IMilkableBehavior milkable = (IMilkableBehavior) this;
        milkable.milked$setEatTicks(milkable.milked$getEatGoal()
                .getEatAnimationTick());
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    public void milked$aiStep(CallbackInfo ci) {
        if (!(((Animal) (Object) this instanceof Cow)
                && this.level.isClientSide)) return;
        IMilkableBehavior milkable = (IMilkableBehavior) this;
        int eatTicks = milkable.milked$getEatTicks();
        if (eatTicks >= 0)
            milkable.milked$setEatTicks(eatTicks - 1);
    }

    /**
     * Handles an entity event fired from {@link net.minecraft.world.level.Level#broadcastEntityEvent}.
     */
    @Inject(method = "handleEntityEvent", at = @At("HEAD"), cancellable = true)
    public void milked$handleEntityEvent(byte pId, CallbackInfo ci) {
        if (!((Animal) (Object) this instanceof Cow)) return;
        if (pId == 10) {
            IMilkableBehavior milkable = (IMilkableBehavior) this;
            milkable.milked$setEatTicks(Util.COW_EAT_ANIMATION_TICKS);
            ci.cancel();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void milked$addAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
        if (!((Animal) (Object) this instanceof Cow)) return;
        IMilkableBehavior milkable = (IMilkableBehavior) this;
        pCompound.putBoolean("HasMilk", milkable.milked$hasMilk());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void milked$readAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
        if (!((Animal) (Object) this instanceof Cow)) return;
        IMilkableBehavior milkable = (IMilkableBehavior) this;
        milkable.milked$setMilk(pCompound.getBoolean("HasMilk"));
    }
}
