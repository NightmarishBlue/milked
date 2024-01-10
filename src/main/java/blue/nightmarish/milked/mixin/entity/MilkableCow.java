package blue.nightmarish.milked.mixin.entity;

import blue.nightmarish.milked.IMilkableBehavior;
import blue.nightmarish.milked.entity.ai.EatGrassGoal;
import blue.nightmarish.milked.particle.MilkedModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
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
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

import static blue.nightmarish.milked.MilkedMod.PARTICLE_SPAWN_OFFSET;
import static blue.nightmarish.milked.MilkedMod.PARTICLE_SPAWN_SPREAD;

@Mixin(Cow.class)
public abstract class MilkableCow extends Animal implements IMilkableBehavior {
    public MilkableCow(EntityType<? extends Cow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Unique
    private static final int EAT_ANIMATION_TICKS = 40;
    @Unique
    private static final EntityDataAccessor<Boolean> DATA_HAS_MILK = SynchedEntityData.defineId(MilkableCow.class, EntityDataSerializers.BOOLEAN);
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

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_HAS_MILK, true);
    }

    @Override
    public void aiStep() {
        if (this.level.isClientSide) {
            this.milked$eatAnimationTick = Math.max(0, this.milked$eatAnimationTick - 1);
        }
        super.aiStep();
    }

    /**
     * Restores the cow's milkability, and ages it up slightly. This function is used in the
     * {@code GenericEatBlockGoal}.
     */
    @Override
    public void ate() {
        super.ate();
        this.milked$setMilk(true);
        if (this.isBaby()) {
            this.ageUp(20);
        }
    }

    public boolean milked$hasMilk() {
        return this.entityData.get(DATA_HAS_MILK);
    }

    public void milked$setMilk(boolean desiredState) {
        this.entityData.set(DATA_HAS_MILK, desiredState);
    }

    /**
     * Handles an entity event fired from {@link net.minecraft.world.level.Level#broadcastEntityEvent}.
     */
    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 10) {
            this.milked$eatAnimationTick = EAT_ANIMATION_TICKS;
        } else {
            super.handleEntityEvent(pId);
        }
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
        if (!(heldItemStack.is(this.milked$getMilkItem()) && this.milked$hasMilk() && !this.isBaby())) return false;
        this.milked$setMilk(false);
        return true;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.milked$setMilk(true);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Redirect(method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/Cow;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"
            )
    )
    public Entity modifyBreedOffspring(EntityType<Cow> instance, Level pLevel) {
        Cow offspring = instance.create(pLevel);
        ((IMilkableBehavior) offspring).milked$setMilk(true);
        return offspring;
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("HasMilk", this.milked$hasMilk());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.milked$setMilk(pCompound.getBoolean("HasMilk"));
    }

    public void tick() {
        super.tick();
        if (!this.isBaby() && this.milked$hasMilk() && this.random.nextFloat() < 0.025F) {
            if (++this.milked$particleDripCounter == 10) return;
            milked$particleDripCounter = 0;
            // calculate the angle of its body and offset it by some amount.
            float angleRad = (this.yBodyRot - 90) * ((float) Math.PI / 180F);
            double x = this.getX() + Mth.cos(angleRad) * PARTICLE_SPAWN_OFFSET;
            double z = this.getZ() + Mth.sin(angleRad) * PARTICLE_SPAWN_OFFSET;
            for (int i = 0; i < this.random.nextInt(2) + 1; ++i) {
                this.milked$spawnFluidParticle(this.level, x - PARTICLE_SPAWN_SPREAD, x + PARTICLE_SPAWN_SPREAD, z - PARTICLE_SPAWN_SPREAD, z + PARTICLE_SPAWN_SPREAD, this.getY(0.5D), milked$getMilkParticles());
            }
        }
    }

    @Unique
    private void milked$spawnFluidParticle(Level pLevel, double pStartX, double pEndX, double pStartZ, double pEndZ, double pPosY, ParticleOptions pParticleOption) {
        pLevel.addParticle(pParticleOption, Mth.lerp(pLevel.random.nextDouble(), pStartX, pEndX), pPosY, Mth.lerp(pLevel.random.nextDouble(), pStartZ, pEndZ), 0.0D, 0.0D, 0.0D);
    }
}
