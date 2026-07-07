package me.giiena.pigeonchat.inventory;

import me.giiena.pigeonchat.PigeonChatCommon;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class MenuTypes {
    public static final MenuType<MessengerMenu> MESSENGER =
            IMenuTypeExtension.create((id, inventory, data) ->
                    new MessengerMenu(id,
                            inventory,
                            data.readVarInt(),
                            inventory.player,
                            data.readCollection(ArrayList::new, UUIDUtil.STREAM_CODEC),
                            InteractionHand.MAIN_HAND));

    public static void register(BiConsumer<MenuType<?>, Identifier> consumer) {
        consumer.accept(MESSENGER, PigeonChatCommon.identifier("messenger"));
    }
}
