package blue.nightmarish.milked.mixin.entity.parents;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AgeableMob.class)
public abstract class CowAgeableMethods extends PathfinderMob {
    protected CowAgeableMethods(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}
