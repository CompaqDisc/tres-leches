package com.compaqdisc.tresleches.mixin;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.MinecartItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow public abstract Item asItem();

    @Inject(method = "getRecipeRemainder", at = @At("HEAD"), cancellable = true)
    public void overrideGetRecipeRemainder(CallbackInfoReturnable<Item> cir) {
        if (isNonRideableMinecart(this.asItem())) {
            cir.setReturnValue(Items.MINECART);
        } else if (isChestedBoat(this.asItem())) {
            cir.setReturnValue(switch (((BoatItemAccessor)this.asItem()).getType()) {
                default -> Items.OAK_BOAT;
                case SPRUCE -> Items.SPRUCE_BOAT;
                case BIRCH -> Items.BIRCH_BOAT;
                case JUNGLE -> Items.JUNGLE_BOAT;
                case ACACIA -> Items.ACACIA_BOAT;
                case DARK_OAK -> Items.DARK_OAK_BOAT;
                case MANGROVE -> Items.MANGROVE_BOAT;
            });
        }
    }

    @Inject(method = "hasRecipeRemainder", at = @At("HEAD"), cancellable = true)
    public void overrideHasRecipeRemainder(CallbackInfoReturnable<Boolean> cir) {
        if (isNonRideableMinecart(this.asItem()) || isChestedBoat(this.asItem())) {
            cir.setReturnValue(true);
        }
    }

    boolean isChestedBoat(Item instance) {
        if (instance instanceof BoatItem boatItem) {
            return ((BoatItemAccessor)boatItem).getChest();
        }
        return false;
    }

    boolean isNonRideableMinecart(Item instance) {
        if (instance instanceof MinecartItem minecartItem) {
            return (((MinecartItemAccessor)minecartItem).getType() != AbstractMinecartEntity.Type.RIDEABLE);
        }
        return false;
    }
}
