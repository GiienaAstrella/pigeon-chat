package me.giiena.pigeonchat.inventory;

import me.giiena.pigeonchat.entity.MessengerAnimal;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;

/**
 * Menu for {@link MessengerAnimal}.
 */
public class MessengerAnimalMenu extends AbstractMessengerMenu {
    protected MessengerAnimalMenu(int containerID,
                                  Inventory inventory,
                                  int messengerID,
                                  Player sender,
                                  List<UUID> targets,
                                  InteractionHand hand) {
        super(MenuTypes.MESSENGER,
                containerID,
                MessengerAnimalSource.resolve(inventory, messengerID),
                sender,
                targets,
                hand);
    }

    /**
     * Opens menu for {@link MessengerAnimal}.
     * Implements {@link AbstractMessengerMenu.Opener}.
     */
    public static void open(ServerPlayer player,
                            MessengerMenuSource source,
                            List<UUID> targets,
                            InteractionHand hand) {
        MessengerAnimal messenger = source.messenger();
        if (messenger == null) return;

        MenuProvider provider = new MenuProvider() {
            @Override
            @NonNull
            public Component getDisplayName() {
                return TITLE;
            }

            @Override
            public AbstractContainerMenu createMenu(int containerID,
                                                    @NonNull Inventory inventory,
                                                    @NonNull Player player) {
                return new MessengerAnimalMenu(containerID,
                        inventory,
                        messenger.getId(),
                        player,
                        targets,
                        hand);
            }
        };

        player.openMenu(provider, buf -> {
            buf.writeVarInt(messenger.getId());
            buf.writeCollection(targets, UUIDUtil.STREAM_CODEC);
        });
    }
}
