package me.giiena.pigeonchat.inventory;

import me.giiena.pigeonchat.entity.MessengerAnimal;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

public interface MessengerMenuSource {
    /**
     * Returns live reference to MessengerAnimal.
     */
    @Nullable MessengerAnimal messenger();

    /**
     * Returns {@code true} as long as {@code player} can still interact with the menu.
     */
    boolean stillValid(Player player);

    /**
     * Creates a delivery job bound to {@code target}.
     */
    void assignMessenger(Player sender, InteractionHand hand, ServerPlayer target);
}
