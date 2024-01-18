package blue.nightmarish.milked.mixin.entity;

import blue.nightmarish.milked.MilkableEntity;
import blue.nightmarish.milked.Util;
import blue.nightmarish.milked.entity.ai.EatGrassGoal;
import blue.nightmarish.milked.networking.MilkMessages;
import blue.nightmarish.milked.particle.MilkedModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cow.class)
public abstract class MilkableCow extends Animal implements MilkableEntity {
    public MilkableCow(EntityType<? extends Cow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private boolean milked$hasMilk;

    public boolean milked$hasMilk() {
        return this.milked$hasMilk;
    }

    public void milked$setMilk(boolean state) {
        if (!this.level().isClientSide()) MilkMessages.updateEntity(this, state);
        this.milked$hasMilk = state;
    }

    @Unique
    private int milked$eatAnimationTick;
    public int milked$getEatTicks() {
        return this.milked$eatAnimationTick;
    }
    public void milked$setEatTicks(int animationTicks) {
        this.milked$eatAnimationTick = animationTicks;
    }

    @Unique
    private int milked$particleDripCounter = 0;
    @Unique
    private EatBlockGoal milked$eatBlockGoal;
    public EatBlockGoal milked$getEatGoal() {
        return this.milked$eatBlockGoal;
    }

    public EatBlockGoal milked$initGoal() {
        return new EatGrassGoal(this);
    }

    @Unique
    public Item milked$getMilkItem() {
        return Items.BUCKET;
    }

    @Unique
    public ParticleOptions milked$getMilkParticles() {
        return MilkedModParticles.DRIPPING_MILK.get();
    }
    @Inject(method = "registerGoals", at = @At("RETURN"))
    void onRegisterGoals(CallbackInfo ci) {
        this.milked$eatBlockGoal = this.milked$initGoal();
        this.goalSelector.addGoal(5, this.milked$eatBlockGoal);
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public void milked$ageUp(int time) {
        this.ageUp(time);
    }

    public float milked$getHeadEatPositionScale(float pPartialTick) {
        if (this.milked$eatAnimationTick <= 0) {
            return 0.0F;
        } else if (this.milked$eatAnimationTick >= 4 && this.milked$eatAnimationTick <= 36) {
            return 1.0F;
        } else {
            return this.milked$eatAnimationTick < 4 ? ((float)this.milked$eatAnimationTick - pPartialTick) / 4.0F : -((float)(this.milked$eatAnimationTick - 40) - pPartialTick) / 4.0F;
        }
    }

    public float milked$getHeadEatAngleScale(float pPartialTick) {
        if (this.milked$eatAnimationTick > 4 && this.milked$eatAnimationTick <= 36) {
            float f = ((float)(this.milked$eatAnimationTick - 4) - pPartialTick) / 32.0F;
            return ((float)Math.PI / 5F) + 0.21991149F * Mth.sin(f * 28.7F);
        } else {
            return this.milked$eatAnimationTick > 0 ? ((float)Math.PI / 5F) : this.getXRot() * ((float)Math.PI / 180F);
        }
    }

    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    public boolean modifyMobInteract(ItemStack heldItemStack, Item milkBucket) {
        this.milked$setMilk(true);
        if (!(heldItemStack.is(this.milked$getMilkItem()) && this.milked$hasMilk() && !this.isBaby())) return false;
        this.milked$setMilk(false);
        return true;
    }

    @Redirect(method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/Cow;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"
            )
    )
    public Entity modifyBreedOffspring(EntityType<Cow> instance, Level pLevel) {
        Cow offspring = instance.create(pLevel);
        ((MilkableEntity) offspring).milked$setMilk(true);
        return offspring;
    }

    public boolean milked$shouldWeSpawnDrips() {
        this.milked$particleDripCounter += this.random.nextInt(2);
        return (this.milked$particleDripCounter %= Util.DROPLET_TICK_COOLDOWN) != 0;
    }
}
