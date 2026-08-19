package me.giiena.pigeonchat.client.renderer.entity;

import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.entity.Pigeon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.jspecify.annotations.NonNull;

public class PigeonRenderer extends MobRenderer<Pigeon, PigeonRenderState, PigeonModel> {
    private static final Identifier BASE = PigeonChatCommon.identifier("pigeon")
            .withPrefix("textures/entity/pigeon/");
    private static final Identifier GRAY = BASE.withSuffix("_gray.png");
    private static final Identifier WHITE = BASE.withSuffix("_white.png");
    private static final Identifier RED = BASE.withSuffix("_red.png");
    private static final Identifier RED_WHITE = BASE.withSuffix("_red_white.png");
    private static final Identifier COLUMBINA = BASE.withSuffix("_columbina.png");

    public PigeonRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PigeonModel(ctx.bakeLayer(EntityModelLayers.PIGEON)), 0.3f);
        this.addLayer(new PigeonCarriedItemLayer(this));
    }

    @Override
    @NonNull
    public Identifier getTextureLocation(@NonNull PigeonRenderState state) {
        return state.columbina ? COLUMBINA : getVariantTexture(state.variant);
    }

    @Override
    public void extractRenderState(@NonNull Pigeon entity,
                                   @NonNull PigeonRenderState state,
                                   float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);

        Component customName = entity.getCustomName();
        state.columbina = (customName != null) &&
                customName.getString().equalsIgnoreCase("Columbina");

        state.carrying = entity.carrying();
        state.variant = entity.variant();

        float flap = Mth.lerp(partialTicks, entity.oFlap, entity.flap);
        float flapSpeed = Mth.lerp(partialTicks, entity.oFlapSpeed, entity.flapSpeed);
        state.flapAngle = (Mth.sin(flap) + 1.0f) * flapSpeed;
        state.pose = PigeonModel.getPose(entity);

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

    public static Identifier getVariantTexture(Pigeon.Variant variant) {
        Identifier texture;
        switch (variant) {
            case GRAY -> texture = GRAY;
            case WHITE -> texture = WHITE;
            case RED -> texture = RED;
            case RED_WHITE -> texture = RED_WHITE;
            default -> throw new MatchException(null, null);
        }
        return texture;
    }
}
