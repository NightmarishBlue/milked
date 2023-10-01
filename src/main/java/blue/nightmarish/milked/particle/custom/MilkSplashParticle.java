package blue.nightmarish.milked.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MilkSplashParticle extends LiquidSplashParticle {
    MilkSplashParticle(ClientLevel clientLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        super(clientLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.setColor(1.0F, 1.0F, 1.0F);
    }
}
