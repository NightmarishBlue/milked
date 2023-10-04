package blue.nightmarish.milked.mixin.entity;

import blue.nightmarish.milked.IMilkableBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MushroomCow.class)
public abstract class MilkableMooshroom extends Cow implements IMilkableBehavior {
    public MilkableMooshroom(EntityType<? extends Cow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Unique
    public Item milked$getMilkItem() {
        return Items.BOWL;
    }
}
