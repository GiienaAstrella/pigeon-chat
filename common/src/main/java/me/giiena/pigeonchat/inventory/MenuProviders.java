package me.giiena.pigeonchat.inventory;

import me.giiena.pigeonchat.block.BirdCageEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.UUID;

public class MenuProviders {
    private static AbstractMessengerMenu.Opener messenger;
    private static AbstractMessengerMenu.Opener cage;

    /**
     * Sets the {@link AbstractMessengerMenu.Opener} for the messenger menu.
     * This should be called at loader-specific mod initialization.
     */
    @ApiStatus.Internal
    public static void setMessenger(AbstractMessengerMenu.Opener opener) {
        messenger = opener;
    }

    /**
     * Sets the {@link AbstractMessengerMenu.Opener} for the cage menu.
     * This should be called at loader-specific mod initialization.
     */
    @ApiStatus.Internal
    public static void setCage(AbstractMessengerMenu.Opener opener) {
        cage = opener;
    }

    /**
     * Opens the messenger menu.
     */
    public static void openMessenger(ServerPlayer player,
                                     MessengerMenuSource source,
                                     List<UUID> targets,
                                     InteractionHand hand) {
        if (source instanceof MessengerAnimalSource src) {
            messenger.open(player, src, targets, hand);
        } else if (source instanceof BirdCageEntity src) {
            cage.open(player, src, targets, hand);
        }
    }
}
