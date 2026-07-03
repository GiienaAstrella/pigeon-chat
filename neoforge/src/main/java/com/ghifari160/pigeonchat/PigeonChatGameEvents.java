package com.ghifari160.pigeonchat;

import com.ghifari160.pigeonchat.component.Converted;
import com.ghifari160.pigeonchat.component.PigeonChatComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

public class PigeonChatGameEvents {
    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        PigeonChatConfig.load();
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        Converted converted = stack.get(PigeonChatComponents.CONVERTED);
        if (converted == null) return;
        converted.addToTooltip(
                event.getContext(),
                event.getToolTip()::add,
                event.getFlags(),
                stack);
    }
}
