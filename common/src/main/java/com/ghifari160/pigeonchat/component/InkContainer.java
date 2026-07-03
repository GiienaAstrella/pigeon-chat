package com.ghifari160.pigeonchat.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

@SuppressWarnings("unused")
public record InkContainer(Optional<Boolean> refillable, Optional<Boolean> drainable) {
    public static final Codec<InkContainer> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.optionalFieldOf("refillable").forGetter(InkContainer::refillable),
                    Codec.BOOL.optionalFieldOf("drainable").forGetter(InkContainer::drainable)
            ).apply(instance, InkContainer::new)
    );
    public static final StreamCodec<ByteBuf, InkContainer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL.apply(ByteBufCodecs::optional), InkContainer::refillable,
            ByteBufCodecs.BOOL.apply(ByteBufCodecs::optional), InkContainer::drainable,
            InkContainer::new
    );

    public static InkContainer withRefillable(boolean refillable) {
        return new InkContainer(refillable ? Optional.of(true) : Optional.empty(),
                Optional.empty());
    }

    public static InkContainer withDrainable(boolean drainable) {
        return new InkContainer(Optional.empty(),
                drainable ? Optional.of(true) : Optional.empty());
    }

    public boolean isRefillable() {
        return this.refillable.orElse(false);
    }

    public boolean isDrainable() {
        return this.drainable.orElse(false);
    }
}
