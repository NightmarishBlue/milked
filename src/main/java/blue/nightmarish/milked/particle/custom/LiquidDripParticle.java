package blue.nightmarish.milked.particle.custom;

import blue.nightmarish.milked.MilkedMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LiquidDripParticle extends TextureSheetParticle {
    // there are no fluid types in this mod (because I couldn't be bothered to figure out how that works and these are not to be placed anywhere)
    protected boolean isGlowing;

    LiquidDripParticle(ClientLevel pLevel, double pX, double pY, double pZ) {
        super(pLevel, pX, pY, pZ);
        this.setSize(0.01F, 0.01F);
        this.gravity = 0.06F;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public int getLightColor(float pPartialTick) {
        return this.isGlowing ? 240 : super.getLightColor(pPartialTick);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.preMoveUpdate();
        if (!this.removed) {
            this.yd -= this.gravity;
            this.move(this.xd, this.yd, this.zd);
            this.postMoveUpdate();
            if (!this.removed) {
                this.xd *= 0.98F;
                this.yd *= 0.98F;
                this.zd *= 0.98F;
                BlockPos blockpos = BlockPos.containing(this.x, this.y, this.z);
                FluidState fluidstate = this.level.getFluidState(blockpos); // if you wanna restore fluids, add in a check here to see if both fluids' types match before removing
                if (this.y < (double)((float)blockpos.getY() + fluidstate.getHeight(this.level, blockpos))) {
                    this.remove();
                }

            }
        }
    }

    protected void preMoveUpdate() {
        if (this.lifetime-- <= 0) {
            this.remove();
        }

    }

    protected void postMoveUpdate() {
    }

    static class CoolingLiquidDripHangParticle extends LiquidDripHangParticle {
        CoolingLiquidDripHangParticle(ClientLevel p_106068_, double p_106069_, double p_106070_, double p_106071_, ParticleOptions p_106073_) {
            super(p_106068_, p_106069_, p_106070_, p_106071_, p_106073_, MilkedMod.getDripLifetime());
        }

        protected void preMoveUpdate() {
            this.rCol = 1.0F;
            this.gCol = 16.0F / (float)(40 - this.lifetime + 16);
            this.bCol = 4.0F / (float)(40 - this.lifetime + 8);
            super.preMoveUpdate();
        }
    }

    static class LiquidDripHangParticle extends LiquidDripParticle {
        private final ParticleOptions fallingParticle;

        LiquidDripHangParticle(ClientLevel pLevel, double pX, double pY, double pZ, ParticleOptions pFallingParticle, int lifetime) {
            super(pLevel, pX, pY, pZ);
            this.fallingParticle = pFallingParticle;
            this.gravity *= 0.025F;
            this.lifetime = lifetime;
        }

        protected void preMoveUpdate() {
            if (this.lifetime-- <= 0) {
                this.remove();
                this.level.addParticle(this.fallingParticle, this.x, this.y, this.z, this.xd, this.yd, this.zd);
            }

        }

        protected void postMoveUpdate() {
            this.xd *= 0.02D;
            this.yd *= 0.02D;
            this.zd *= 0.02D;
        }
    }

    static class LiquidDripLandParticle extends LiquidDripParticle {
        LiquidDripLandParticle(ClientLevel p_106102_, double p_106103_, double p_106104_, double p_106105_) {
            super(p_106102_, p_106103_, p_106104_, p_106105_);
            this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
        }
    }

    static class FallAndLandParticleLiquid extends FallingParticleLiquid {
        protected final ParticleOptions landParticle;

        FallAndLandParticleLiquid(ClientLevel pLevel, double pX, double pY, double pZ, ParticleOptions pLandParticle) {
            super(pLevel, pX, pY, pZ);
            this.landParticle = pLandParticle;
        }

        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
            }

        }
    }

    static class FallingParticleLiquid extends LiquidDripParticle {
        FallingParticleLiquid(ClientLevel pLevel, double pX, double pY, double pZ) {
            this(pLevel, pX, pY, pZ, (int)(64.0D / (Math.random() * 0.8D + 0.2D)));
        }

        FallingParticleLiquid(ClientLevel pLevel, double pX, double pY, double pZ, int pLifetime) {
            super(pLevel, pX, pY, pZ);
            this.lifetime = pLifetime;
        }

        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
            }

        }
    }

