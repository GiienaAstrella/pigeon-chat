package me.giiena.pigeonchat.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import me.giiena.pigeonchat.block.BirdCage;
import me.giiena.pigeonchat.block.BirdCageEntity;
import me.giiena.pigeonchat.entity.MessengerAnimal;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class BirdCageEntityRenderer
        implements BlockEntityRenderer<BirdCageEntity, BirdCageEntityRenderState> {
    private final EntityRenderDispatcher entityRenderer;

    public BirdCageEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.entityRenderer = ctx.entityRenderer();
    }

    @Override
    @NonNull
    public BirdCageEntityRenderState createRenderState() {
        return new BirdCageEntityRenderState();
    }

    @Override
    public void extractRenderState(@NonNull BirdCageEntity blockEntity,
                                   @NonNull BirdCageEntityRenderState state,
                                   float partialTicks,
                                   @NonNull Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity,
                state,
                partialTicks,
                cameraPos,
                breakProgress);
        MessengerAnimal messenger = blockEntity.messenger();
        if (messenger != null) {
            float yRot = directionToYRot(blockEntity.getBlockState().getValue(BirdCage.FACING));
            messenger.setYRot(yRot);
            messenger.setYHeadRot(yRot);
            messenger.setYBodyRot(yRot);
            messenger.yRotO = yRot;
            messenger.yHeadRotO = yRot;
            messenger.yBodyRotO = yRot;
            state.messengerState = this.entityRenderer.extractEntity(messenger, partialTicks);
        } else {
            state.messengerState = null;
        }
    }

    private static float directionToYRot(Direction direction) {
        return switch (direction) {
            case WEST -> 90.0f;
            case NORTH -> 180.0f;
            case EAST -> 270.0f;
            default -> 0.0f;
        };
    }

    @Override
    public void submit(@NonNull BirdCageEntityRenderState state,
                       @NonNull PoseStack stack,
                       @NonNull SubmitNodeCollector queue,
                       @NonNull CameraRenderState cameraState) {
        EntityRenderState messengerState = state.messengerState;
        if (messengerState == null) return;

        stack.pushPose();
        stack.translate(0.5d, 0.1d, 0.5d);
        stack.scale(0.8f, 0.8f, 0.8f);

        this.entityRenderer.submit(messengerState, cameraState, 0.0d, 0.0d, 0.0d, stack, queue);

        stack.popPose();
    }
}
