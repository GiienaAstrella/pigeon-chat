package com.ghifari160.pigeonchat.component;

import com.ghifari160.pigeonchat.PigeonChatCommon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public record Sealed(ItemStack sealItem) implements TooltipProvider {
    public static final Codec<Sealed> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ItemStack.CODEC.fieldOf("seal_item").forGetter(Sealed::sealItem))
                    .apply(instance, Sealed::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, Sealed> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.STREAM_CODEC,
                    Sealed::sealItem,
                    Sealed::new
            );

    public Sealed(ItemStack sealItem) {
        this.sealItem = sealItem.copyWithCount(1);
    }

    public static boolean isSealed(ItemStack stack) {
        Sealed seal = stack.get(PigeonChatComponents.SEALED);
        return seal != null;
    }

    @Override
    public void addToTooltip(
            Item.@NonNull TooltipContext tooltipContext,
            Consumer<Component> consumer,
            @NonNull TooltipFlag tooltipFlag,
            @NonNull DataComponentGetter dataComponentGetter) {
        consumer.accept(Component.translatable(
                PigeonChatCommon.langKey("letter", "tooltip", "sealed"),
                this.sealItem.getItemName()).withColor(TextColor.DARK_PURPLE));
    }
}
