package com.ghifari160.pigeonchat.mixin.client;

import com.ghifari160.pigeonchat.client.screen.ItemScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Inject(
            method = "openItemGui",
            at = @At("HEAD"),
            cancellable = true
    )
    public void openItemGui(ItemStack itemStack, InteractionHand hand, CallbackInfo ci) {
        if (ItemScreen.open((LocalPlayer)(Object) this, itemStack, hand)) ci.cancel();
    }
}
