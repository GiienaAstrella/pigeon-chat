package com.ghifari160.pigeonchat.component;

import com.ghifari160.pigeonchat.PigeonChatCommon;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;

import java.util.function.BiConsumer;

public class PigeonChatComponents {
    public static DataComponentType<InkContainer> INK_CONTAINER;
    public static DataComponentType<Unit> UTENSIL;
    public static DataComponentType<DyeColor> INK_COLOR;
    public static DataComponentType<Converted> CONVERTED;
    public static DataComponentType<Boolean> UNBREAKABLE;
    public static DataComponentType<Writable> WRITABLE;
    public static DataComponentType<Sealed> SEALED;

    public static void register(BiConsumer<DataComponentType<?>, Identifier> consumer) {
        INK_CONTAINER = DataComponentType.<InkContainer>builder()
                .persistent(InkContainer.CODEC)
                .networkSynchronized(InkContainer.STREAM_CODEC)
                .build();
        UTENSIL = DataComponentType.<Unit>builder()
                .persistent(Unit.CODEC)
                .networkSynchronized(Unit.STREAM_CODEC)
                .build();
        INK_COLOR = DataComponentType.<DyeColor>builder()
                .persistent(DyeColor.CODEC)
                .networkSynchronized(DyeColor.STREAM_CODEC)
                .build();
        CONVERTED = DataComponentType.<Converted>builder()
                .persistent(Converted.CODEC)
                .networkSynchronized(Converted.STREAM_CODEC)
                .build();
        UNBREAKABLE = DataComponentType.<Boolean>builder()
                .persistent(Codec.BOOL)
                .networkSynchronized(ByteBufCodecs.BOOL)
                .build();
        WRITABLE = DataComponentType.<Writable>builder()
                .persistent(Writable.CODEC)
                .networkSynchronized(Writable.STREAM_CODEC)
                .build();
        SEALED = DataComponentType.<Sealed>builder()
                .persistent(Sealed.CODEC)
                .networkSynchronized(Sealed.STREAM_CODEC)
                .build();

        consumer.accept(INK_CONTAINER, PigeonChatCommon.identifier("ink_container"));
        consumer.accept(INK_COLOR, PigeonChatCommon.identifier("ink_color"));
        consumer.accept(CONVERTED, PigeonChatCommon.identifier("converted"));
        consumer.accept(UNBREAKABLE, PigeonChatCommon.identifier("unbreakable"));
        consumer.accept(WRITABLE, PigeonChatCommon.identifier("writable"));
        consumer.accept(SEALED, PigeonChatCommon.identifier("sealed"));
    }
}
