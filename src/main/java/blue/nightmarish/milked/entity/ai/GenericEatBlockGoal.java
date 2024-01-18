package blue.nightmarish.milked.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

import java.util.function.Predicate;

public class GenericEatBlockGoal extends EatBlockGoal {
    private static final int EAT_ANIMATION_TICKS = 40;
    protected Predicate<BlockState> EDIBLE_GRASSES;
    protected Predicate<BlockState> EDIBLE_GROUND_BLOCKS;
    protected Block LEFTOVER_BLOCK;
    /** The entity owner of this AITask */
    private final Mob mob;
    /** The world the grass eater entity is eating from */
    private final Level level;
    /** Number of ticks since the entity started to eat grass */
    private int eatAnimationTick;

    protected void initFields() {
        EDIBLE_GRASSES = BlockStatePredicate.forBlock(Blocks.GRASS);
        EDIBLE_GROUND_BLOCKS = BlockStatePredicate.forBlock(Blocks.GRASS_BLOCK);
        LEFTOVER_BLOCK = Blocks.CLAY;
    }

    public GenericEatBlockGoal(Mob pMob) {
        super(pMob);
        this.mob = pMob;
        this.level = pMob.level();
        initFields();
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
            return false;
        } else {
            BlockPos blockpos = this.mob.blockPosition();
            return (this.EDIBLE_GRASSES.test(this.level.getBlockState(blockpos)))
            || EDIBLE_GROUND_BLOCKS.test(this.level.getBlockState(blockpos.below()));
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.eatAnimationTick = this.adjustedTickDelay(40);
        this.level.broadcastEntityEvent(this.mob, (byte)10);
        this.mob.getNavigation().stop();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.eatAnimationTick = 0;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        return this.eatAnimationTick > 0;
    }

    /**
     * Number of ticks since the entity started to eat grass
     */
    public int getEatAnimationTick() {
        return this.eatAnimationTick;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
        if (this.eatAnimationTick != this.adjustedTickDelay(4)) return;

        BlockPos blockPos = this.mob.blockPosition();
        if (EDIBLE_GRASSES.test(this.level.getBlockState(blockPos))) {
            if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.mob)) {
                this.level.destroyBlock(blockPos, false);
            }
            this.mob.ate();
            return;
        }

        BlockPos blockPos1 = blockPos.below();
        BlockState blockState1 = this.level.getBlockState(blockPos1);
        if (EDIBLE_GROUND_BLOCKS.test(blockState1)) {
            if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.mob)) {
                this.level.levelEvent(2001, blockPos1, Block.getId(blockState1));
                this.level.setBlock(blockPos1, LEFTOVER_BLOCK.defaultBlockState(), 2);
            }
            this.mob.ate();
        }
    }
}
