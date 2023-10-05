package blue.nightmarish.milked.particle;

import blue.nightmarish.milked.MilkedMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MilkedModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MilkedMod.MOD_ID);

    public static final RegistryObject<SimpleParticleType> DRIPPING_MILK =
            PARTICLE_TYPES.register("dripping_milk", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> FALLING_MILK =
            PARTICLE_TYPES.register("falling_milk", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> MILK_SPLASH =
            PARTICLE_TYPES.register("splash_milk", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
