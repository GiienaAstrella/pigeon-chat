package com.ghifari160.pigeonchat.item;

import com.ghifari160.pigeonchat.component.PigeonChatComponents;
import com.ghifari160.pigeonchat.tag.ItemTags;
import com.ghifari160.pigeonchat.util.ContainerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class WritingUtensilItem extends Item {
    public WritingUtensilItem(final Item.Properties properties) {
        properties.stacksTo(1);
        super(properties);
    }

    @Override
    @NonNull
    public InteractionResult use(
            @NonNull Level level,
            Player player,
            @NonNull InteractionHand hand) {
        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ?
                InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack utensil = player.getItemInHand(hand);
        ItemStack other = player.getItemInHand(otherHand);

        if (other.is(ItemTags.WRITABLES)) {
            if (other.is(ItemTags.WRITABLE_LETTERS)) {
                player.openItemGui(other, otherHand);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        if (!ContainerUtils.isRefillable(other) && !ContainerUtils.isDrainable(other)) {
            return InteractionResult.PASS;
        } else if (!utensil.isDamaged()) {
            return InteractionResult.PASS;
        } else if (ContainerUtils.inkColor(utensil) != ContainerUtils.inkColor(other) &&
                ContainerUtils.inkColor(utensil) != null) {
            return InteractionResult.PASS;
        }

        int remainingFill = ContainerUtils.remainingFill(other);
        int filled = ContainerUtils.refill(utensil, remainingFill);
        utensil.set(PigeonChatComponents.INK_COLOR, ContainerUtils.inkColor(other));
        ContainerUtils.drain(other, filled, player, otherHand);
        return InteractionResult.SUCCESS;
    }

    @Override
    @NonNull
    public Component getName(@NonNull ItemStack stack) {
        return ContainerUtils.containerName(stack, super::getName);
    }
}
