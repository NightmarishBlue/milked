package blue.nightmarish.milked.mixin.entity;

import blue.nightmarish.milked.IMilkableCow;
import blue.nightmarish.milked.particle.MilkedModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

import static blue.nightmarish.milked.MilkedMod.offset;
import static blue.nightmarish.milked.MilkedMod.spread;

@Mixin(Cow.class)
public abstract class MilkableCow extends Animal implements IMilkableCow {
    @Unique
    private static final int EAT_ANIMATION_TICKS = 40;
    private static final EntityDataAccessor<Boolean> DATA_HAS_MILK = SynchedEntityData.defineId(MilkableCow.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private int eatAnimationTick;
    @Unique
    private EatBlockGoal eatBlockGoal;

    public MilkableCow(EntityType<? extends Cow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("RETURN"))
    void onRegisterGoals(CallbackInfo ci) {
        this.eatBlockGoal = new EatBlockGoal(this);
        this.goalSelector.addGoal(5, this.eatBlockGoal);
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
    protected void customServerAiStep() {
        this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        if (this.level.isClientSide) {
            this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
        }
        super.aiStep();
    }

    /**
     * Restores the cow's milkability, and ages it up slightly. This function is used in the
     * {@code EatBlockGoal}.
     */
    @Override
    public void ate() {
        super.ate();
        this.setMilk(true);
        if (this.isBaby()) {
            this.ageUp(20);
        }
    }

    public boolean hasMilk() {
        return this.entityData.get(DATA_HAS_MILK);
    }

    public void setMilk(boolean desiredState) {
        this.entityData.set(DATA_HAS_MILK, desiredState);
    }

    /**
     * Handles an entity event fired from {@link net.minecraft.world.level.Level#broadcastEntityEvent}.
     */
    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 10) {
            this.eatAnimationTick = 40;
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public float getHeadEatPositionScale(float pPartialTick) {
        if (this.eatAnimationTick <= 0) {
            return 0.0F;
        } else if (this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36) {
            return 1.0F;
        } else {
            return this.eatAnimationTick < 4 ? ((float)this.eatAnimationTick - pPartialTick) / 4.0F : -((float)(this.eatAnimationTick - 40) - pPartialTick) / 4.0F;
        }
    }

    public float getHeadEatAngleScale(float pPartialTick) {
        if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
            float f = ((float)(this.eatAnimationTick - 4) - pPartialTick) / 32.0F;
            return ((float)Math.PI / 5F) + 0.21991149F * Mth.sin(f * 28.7F);
        } else {
            return this.eatAnimationTick > 0 ? ((float)Math.PI / 5F) : this.getXRot() * ((float)Math.PI / 180F);
        }
    }

    /**
     * @author blue
     * @reason decided best way to alter the branch was to wipe the slate and inject my own
     */
    @Overwrite
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
            return super.mobInteract(pPlayer, pHand);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    public void onMobInteract(Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (itemstack.is(Items.BUCKET) && !this.isBaby() && this.hasMilk()) {
            pPlayer.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, pPlayer, Items.MILK_BUCKET.getDefaultInstance());
            pPlayer.setItemInHand(pHand, itemstack1);
            this.setMilk(false);
            cir.setReturnValue(InteractionResult.sidedSuccess(this.level.isClientSide));
        }
    }


    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.setMilk(true);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Inject(method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/Cow;", at = @At("TAIL"), cancellable = true)
    public void onGetBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent, CallbackInfoReturnable<Cow> cir) {
        Cow cow = EntityType.COW.create(pLevel);
        // assert cow != null; // im pretty sure the cow exists guys.
        ((IMilkableCow) cow).setMilk(true);
        cir.setReturnValue(cow);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("HasMilk", this.hasMilk());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setMilk(pCompound.getBoolean("HasMilk"));
    }

    public void tick() {
        super.tick();
        if (!this.isBaby() && this.hasMilk() && this.random.nextFloat() < 0.025F) {
            // calculate the angle of its body and offset it by some amount.
            float angleRad = (this.yBodyRot - 90) * ((float) Math.PI / 180F);
            double x = this.getX() + Mth.cos(angleRad) * offset;
            double z = this.getZ() + Mth.sin(angleRad) * offset;
            for (int i = 0; i < this.random.nextInt(3) + 1; ++i) {
                this.spawnFluidParticle(this.level, x - spread, x + spread, z - spread, z + spread, this.getY(0.5D), MilkedModParticles.FALLING_MILK.get());
            }
        }
    }

    private void spawnFluidParticle(Level pLevel, double pStartX, double pEndX, double pStartZ, double pEndZ, double pPosY, ParticleOptions pParticleOption) {
        pLevel.addParticle(pParticleOption, Mth.lerp(pLevel.random.nextDouble(), pStartX, pEndX), pPosY, Mth.lerp(pLevel.random.nextDouble(), pStartZ, pEndZ), 0.0D, 0.0D, 0.0D);
    }
}
