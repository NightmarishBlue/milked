package blue.nightmarish.milked;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.item.Item;

public interface IMilkableBehavior {
    boolean milked$hasMilk();
    void milked$setMilk(boolean desiredState);

    Item milked$getMilkItem();
    EatBlockGoal milked$initGoal();

    ParticleOptions milked$getMilkParticles();

    float milked$getHeadEatPositionScale(float pPartialTick);
    float milked$getHeadEatAngleScale(float pPartialTick);
}
