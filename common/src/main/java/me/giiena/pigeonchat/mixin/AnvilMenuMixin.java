package me.giiena.pigeonchat.mixin;

import me.giiena.pigeonchat.PigeonChatConfig;
import me.giiena.pigeonchat.tag.ItemTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    @Shadow
    private String itemName;

    @Inject(
            method = "createResult",
            at = @At("HEAD")
    )
    private void pigeonchat$lockItemName(CallbackInfo ci) {
        if (PigeonChatConfig.COMMON.getOrDefault(PigeonChatConfig.Key.NAME_TAG_ANVIL_EDITABLE,
                PigeonChatConfig.Default.NAME_TAG_ANVIL_EDITABLE)) return;

        AnvilMenu menu = (AnvilMenu)(Object) this;
        ItemStack input = menu.getSlot(AnvilMenu.INPUT_SLOT).getItem();

        if (input.is(ItemTags.WRITABLE_NAME_TAGS)) {
            this.itemName = input.has(DataComponents.CUSTOM_NAME) ?
                    input.getHoverName().getString() : null;
        }
    }
}
