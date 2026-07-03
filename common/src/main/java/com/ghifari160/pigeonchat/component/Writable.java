package com.ghifari160.pigeonchat.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;

public record Writable(Component contents) {
    public static final Writable EMPTY = new Writable(Component.empty());
    public static final Codec<Writable> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ComponentSerialization.CODEC.fieldOf("contents")
                            .forGetter(Writable::contents)
            ).apply(instance, Writable::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, Writable> STREAM_CODEC =
            StreamCodec.composite(
                    ComponentSerialization.STREAM_CODEC, Writable::contents,
                    Writable::new);

    public Writable withText(Component text) {
        return new Writable(text);
    }
}
