package blue.nightmarish.milked;

import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.item.Item;

public interface IMilkableBehavior {
    boolean milked$hasMilk();
    void milked$setMilk(boolean desiredState);

    Item milked$getMilkItem();
    EatBlockGoal milked$initGoal();

    float milked$getHeadEatPositionScale(float pPartialTick);
    float milked$getHeadEatAngleScale(float pPartialTick);
}
