package blue.nightmarish.milked.particle.custom;

import blue.nightmarish.milked.MilkedMod;
import blue.nightmarish.milked.particle.MilkedModParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class StewDripParticle extends LiquidDripParticle {
    StewDripParticle(ClientLevel pLevel, double pX, double pY, double pZ) {
        super(pLevel, pX, pY, pZ);
    }

    public static class StewHangProvider implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public StewHangProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            LiquidDripParticle dripparticle = new LiquidDripHangParticle(pLevel, pX, pY, pZ, MilkedModParticles.FALLING_STEW.get(), MilkedMod.getDripLifetime());
            dripparticle.setColor(0.85F, 0.55F, 0.44F);
            dripparticle.pickSprite(this.sprite);
            return dripparticle;
        }
    }
    
    public static class StewFallProvider implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public StewFallProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            LiquidDripParticle dripparticle = new FallAndLandParticleLiquid(pLevel, pX, pY, pZ, MilkedModParticles.STEW_SPLASH.get());
            dripparticle.setColor(0.85F, 0.55F, 0.44F);
            dripparticle.pickSprite(this.sprite);
            return dripparticle;
        }
    }
}
