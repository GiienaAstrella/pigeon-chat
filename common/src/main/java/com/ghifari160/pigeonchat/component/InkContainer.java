package com.ghifari160.pigeonchat.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;

import java.util.Optional;

public record InkContainer(Optional<DyeColor> color, boolean refillable, boolean drainable) {
    public static final Codec<InkContainer> MAP_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    DyeColor.CODEC.optionalFieldOf("color").forGetter(InkContainer::color),
                    Codec.BOOL.fieldOf("refillable").forGetter(InkContainer::refillable),
                    Codec.BOOL.fieldOf("drainable").forGetter(InkContainer::drainable)
            ).apply(instance, InkContainer::new)
    );
    public static final StreamCodec<ByteBuf, InkContainer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(
                    ByteBufCodecs.VAR_INT.map(DyeColor::byId, DyeColor::getId)
            ), InkContainer::color,
            ByteBufCodecs.BOOL, InkContainer::refillable,
            ByteBufCodecs.BOOL, InkContainer::drainable,
            InkContainer::new
    );

    public InkContainer(boolean refillable, boolean drainable) {
        this(Optional.empty(), refillable, drainable);
    }

    public InkContainer(DyeColor color, boolean refillable, boolean drainable) {
        this(Optional.of(color), refillable, drainable);
    }
}
