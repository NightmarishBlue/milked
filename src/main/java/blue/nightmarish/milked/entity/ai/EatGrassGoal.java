package blue.nightmarish.milked.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;

public class EatGrassGoal extends GenericEatBlockGoal {
    @Override
    protected void initFields() {
        super.initFields();

        LEFTOVER_BLOCK = Blocks.DIRT;
    }

    public EatGrassGoal(Mob pMob) {
        super(pMob);
    }
}
