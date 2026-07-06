package me.giiena.pigeonchat.inventory;

import me.giiena.pigeonchat.entity.MessengerAnimal;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

/**
 * Menu for {@link MessengerAnimal}.
 */
public class MessengerMenu extends AbstractMessengerMenu {
    protected MessengerMenu(int containerID,
                            Inventory inventory,
                            int messengerID,
                            Player sender,
                            InteractionHand hand) {
        super(MenuTypes.MESSENGER,
                containerID,
                resolveMessenger(inventory, messengerID),
                sender,
                hand);
    }

    /**
     * Opens menu for {@link MessengerAnimal}.
     * Implements {@link AbstractMessengerMenu.Opener}.
     */
    @ApiStatus.Internal
    public static void open(ServerPlayer player, MessengerAnimal messenger, InteractionHand hand) {
        player.openMenu(new ExtendedMenuProvider<Integer>() {
            @Override
            public AbstractContainerMenu createMenu(int containerId,
                                                    @NonNull Inventory inventory,
                                                    @NonNull Player player) {
                return new MessengerMenu(containerId,
                        inventory,
                        messenger.getId(),
                        player,
                        hand);
            }

            @Override
            @NonNull
            public Component getDisplayName() {
                return TITLE;
            }

            @Override
            @NonNull
            public Integer getScreenOpeningData(@NonNull ServerPlayer player) {
                return messenger.getId();
            }
        });
    }
}
