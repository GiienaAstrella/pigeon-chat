package me.giiena.pigeonchat.inventory;

import me.giiena.pigeonchat.PigeonChatCommon;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.MenuType;

import java.util.function.BiConsumer;

public class MenuTypes {
    public static final ExtendedMenuType<MessengerMenu, Integer> MESSENGER =
            new ExtendedMenuType<>((containerID, inventory, messengerID) ->
                    new MessengerMenu(containerID,
                            inventory,
                            messengerID,
                            inventory.player,
                            InteractionHand.MAIN_HAND),
                    ByteBufCodecs.VAR_INT);

    public static void register(BiConsumer<MenuType<?>, Identifier> consumer) {
        consumer.accept(MESSENGER, PigeonChatCommon.identifier("messenger"));
    }
}
