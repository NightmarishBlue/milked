package blue.nightmarish.milked.mixin.entity;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Animal.class)
public abstract class CowAnimalMethods extends AgeableMob {
    protected CowAnimalMethods(EntityType<? extends AgeableMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}
