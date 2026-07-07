package me.giiena.pigeonchat.inventory;

import me.giiena.pigeonchat.PigeonChatCommon;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.MenuType;

import java.util.function.BiConsumer;

public class MenuTypes {
    public static final ExtendedMenuType<MessengerMenu, MessengerMenu.Data> MESSENGER =
            new ExtendedMenuType<>((containerID, inventory, data) ->
                    new MessengerMenu(containerID,
                            inventory,
                            data.messengerID(),
                            inventory.player,
                            data.targets(),
                            InteractionHand.MAIN_HAND),
                    MessengerMenu.Data.STREAM_CODEC);

    public static void register(BiConsumer<MenuType<?>, Identifier> consumer) {
        consumer.accept(MESSENGER, PigeonChatCommon.identifier("messenger"));
    }
}
