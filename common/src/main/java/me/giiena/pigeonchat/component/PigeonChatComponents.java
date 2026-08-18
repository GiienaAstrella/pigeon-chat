package me.giiena.pigeonchat.component;

import com.mojang.serialization.Codec;
import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.entity.Pigeon;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class PigeonChatComponents {
    private static final Map<Identifier, DataComponentType<?>> TYPES = new HashMap<>();
    public static final DataComponentType<InkContainer> INK_CONTAINER = register("ink_container",
            DataComponentType.<InkContainer>builder()
                    .persistent(InkContainer.CODEC)
                    .networkSynchronized(InkContainer.STREAM_CODEC));
    public static final DataComponentType<Unit> UTENSIL = register("utensil",
            DataComponentType.<Unit>builder()
                    .persistent(Unit.CODEC)
                    .networkSynchronized(Unit.STREAM_CODEC));
    public static DataComponentType<DyeColor> INK_COLOR = register("ink_color",
            DataComponentType.<DyeColor>builder()
                    .persistent(DyeColor.CODEC)
                    .networkSynchronized(DyeColor.STREAM_CODEC));
    public static DataComponentType<Converted> CONVERTED = register("converted",
            DataComponentType.<Converted>builder()
                    .persistent(Converted.CODEC)
                    .networkSynchronized(Converted.STREAM_CODEC));
    public static DataComponentType<Boolean> UNBREAKABLE = register("unbreakable",
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL));
    public static DataComponentType<Writable> WRITABLE = register("writable",
            DataComponentType.<Writable>builder()
                    .persistent(Writable.CODEC)
                    .networkSynchronized(Writable.STREAM_CODEC));
    public static DataComponentType<Sealed> SEALED = register("sealed",
            DataComponentType.<Sealed>builder()
                    .persistent(Sealed.CODEC)
                    .networkSynchronized(Sealed.STREAM_CODEC));
    public static DataComponentType<CagedMessenger> CAGED_MESSENGER = register("caged_messenger",
            DataComponentType.<CagedMessenger>builder()
                    .persistent(CagedMessenger.CODEC)
                    .networkSynchronized(CagedMessenger.STREAM_CODEC));
    public static DataComponentType<Pigeon.Variant> PIGEON_VARIANT = register("pigeon/variant",
            DataComponentType.<Pigeon.Variant>builder()
                    .persistent(Pigeon.Variant.CODEC)
                    .networkSynchronized(Pigeon.Variant.STREAM_CODEC));

    public static void registerAll(BiConsumer<Identifier, DataComponentType<?>> registry) {
        TYPES.forEach(registry);
    }

    private static <T> DataComponentType<T> register(final String id, DataComponentType.Builder<T> builder) {
        Identifier key = PigeonChatCommon.identifier(id);
        DataComponentType<T> type = builder.build();
        TYPES.put(key, type);
        return type;
    }
}
