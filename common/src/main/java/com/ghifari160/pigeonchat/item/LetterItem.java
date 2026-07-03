package com.ghifari160.pigeonchat.item;

import com.ghifari160.pigeonchat.component.PigeonChatComponents;
import com.ghifari160.pigeonchat.component.Sealed;
import com.ghifari160.pigeonchat.tag.ItemTags;
import com.ghifari160.pigeonchat.util.ContainerUtils;
import com.ghifari160.pigeonchat.util.InteractionUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class LetterItem extends Item {
    public LetterItem(Properties properties) {
        super(properties);
    }

    @Override
    @NonNull
    public InteractionResult use(
            @NonNull Level level,
            Player player,
            @NonNull InteractionHand hand) {
        ItemStack letter = player.getItemInHand(hand);

        Sealed sealed = letter.get(PigeonChatComponents.SEALED);
        if (sealed != null) {
            ItemStack sealItem = sealed.sealItem();
            if (!player.getInventory().add(sealItem)) {
                player.drop(sealItem, false);
            }
            letter.remove(PigeonChatComponents.SEALED);
            return InteractionResult.SUCCESS;
        }

        ItemStack other = player.getItemInHand(InteractionUtils.otherHand(hand));
        // If other hand is writing utensil, let it handle interaction.
        if (ContainerUtils.isUtensil(other)) {
            return InteractionResult.PASS;
        } else if (other.is(ItemTags.STRINGS)) {
            letter.set(PigeonChatComponents.SEALED, new Sealed(other));
            other.consume(1, player);
            return InteractionResult.SUCCESS;
        }

        player.openItemGui(player.getItemInHand(hand), hand);
        return InteractionResult.SUCCESS;
    }
}
