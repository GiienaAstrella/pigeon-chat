package me.giiena.pigeonchat.inventory;

import com.google.common.base.Preconditions;
import me.giiena.pigeonchat.Constants;
import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.entity.MessengerAnimal;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

/**
 * Common menu code for {@link MessengerAnimal}.
 * Also see the loader specific {@link MessengerMenu}.
 */
@SuppressWarnings("JavadocReference")
public abstract class AbstractMessengerMenu extends AbstractContainerMenu {
    public static final Component TITLE =
            Component.translatable(PigeonChatCommon.langKey(
                    "messenger",
                    "gui",
                    "title"));

    private final MessengerAnimal messenger;
    private final Player sender;
    private final InteractionHand hand;

    protected AbstractMessengerMenu(final MenuType<? extends AbstractMessengerMenu> type,
                                    final int id,
                                    final MessengerAnimal messenger,
                                    final Player sender,
                                    final InteractionHand hand) {
        super(type, id);
        this.messenger = messenger;
        this.sender = sender;
        this.hand = hand;
    }

    @NonNull
    protected static MessengerAnimal resolveMessenger(Inventory inventory, int entityID) {
        Entity entity = inventory.player.level().getEntity(entityID);
        Preconditions.checkNotNull(entity, "null entity provided");
        if (entity instanceof MessengerAnimal messenger) {
            return messenger;
        }
        throw new IllegalStateException(entity.getStringUUID() + " is not MessengerAnimal");
    }

    @Override
    @NonNull
    public ItemStack quickMoveStack(@NonNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return this.messenger.isAlive() &&
                !this.messenger.isCarrying() &&
                player.isWithinEntityInteractionRange(this.messenger, 4.0d);
    }

    /**
     * Creates a delivery job bound to {@code target} and assigns {@link #messenger}.
     */
    public void assignMessenger(ServerPlayer target) {
        ItemStack held = this.sender.getItemInHand(this.hand);
        if (this.messenger.isCarrying() || held.isEmpty() ||
                !this.messenger.isDeliverable(held)) {
            return;
        }

        ItemStack carrying = held.split(1);
        this.messenger.carrying(carrying);
        this.messenger.target(target);
        this.messenger.sender(this.sender);
        Constants.LOG.info("{} assigned delivery job (bound to {}) to {}",
                this.sender.getName().getString(),
                target.getName().getString(),
                this.messenger.getReportableName());
    }

    /**
     * Interface implemented by the loader specific opener.
     */
    @ApiStatus.Internal
    @FunctionalInterface
    public interface Opener {
        /**
         * Opens menu for {@link MessengerAnimal}.
         */
        void open(ServerPlayer player, MessengerAnimal messenger, InteractionHand hand);
    }
}
