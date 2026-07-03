package me.giiena.pigeonchat.client.color.item;

import me.giiena.pigeonchat.util.ContainerUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record InkContainer(int defaultColor) implements ItemTintSource {
    public static final MapCodec<InkContainer> MAP_CODEC =
            ExtraCodecs.RGB_COLOR_CODEC
                    .fieldOf("default")
                    .xmap(InkContainer::new, InkContainer::defaultColor);

    public InkContainer() {
        this(DyeColor.BLACK.getTextureDiffuseColor());
    }

    public InkContainer(int defaultColor) {
        this.defaultColor = ARGB.opaque(defaultColor);
    }

    @Override
    public int calculate(
            @NonNull ItemStack itemStack,
            @Nullable ClientLevel clientLevel,
            @Nullable LivingEntity livingEntity) {
        DyeColor color = ContainerUtils.inkColor(itemStack);
        return (color != null) ? ARGB.opaque(color.getTextureDiffuseColor()) : this.defaultColor();
    }

    @Override
    @NonNull
    public MapCodec<InkContainer> type() {
        return MAP_CODEC;
    }
}
