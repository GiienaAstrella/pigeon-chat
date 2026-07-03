package me.giiena.pigeonchat.component;

import me.giiena.pigeonchat.PigeonChatCommon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public record Converted(ItemStack baseItem) implements TooltipProvider {
    public Converted(ItemStack baseItem) {
        this.baseItem = baseItem.copyWithCount(1);
    }

    public static final Codec<Converted> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemStack.CODEC.fieldOf("base_item").forGetter(Converted::baseItem)
            ).apply(instance, Converted::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Converted> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            Converted::baseItem,
            Converted::new
    );

    @Override
    public void addToTooltip(
            Item.@NonNull TooltipContext tooltipContext,
            Consumer<Component> consumer,
            @NonNull TooltipFlag tooltipFlag,
            @NonNull DataComponentGetter dataComponentGetter) {
        consumer.accept(Component.translatable(
                PigeonChatCommon.langKey("converted", "tooltip"),
                this.baseItem().getItemName()));
    }
}
