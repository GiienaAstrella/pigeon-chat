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
    public static final MenuType<MessengerAnimalMenu> MESSENGER =
            IMenuTypeExtension.create((id, inventory, data) ->
                    new MessengerAnimalMenu(id,
                            inventory,
                            data.readVarInt(),
                            inventory.player,
                            data.readCollection(ArrayList::new, UUIDUtil.STREAM_CODEC),
                            InteractionHand.MAIN_HAND));
    public static final MenuType<MessengerCageMenu> CAGE =
            IMenuTypeExtension.create((id, inventory, data) ->
                    new MessengerCageMenu(id,
                            inventory,
                            data.readBlockPos(),
                            inventory.player,
                            data.readCollection(ArrayList::new, UUIDUtil.STREAM_CODEC),
                            InteractionHand.MAIN_HAND));

    public static void register(BiConsumer<MenuType<?>, Identifier> consumer) {
        consumer.accept(MESSENGER, PigeonChatCommon.identifier("messenger_animal"));
        consumer.accept(CAGE, PigeonChatCommon.identifier("messenger_cage"));
    }
}
