package com.ghifari160.pigeonchat.item;

import com.ghifari160.pigeonchat.util.ContainerUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class InkContainerItem extends Item {
    public InkContainerItem(final Item.Properties properties) {
        properties.stacksTo(1);
        properties.component(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
        super(properties);
    }

    @Override
    @NonNull
    public InteractionResult use(
            @NonNull Level level,
            Player player,
            @NonNull InteractionHand hand) {
        ItemStack container = player.getItemInHand(hand);
        ItemStack other = player.getItemInHand(hand == InteractionHand.MAIN_HAND ?
                InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);

        if (ContainerUtils.isRefillable(other) && !ContainerUtils.isDrainable(other)) {
            // Not a bottle.
            // Delegate refill to the other item.
            return InteractionResult.PASS;
        } else if (ContainerUtils.isRefillable(other) && ContainerUtils.isDrainable(other)) {
            // Other item is a bottle.
            // Top them up.
            if (ContainerUtils.inkColor(container) != ContainerUtils.inkColor(other)) {
                return InteractionResult.PASS;
            } else if (!other.isDamaged()) {
                return InteractionResult.PASS;
            }

            int remainingFill = ContainerUtils.remainingFill(container);
            int filled = ContainerUtils.refill(other, remainingFill);
            ContainerUtils.drain(container, filled, player, hand);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    @NonNull
    public Component getName(@NonNull ItemStack stack) {
        return ContainerUtils.containerName(stack, super::getName);
    }
}
