package blue.nightmarish.milked.mixin.model;

import blue.nightmarish.milked.IMilkableCow;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CowModel.class)
public abstract class CowEatAnim<T extends Entity & IMilkableCow> extends QuadrupedModel<T> {
    public CowEatAnim(ModelPart pRoot) {
        super(pRoot, false, 10.0F, 4.0F, 2.0F, 2.0F, 24);
    }


    private float headXRot;

    public void prepareMobModel(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
        super.prepareMobModel(pEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
        //if (pEntity.getCustomName() != null && pEntity.getCustomName().getString() != "Mr. Milk") return;
        this.head.y = 3.0F + pEntity.getHeadEatPositionScale(pPartialTick) * 9.0F;
        this.headXRot = pEntity.getHeadEatAngleScale(pPartialTick);
    }

    /**
     * Sets this entity's model rotation angles
     */
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        this.head.xRot = this.headXRot;
    }
}
