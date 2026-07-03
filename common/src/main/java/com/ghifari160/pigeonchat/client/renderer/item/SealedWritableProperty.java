package com.ghifari160.pigeonchat.client.renderer.item;

import com.ghifari160.pigeonchat.component.Sealed;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SealedWritableProperty implements ConditionalItemModelProperty {
    public static final MapCodec<SealedWritableProperty> MAP_CODEC =
            MapCodec.unit(new SealedWritableProperty());

    @Override
    @NonNull
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }

    @Override
    public boolean get(
            @NonNull ItemStack stack,
            @Nullable ClientLevel clientLevel,
            @Nullable LivingEntity livingEntity,
            int i,
            @NonNull ItemDisplayContext ctx) {
        return Sealed.isSealed(stack);
    }
}
