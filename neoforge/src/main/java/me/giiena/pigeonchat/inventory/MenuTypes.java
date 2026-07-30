package me.giiena.pigeonchat.inventory;

import me.giiena.pigeonchat.PigeonChatCommon;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class MenuTypes {
    private static final Map<Identifier, MenuType<?>> MENU_TYPES = new HashMap<>();

    public static final MenuType<MessengerAnimalMenu> MESSENGER =
            register("messenger_animal", IMenuTypeExtension.create((id, inventory, data) ->
                    new MessengerAnimalMenu(id,
                            inventory,
                            data.readVarInt(),
                            inventory.player,
                            data.readCollection(ArrayList::new, UUIDUtil.STREAM_CODEC),
                            InteractionHand.MAIN_HAND)));
    public static final MenuType<MessengerCageMenu> CAGE =
            register("messenger_cage", IMenuTypeExtension.create((id, inventory, data) ->
                    new MessengerCageMenu(id,
                            inventory,
                            data.readBlockPos(),
                            inventory.player,
                            data.readCollection(ArrayList::new, UUIDUtil.STREAM_CODEC),
                            InteractionHand.MAIN_HAND)));

    public static void registerAll(BiConsumer<Identifier, MenuType<?>> registry) {
        MENU_TYPES.forEach(registry);
    }

    private static <T extends AbstractMessengerMenu> MenuType<T> register(final String id,
                                                                          MenuType<T> type) {
        Identifier key = PigeonChatCommon.identifier(id);
        MENU_TYPES.put(key, type);
        return type;
    }
}
