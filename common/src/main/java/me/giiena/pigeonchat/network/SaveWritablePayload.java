package me.giiena.pigeonchat.network;

import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.component.Converted;
import me.giiena.pigeonchat.component.PigeonChatComponents;
import me.giiena.pigeonchat.component.Sealed;
import me.giiena.pigeonchat.component.Writable;
import me.giiena.pigeonchat.item.Items;
import me.giiena.pigeonchat.item.LetterItem;
import me.giiena.pigeonchat.tag.ItemTags;
import me.giiena.pigeonchat.util.ContainerUtils;
import me.giiena.pigeonchat.util.InteractionUtils;
import me.giiena.pigeonchat.util.StringUtil;
import net.minecraft.core.component.DataComponents;
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

        if (!writable.is(ItemTags.WRITABLES)) return;

        int writeAmount = StringUtil.countIgnoreWhitespace(this.contents);
        if (ContainerUtils.remainingFill(utensil) < writeAmount) return;

        if (writable.is(ItemTags.WRITABLE_LETTERS)) {
            if (Sealed.isSealed(writable)) return;
            this.handleLetter(player, writable);
        } else if (writable.is(ItemTags.WRITABLE_NAME_TAGS)) {
            this.handleNameTag(player, writable);
        }

        ContainerUtils.write(utensil, writeAmount, player, InteractionUtils.otherHand(this.hand));
    }

    private void handleLetter(Player player, ItemStack writable) {
        Writable current = writable.getOrDefault(PigeonChatComponents.WRITABLE, Writable.EMPTY);
        Component combined = current.contents().copy().append(this.contents);

        if (writable.getItem() instanceof LetterItem) {
            writable.set(PigeonChatComponents.WRITABLE, current.withText(combined));
        } else {
            ItemStack letter = new ItemStack(Items.LETTER);
            letter.set(PigeonChatComponents.WRITABLE, current.withText(combined));
            letter.set(PigeonChatComponents.CONVERTED, new Converted(writable));

            writable.consume(1, player);
            if (writable.isEmpty()) {
                player.setItemInHand(this.hand, letter);
            } else if (!player.getInventory().add(letter)) {
                player.drop(letter, false);
            }
        }
    }

    @SuppressWarnings("unused")
    private void handleNameTag(Player player, ItemStack writable) {
        Component current = writable.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty());
        writable.set(DataComponents.CUSTOM_NAME, current.copy().append(this.contents));
    }
}
