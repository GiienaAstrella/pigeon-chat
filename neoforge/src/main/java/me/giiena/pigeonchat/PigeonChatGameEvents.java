package me.giiena.pigeonchat;

import me.giiena.pigeonchat.component.Converted;
import me.giiena.pigeonchat.component.PigeonChatComponents;
import me.giiena.pigeonchat.component.Sealed;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber
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
