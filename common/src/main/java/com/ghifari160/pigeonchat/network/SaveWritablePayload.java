package com.ghifari160.pigeonchat.network;

import com.ghifari160.pigeonchat.PigeonChatCommon;
import com.ghifari160.pigeonchat.component.Converted;
import com.ghifari160.pigeonchat.component.PigeonChatComponents;
import com.ghifari160.pigeonchat.component.Sealed;
import com.ghifari160.pigeonchat.component.Writable;
import com.ghifari160.pigeonchat.item.Items;
import com.ghifari160.pigeonchat.item.LetterItem;
import com.ghifari160.pigeonchat.tag.ItemTags;
import com.ghifari160.pigeonchat.util.ContainerUtils;
import com.ghifari160.pigeonchat.util.InteractionUtils;
import com.ghifari160.pigeonchat.util.StringUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public record SaveWritablePayload(Component contents, InteractionHand hand)
        implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SaveWritablePayload> TYPE =
            new CustomPacketPayload.Type<>(PigeonChatCommon.identifier("save_writable"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SaveWritablePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ComponentSerialization.STREAM_CODEC, SaveWritablePayload::contents,
                    InteractionHand.STREAM_CODEC, SaveWritablePayload::hand,
                    SaveWritablePayload::new);

    @Override
    @NonNull
    public Type<SaveWritablePayload> type() {
        return TYPE;
    }

    public void handle(Player player) {
        ItemStack writable = player.getItemInHand(this.hand);
        ItemStack utensil = player.getItemInHand(InteractionUtils.otherHand(this.hand));

        if (!(writable.is(ItemTags.WRITABLE_LETTERS))) return;
        if (!ContainerUtils.isUtensil(utensil)) return;

        Writable current = writable.getOrDefault(PigeonChatComponents.WRITABLE, Writable.EMPTY);
        if (Sealed.isSealed(writable)) return;

        int writeAmount = StringUtil.countIgnoreWhitespace(this.contents);
        if (ContainerUtils.remainingFill(utensil) < writeAmount) return;

        Component combined = current.contents().copy().append(this.contents);

        if (!(writable.getItem() instanceof LetterItem)) {
            ItemStack letter = new ItemStack(Items.LETTER);
            letter.set(PigeonChatComponents.WRITABLE, current.withText(combined));
            letter.set(PigeonChatComponents.CONVERTED, new Converted(writable));

            writable.consume(1, player);
            if (writable.isEmpty()) {
                player.setItemInHand(this.hand, letter);
            } else {
                if (!player.getInventory().add(letter)) {
                    player.drop(letter, false);
                }
            }
        } else {
            writable.set(PigeonChatComponents.WRITABLE, current.withText(combined));
        }
        ContainerUtils.write(utensil, writeAmount, player, InteractionUtils.otherHand(this.hand));
    }
}
