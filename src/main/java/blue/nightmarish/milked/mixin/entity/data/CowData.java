package blue.nightmarish.milked.mixin.entity.data;

import blue.nightmarish.milked.IMilkableBehavior;
import blue.nightmarish.milked.MilkedMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cow.class)
public abstract class CowData extends ExposeAgeableMob implements IMilkableBehavior {
    private static EntityDataAccessor<Boolean> milked$DATA_HAS_MILK = SynchedEntityData.defineId(CowData.class, EntityDataSerializers.BOOLEAN);

    protected CowData(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void milked$defineSynchedData(CallbackInfo callbackInfo) {
        MilkedMod.LOGGER.info("fuuuck");
        this.entityData.define(milked$DATA_HAS_MILK, true);
    }

    @Override
    public void milked$finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, SpawnGroupData pSpawnData, CompoundTag pDataTag, CallbackInfoReturnable<SpawnGroupData> cir) {
        this.milked$setMilk(true);
    }

    public boolean milked$hasMilk() {
        return this.entityData.get(milked$DATA_HAS_MILK);
    }

    public void milked$setMilk(boolean desiredState) {
        this.entityData.set(milked$DATA_HAS_MILK, desiredState);
    }
}
