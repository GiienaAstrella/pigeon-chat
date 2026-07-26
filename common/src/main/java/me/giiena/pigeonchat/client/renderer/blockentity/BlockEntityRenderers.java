package me.giiena.pigeonchat.client.renderer.blockentity;

import me.giiena.pigeonchat.block.BlockEntities;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityRenderers {
    public static void register(BlockEntityRendererRegistrar registrar) {
        registrar.register(BlockEntities.BIRD_CAGE, BirdCageEntityRenderer::new);
    }

    @FunctionalInterface
    public interface BlockEntityRendererRegistrar {
        <T extends BlockEntity, S extends BlockEntityRenderState> void register(
                BlockEntityType<? extends T> type,
                BlockEntityRendererProvider<T, S> renderer);
    }
}
