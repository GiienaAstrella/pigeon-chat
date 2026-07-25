package me.giiena.pigeonchat.mixin;

import me.giiena.pigeonchat.item.PigeonChatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract Item getItem();

    @Inject(method = "isDamaged", at = @At("HEAD"), cancellable = true)
    private void pigeonchat$isDamaged(CallbackInfoReturnable<Boolean> cir) {
        if (this.getItem() instanceof PigeonChatItem item) {
            cir.setReturnValue(item.pigeonchat$isDamaged(self()));
        }
    }

    @Inject(method = "getDamageValue", at = @At("HEAD"), cancellable = true)
    private void pigeonchat$getDamageValue(CallbackInfoReturnable<Integer> cir) {
        if (this.getItem() instanceof PigeonChatItem item) {
            cir.setReturnValue(item.pigeonchat$getDamage(self()));
        }
    }

    @Inject(method = "setDamageValue", at = @At("HEAD"), cancellable = true)
    private void pigeonchat$setDamageValue(int value, CallbackInfo ci) {
        if (this.getItem() instanceof PigeonChatItem item) {
            item.pigeonchat$setDamage(self(), value);
            ci.cancel();
        }
    }

    @Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
    private void pigeonchat$getMaxDamage(CallbackInfoReturnable<Integer> cir) {
        if (this.getItem() instanceof PigeonChatItem item) {
            cir.setReturnValue(item.pigeonchat$getMaxDamage(self()));
        }
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Unique
    private ItemStack self() {
        return (ItemStack)(Object) this;
    }
}
