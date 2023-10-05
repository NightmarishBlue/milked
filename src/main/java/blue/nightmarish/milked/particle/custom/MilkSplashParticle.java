package blue.nightmarish.milked.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MilkSplashParticle extends LiquidSplashParticle {
    MilkSplashParticle(ClientLevel clientLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        super(clientLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            LiquidSplashParticle liquidSplashParticle = new LiquidSplashParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
            liquidSplashParticle.pickSprite(this.sprite);
            liquidSplashParticle.setColor(1.0F, 1.0F, 1.0F);
            return liquidSplashParticle;
        }
    }
}
