package blue.nightmarish.milked.mixin.entity;

import blue.nightmarish.milked.IMilkableBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MushroomCow.class)
public abstract class MilkableMooshroom extends Cow implements IMilkableBehavior {
    public MilkableMooshroom(EntityType<? extends Cow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Unique
    @Override
    public Item milked$getMilkItem() {
        return Items.BOWL;
    }

    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    boolean modifyMobInteract(ItemStack heldItemStack, Item emptyBowl) {
        if (!(heldItemStack.is(this.milked$getMilkItem()) && this.milked$hasMilk() && !this.isBaby())) return false;
        this.milked$setMilk(false);
        return true;
    }
}
