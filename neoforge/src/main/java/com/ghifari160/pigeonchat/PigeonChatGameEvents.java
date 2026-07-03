package com.ghifari160.pigeonchat;

import com.ghifari160.pigeonchat.component.Converted;
import com.ghifari160.pigeonchat.component.PigeonChatComponents;
import com.ghifari160.pigeonchat.component.Sealed;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class PigeonChatGameEvents {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        Converted converted = stack.get(PigeonChatComponents.CONVERTED);
        if (converted != null) {
            converted.addToTooltip(
                    event.getContext(),
                    event.getToolTip()::add,
                    event.getFlags(),
                    stack);
        }

        Sealed sealed = stack.get(PigeonChatComponents.SEALED);
        if (sealed != null) {
            sealed.addToTooltip(
                    event.getContext(),
                    event.getToolTip()::add,
                    event.getFlags(),
                    stack);
        }
    }
}
