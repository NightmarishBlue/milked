package blue.nightmarish.milked;

public interface IMilkableCow {
    boolean milked$hasMilk();
    void milked$setMilk(boolean desiredState);
    float milked$getHeadEatPositionScale(float pPartialTick);
    float milked$getHeadEatAngleScale(float pPartialTick);
}
