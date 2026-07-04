package me.giiena.pigeonchat.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jspecify.annotations.NonNull;

public class PigeonCarriedItemLayer extends RenderLayer<PigeonRenderState, PigeonModel> {
    public PigeonCarriedItemLayer(RenderLayerParent<PigeonRenderState, PigeonModel> parent) {
        super(parent);
    }

    @Override
    public void submit(@NonNull PoseStack poseStack,
                       @NonNull SubmitNodeCollector submitNodeCollector,
                       int lightCoords,
                       PigeonRenderState state,
                       float yRot,
                       float xRot) {
        if (state.carrying.isEmpty()) return;

        poseStack.pushPose();
        this.getParentModel().Beak.translateAndRotate(poseStack);
        poseStack.translate(0.0f, -0.55f, -0.3f);
        poseStack.mulPose(Axis.XP.rotation(-1.0f));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        state.carryingState.submit(poseStack,
                submitNodeCollector,
                lightCoords,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor);

        poseStack.popPose();
    }
}
