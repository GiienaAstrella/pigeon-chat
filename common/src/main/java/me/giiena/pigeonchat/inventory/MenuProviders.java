package me.giiena.pigeonchat.inventory;

import me.giiena.pigeonchat.entity.MessengerAnimal;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.UUID;

public class MenuProviders {
    private static AbstractMessengerMenu.Opener messenger;

    /**
     * Sets the {@link AbstractMessengerMenu.Opener} for the messenger menu.
     * This should be called at loader-specific mod initialization.
     */
    @ApiStatus.Internal
    public static void setMessenger(AbstractMessengerMenu.Opener opener) {
        messenger = opener;
    }

    /**
     * Opens the messenger menu.
     */
    public static void openMessenger(ServerPlayer player,
                                     MessengerAnimal messengerAnimal,
                                     List<UUID> targets,
                                     InteractionHand hand) {
        messenger.open(player, messengerAnimal, targets, hand);
    }
}
