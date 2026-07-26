package me.giiena.pigeonchat.inventory;

import com.google.common.base.Preconditions;
import me.giiena.pigeonchat.Constants;
import me.giiena.pigeonchat.entity.MessengerAnimal;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record MessengerAnimalSource(MessengerAnimal messenger) implements MessengerMenuSource {
    public static MessengerAnimalSource resolve(Inventory inventory, int entityID) {
        Entity entity = inventory.player.level().getEntity(entityID);
        Preconditions.checkNotNull(entity, "null entity provided");
        if (entity instanceof MessengerAnimal messenger) {
            return new MessengerAnimalSource(messenger);
        }
        throw new IllegalStateException(entity.getStringUUID() + " is not MessengerAnimal");
    }

    @Override
    public boolean stillValid(Player player) {
        return this.messenger.isAlive() &&
                !this.messenger.isCarrying() &&
                player.isWithinEntityInteractionRange(this.messenger, 4.0d);
    }

    @Override
    public void assignMessenger(Player sender, InteractionHand hand, ServerPlayer target) {
        ItemStack held = sender.getItemInHand(hand);
        if (this.messenger.isCarrying() || held.isEmpty() || !this.messenger.isDeliverable(held)) {
            return;
        }

        ItemStack carrying = held.split(1);
        this.messenger.carrying(carrying);
        this.messenger.target(target);
        this.messenger.sender(sender);
        Constants.LOG.info("{} assigned delivery job (bound to {}) to {}",
                sender.getName().getString(),
                target.getName().getString(),
                this.messenger.getReportableName());
    }
}
