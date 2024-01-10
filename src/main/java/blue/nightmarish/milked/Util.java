package blue.nightmarish.milked;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class Util {
    public static final int COW_EAT_ANIMATION_TICKS = 40;
    public static final int DROPLET_TICK_COOLDOWN = 10;

    public static void spawnFluidParticle(Level pLevel, double pStartX, double pEndX, double pStartZ, double pEndZ, double pPosY, ParticleOptions pParticleOption) {
        if (!pLevel.isClientSide) return;
        pLevel.addParticle(pParticleOption, Mth.lerp(pLevel.random.nextDouble(), pStartX, pEndX), pPosY, Mth.lerp(pLevel.random.nextDouble(), pStartZ, pEndZ), 0.0D, 0.0D, 0.0D);
    }
}
