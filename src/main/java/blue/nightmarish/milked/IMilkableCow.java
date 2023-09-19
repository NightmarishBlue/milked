package blue.nightmarish.milked;

public interface IMilkableCow {
    boolean hasMilk();
    void setMilk(boolean desiredState);
    float getHeadEatPositionScale(float pPartialTick);
    float getHeadEatAngleScale(float pPartialTick);
}