//    @OnlyIn(Dist.CLIENT)
//    static class HoneyFallAndLandParticleLiquid extends FallAndLandParticleLiquid {
//        HoneyFallAndLandParticleLiquid(ClientLevel p_106146_, double p_106147_, double p_106148_, double p_106149_, ParticleOptions p_106151_) {
//            super(p_106146_, p_106147_, p_106148_, p_106149_, p_106151_);
//        }
//
//        protected void postMoveUpdate() {
//            if (this.onGround) {
//                this.remove();
//                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
//                float f = Mth.randomBetween(this.random, 0.3F, 1.0F);
//                this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, f, 1.0F, false);
//            }
//
//        }
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public static class HoneyFallProvider implements ParticleProvider<SimpleParticleType> {
//        protected final SpriteSet sprite;
//
//        public HoneyFallProvider(SpriteSet pSprites) {
//            this.sprite = pSprites;
//        }
//
//        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
//            LiquidDripParticle dripparticle = new HoneyFallAndLandParticleLiquid(pLevel, pX, pY, pZ, ParticleTypes.LANDING_HONEY);
//            dripparticle.gravity = 0.01F;
//            dripparticle.setColor(0.582F, 0.448F, 0.082F);
//            dripparticle.pickSprite(this.sprite);
//            return dripparticle;
//        }
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public static class HoneyHangProvider implements ParticleProvider<SimpleParticleType> {
//        protected final SpriteSet sprite;
//
//        public HoneyHangProvider(SpriteSet pSprites) {
//            this.sprite = pSprites;
//        }
//
//        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
//            LiquidDripHangParticle dripparticle$driphangparticle = new LiquidDripHangParticle(pLevel, pX, pY, pZ, ParticleTypes.FALLING_HONEY);
//            dripparticle$driphangparticle.gravity *= 0.01F;
//            dripparticle$driphangparticle.lifetime = 100;
//            dripparticle$driphangparticle.setColor(0.622F, 0.508F, 0.082F);
//            dripparticle$driphangparticle.pickSprite(this.sprite);
//            return dripparticle$driphangparticle;
//        }
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public static class HoneyLandProvider implements ParticleProvider<SimpleParticleType> {
//        protected final SpriteSet sprite;
//
//        public HoneyLandProvider(SpriteSet pSprites) {
//            this.sprite = pSprites;
//        }
//
//        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
//            LiquidDripParticle dripparticle = new LiquidDripLandParticle(pLevel, pX, pY, pZ);
//            dripparticle.lifetime = (int)(128.0D / (Math.random() * 0.8D + 0.2D));
//            dripparticle.setColor(0.522F, 0.408F, 0.082F);
//            dripparticle.pickSprite(this.sprite);
//            return dripparticle;
//        }
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public static class LavaFallProvider implements ParticleProvider<SimpleParticleType> {
//        protected final SpriteSet sprite;
//
//        public LavaFallProvider(SpriteSet pSprites) {
//            this.sprite = pSprites;
//        }
//
//        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
//            LiquidDripParticle dripparticle = new FallAndLandParticleLiquid(pLevel, pX, pY, pZ, ParticleTypes.LANDING_LAVA);
//            dripparticle.setColor(1.0F, 0.2857143F, 0.083333336F);
//            dripparticle.pickSprite(this.sprite);
//            return dripparticle;
//        }
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public static class LavaHangProvider implements ParticleProvider<SimpleParticleType> {
//        protected final SpriteSet sprite;
//
//        public LavaHangProvider(SpriteSet pSprites) {
//            this.sprite = pSprites;
//        }
//
//        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
//            CoolingLiquidDripHangParticle dripparticle$coolingdriphangparticle = new CoolingLiquidDripHangParticle(pLevel, pX, pY, pZ, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
//            dripparticle$coolingdriphangparticle.pickSprite(this.sprite);
//            return dripparticle$coolingdriphangparticle;
//        }
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public static class LavaLandProvider implements ParticleProvider<SimpleParticleType> {
//        protected final SpriteSet sprite;
//
//        public LavaLandProvider(SpriteSet pSprites) {
//            this.sprite = pSprites;
//        }
//
//        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
//            LiquidDripParticle dripparticle = new LiquidDripLandParticle(pLevel, pX, pY, pZ);
//            dripparticle.setColor(1.0F, 0.2857143F, 0.083333336F);
//            dripparticle.pickSprite(this.sprite);
//            return dripparticle;
//        }
//    }

//    @OnlyIn(Dist.CLIENT)
//    public static class MilkFallProvider implements ParticleProvider<SimpleParticleType> {
//        protected final SpriteSet sprite;
//
//        public MilkFallProvider(SpriteSet pSprites) {
//            this.sprite = pSprites;
//        }
//
//        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
//            LiquidDripParticle dripparticle = new FallAndLandParticleLiquid(pLevel, pX, pY, pZ, ParticleTypes.SPLASH);
//            dripparticle.setColor(1.0F, 1.0F, 1.0F);
//            dripparticle.pickSprite(this.sprite);
//            return dripparticle;
//        }
//    }

//    @OnlyIn(Dist.CLIENT)
//    public static class MilkHangProvider implements ParticleProvider<SimpleParticleType> {
//        protected final SpriteSet sprite;
//
//        public MilkHangProvider(SpriteSet pSprites) {
//            this.sprite = pSprites;
//        }
//
//        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
//            LiquidDripParticle dripparticle = new LiquidDripHangParticle(pLevel, pX, pY, pZ, MilkedModParticles.DRIPPING_MILK_REG.get());
//            dripparticle.setColor(1.0F, 1.0F, 1.0F);
//            dripparticle.pickSprite(this.sprite);
//            return dripparticle;
//        }
//    }
}