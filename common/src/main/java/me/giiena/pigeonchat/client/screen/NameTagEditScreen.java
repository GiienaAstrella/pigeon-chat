package me.giiena.pigeonchat.client.screen;

import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.client.screen.component.InkyEditBox;
import me.giiena.pigeonchat.network.SaveWritablePayload;
import me.giiena.pigeonchat.platform.Services;
import me.giiena.pigeonchat.util.ContainerUtils;
import me.giiena.pigeonchat.util.InteractionUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class NameTagEditScreen extends Screen {
    public static final Component TITLE =
            Component.translatable(PigeonChatCommon.langKey("edit_name_tag", "gui", "title"));

    private static final int BOX_WIDTH = 122;
    private static final int BOX_WIDTH_DELTA = 0;
    private static final int BOX_HEIGHT = 44;
    private static final int BOX_HEIGHT_DELTA = 4;
    private static final int MAX_CHARS = 50;
    private static final Identifier BACKGROUND = PigeonChatCommon.identifier("name_tag")
            .withPrefix("textures/gui/")
            .withSuffix(".png");
    private static final int BACKGROUND_WIDTH = 240;
    private static final int BACKGROUND_WIDTH_DELTA = 20;
    private static final int BACKGROUND_HEIGHT = 100;
    private static final int BACKGROUND_HEIGHT_DELTA = 0;

    private final ItemStack utensil;
    private final InteractionHand hand;
    private final Component pastText;

    private InkyEditBox box;
    private boolean saved = false;

    public NameTagEditScreen(Player owner, ItemStack tag, InteractionHand hand) {
        super(TITLE);

        this.utensil = owner.getItemInHand(InteractionUtils.otherHand(hand));
        this.hand = hand;

        this.pastText = tag.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty());
    }

    @Override
    protected void init() {
        DyeColor inkColor = ContainerUtils.inkColor(this.utensil);
        if (inkColor == null) inkColor = DyeColor.BLACK;
        int remainingFill = ContainerUtils.remainingFill(this.utensil);
        boolean editable = ContainerUtils.isUtensil(this.utensil) && remainingFill > 0;

        this.box = InkyEditBox.builder()
                .setInkColor(inkColor)
                .setX((this.width - BOX_WIDTH) / 2 - BOX_WIDTH_DELTA)
                .setY((this.height - BOX_HEIGHT) / 2 - BOX_HEIGHT_DELTA)
                .setPastText(this.pastText)
                .setLineLimit(4)
                .setCharacterLimit(remainingFill)
                .build(this.font, BOX_WIDTH, BOX_HEIGHT, Component.empty());
        this.box.characterLimit(MAX_CHARS);
        this.box.setEditable(editable);

        Button done = Button
                .builder(CommonComponents.GUI_DONE, _ -> this.onClose())
                .pos(
                        (this.width - Button.DEFAULT_WIDTH) / 2 + 3,
                        this.box.getY() + this.box.getHeight() + 40)
                .build();

        this.addRenderableWidget(this.box);
        this.addRenderableWidget(done);

        if (editable) this.setInitialFocus(this.box);
    }

    @Override
    public void extractBackground(
            @NonNull GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND,
                (this.width - BACKGROUND_WIDTH) / 2 - BACKGROUND_WIDTH_DELTA,
                (this.height - BACKGROUND_HEIGHT) / 2 - BACKGROUND_HEIGHT_DELTA,
                0F,
                0F,
                BACKGROUND_WIDTH,
                BACKGROUND_HEIGHT,
                256,
                256);
    }

    @Override
    public void onClose() {
        this.save();
        super.onClose();
    }

    protected void save() {
        if (this.saved) return;
        Component value = this.box.getValue();
        if (value.equals(Component.empty())) return;

        MutableComponent newText = Component.empty();
        value.visit((style, text) -> {
            newText.append(Component.literal(text.replace('\n', ' ')).setStyle(style));
            return Optional.empty();
        }, Style.EMPTY);

        Services.PLATFORM.sendPacketToServer(new SaveWritablePayload(newText, this.hand));
        this.saved = true;
    }
}
