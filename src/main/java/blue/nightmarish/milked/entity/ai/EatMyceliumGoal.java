package blue.nightmarish.milked.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

public class EatMyceliumGoal extends GenericEatBlockGoal {
    @Override
    protected void initFields() {
        EDIBLE_GRASSES = BlockStatePredicate.forBlock(Blocks.RED_MUSHROOM).or(BlockStatePredicate.forBlock(Blocks.BROWN_MUSHROOM));
        EDIBLE_GROUND_BLOCKS = BlockPredicate.forBlock(Blocks.MYCELIUM);
        LEFTOVER_BLOCK = Blocks.DIRT;
    }

    public EatMyceliumGoal(Mob pMob) {
        super(pMob);
    }
}
