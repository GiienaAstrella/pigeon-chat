package me.giiena.pigeonchat.client.renderer.entity;

import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.entity.Pigeon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.jspecify.annotations.NonNull;

public class PigeonRenderer extends MobRenderer<Pigeon, PigeonRenderState, PigeonModel> {
    private static final Identifier TEXTURE = PigeonChatCommon.identifier("pigeon")
            .withPrefix("textures/entity/pigeon/")
            .withSuffix(".png");

    public PigeonRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PigeonModel(ctx.bakeLayer(EntityModelLayers.PIGEON)), 0.3f);
        this.addLayer(new PigeonCarriedItemLayer(this));
    }

    @Override
    @NonNull
    public Identifier getTextureLocation(@NonNull PigeonRenderState state) {
        return TEXTURE;
    }

    @Override
    public void extractRenderState(@NonNull Pigeon entity,
                                   @NonNull PigeonRenderState state,
                                   float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.carrying = entity.carrying();

        if (!state.carrying.isEmpty()) {
            this.itemModelResolver.updateForTopItem(
                    state.carryingState,
                    state.carrying,
                    ItemDisplayContext.GROUND,
                    entity.level(),
                    entity,
                    entity.getId());
        } else {
            state.carryingState.clear();
        }
    }

    @Override
    @NonNull
    public PigeonRenderState createRenderState() {
        return new PigeonRenderState();
    }
}
