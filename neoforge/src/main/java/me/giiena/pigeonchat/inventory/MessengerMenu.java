package me.giiena.pigeonchat.inventory;

import me.giiena.pigeonchat.client.screen.TargetSelectionScreen;
import me.giiena.pigeonchat.entity.MessengerAnimal;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
    public static void open(ServerPlayer player, MessengerAnimal messenger, InteractionHand hand) {
        MenuProvider provider = new MenuProvider() {
            @Override
            @NonNull
            public Component getDisplayName() {
                return TargetSelectionScreen.TITLE;
            }

            @Override
            public AbstractContainerMenu createMenu(int containerID,
                                                    @NonNull Inventory inventory,
                                                    @NonNull Player player) {
                return new MessengerMenu(containerID,
                        inventory,
                        messenger.getId(),
                        player,
                        hand);
            }
        };

        player.openMenu(provider, buf -> buf.writeVarInt(messenger.getId()));
    }
}
