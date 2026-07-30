package me.giiena.pigeonchat.inventory;

import me.giiena.pigeonchat.PigeonChatCommon;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class MenuTypes {
    private static final Map<Identifier, MenuType<?>> MENU_TYPES = new HashMap<>();

    public static final ExtendedMenuType<MessengerAnimalMenu, MessengerAnimalMenu.Data> MESSENGER =
            register("messenger_animal",
                    new ExtendedMenuType<>((containerID, inventory, data) ->
                                    new MessengerAnimalMenu(containerID,
                                            inventory,
                                            data.messengerID(),
                                            inventory.player,
                                            data.targets(),
                                            InteractionHand.MAIN_HAND),
                            MessengerAnimalMenu.Data.STREAM_CODEC));
    public static final ExtendedMenuType<MessengerCageMenu, MessengerCageMenu.Data> CAGE =
            register("messenger_cage",
                    new ExtendedMenuType<>((containerID, inventory, data) ->
                                new MessengerCageMenu(containerID,
                                        inventory,
                                        data.pos(),
                                        inventory.player,
                                        data.targets(),
                                        InteractionHand.MAIN_HAND),
                            MessengerCageMenu.Data.STREAM_CODEC));

    public static void registerAll(BiConsumer<Identifier, MenuType<?>> registry) {
        MENU_TYPES.forEach(registry);
    }

    private static <T extends AbstractContainerMenu, D> ExtendedMenuType<T, D> register(
            final String id,
            ExtendedMenuType<T, D> type) {
        Identifier key = PigeonChatCommon.identifier(id);
        MENU_TYPES.put(key, type);
        return type;
    }
}
