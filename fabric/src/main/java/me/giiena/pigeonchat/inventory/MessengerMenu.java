package me.giiena.pigeonchat.inventory;

import me.giiena.pigeonchat.entity.MessengerAnimal;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Menu for {@link MessengerAnimal}.
 */
public class MessengerMenu extends AbstractMessengerMenu {
    protected MessengerMenu(int containerID,
                            Inventory inventory,
                            int messengerID,
                            Player sender,
                            List<UUID> targets,
                            InteractionHand hand) {
        super(MenuTypes.MESSENGER,
                containerID,
                resolveMessenger(inventory, messengerID),
                sender,
                targets,
                hand);
    }

    /**
     * Opens menu for {@link MessengerAnimal}.
     * Implements {@link AbstractMessengerMenu.Opener}.
     */
    @ApiStatus.Internal
    public static void open(ServerPlayer player,
                            MessengerAnimal messenger,
                            List<UUID> targets,
                            InteractionHand hand) {
        player.openMenu(new ExtendedMenuProvider<Data>() {
            @Override
            public AbstractContainerMenu createMenu(int containerId,
                                                    @NonNull Inventory inventory,
                                                    @NonNull Player player) {
                return new MessengerMenu(containerId,
                        inventory,
                        messenger.getId(),
                        player,
                        targets,
                        hand);
            }

            @Override
            @NonNull
            public Component getDisplayName() {
                return TITLE;
            }

            @Override
            @NonNull
            public Data getScreenOpeningData(@NonNull ServerPlayer player) {
                return new Data(messenger.getId(), targets);
            }
        });
    }

    public record Data(int messengerID, List<UUID> targets) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_INT, Data::messengerID,
                        ByteBufCodecs.collection(ArrayList::new, UUIDUtil.STREAM_CODEC),
                        Data::targets,
                        Data::new);
    }
}
