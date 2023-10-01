package blue.nightmarish.milked.particle.custom;

import blue.nightmarish.milked.particle.MilkedModParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MilkDripParticle extends LiquidDripParticle{
    MilkDripParticle(ClientLevel pLevel, double pX, double pY, double pZ) {
        super(pLevel, pX, pY, pZ);
    }

    @OnlyIn(Dist.CLIENT)
    public static class MilkHangProvider implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public MilkHangProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            LiquidDripParticle dripparticle = new LiquidDripHangParticle(pLevel, pX, pY, pZ, MilkedModParticles.FALLING_MILK.get());
            dripparticle.setColor(1.0F, 1.0F, 1.0F);
            dripparticle.pickSprite(this.sprite);
            return dripparticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class MilkFallProvider implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public MilkFallProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            LiquidDripParticle dripparticle = new FallAndLandParticleLiquid(pLevel, pX, pY, pZ, MilkedModParticles.MILK_SPLASH.get());
            dripparticle.setColor(1.0F, 1.0F, 1.0F);
            dripparticle.pickSprite(this.sprite);
            return dripparticle;
        }
    }
}
