package com.ghifari160.pigeonchat.client.screen;

import com.ghifari160.pigeonchat.PigeonChatCommon;
import com.ghifari160.pigeonchat.client.screen.component.InkyEditBox;
import com.ghifari160.pigeonchat.component.Converted;
import com.ghifari160.pigeonchat.component.PigeonChatComponents;
import com.ghifari160.pigeonchat.component.Writable;
import com.ghifari160.pigeonchat.network.SaveWritablePayload;
import com.ghifari160.pigeonchat.platform.Services;
import com.ghifari160.pigeonchat.util.ContainerUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;

public class WritableEditScreen extends Screen {
    public static final Component TITLE =
            Component.translatable(PigeonChatCommon.langKey("edit_letter", "gui", "title"));

    private static final int MAX_LINES = 14;

    private final Identifier background;
    private final ItemStack utensil;
    private final InteractionHand hand;
    private final Component pastText;

    private InkyEditBox box;
    private boolean saved = false;

    public WritableEditScreen(Player owner, ItemStack writable, InteractionHand hand) {
        super(TITLE);

        Item baseItem = null;
        if (writable.has(PigeonChatComponents.CONVERTED)) {
            Converted converted = writable.get(PigeonChatComponents.CONVERTED);
            if (converted != null) baseItem = converted.baseItem().getItem();
        }
        if (baseItem == null) baseItem = Items.PAPER;

        Identifier baseItemID = BuiltInRegistries.ITEM.getKey(baseItem);
        this.background = Identifier.fromNamespaceAndPath(
                baseItemID.getNamespace(),
                baseItemID.getPath())
                .withPrefix("textures/gui/letter/")
                .withSuffix(".png");

        this.utensil = (hand == InteractionHand.MAIN_HAND) ?
                owner.getItemInHand(InteractionHand.OFF_HAND) :
                owner.getItemInHand(InteractionHand.MAIN_HAND);
        this.hand = hand;
        Writable content = writable.getOrDefault(PigeonChatComponents.WRITABLE, Writable.EMPTY);
        this.pastText = content.contents();
    }

    @Override
    protected void init() {
        DyeColor inkColor = ContainerUtils.inkColor(this.utensil);
        if (inkColor == null) inkColor = DyeColor.BLACK;
        int remainingFill = ContainerUtils.remainingFill(this.utensil);
        boolean editable = ContainerUtils.isUtensil(this.utensil) && remainingFill > 0;

        this.box = InkyEditBox.builder()
                .setInkColor(inkColor)
                .setX((this.width - 114) / 2 - 8)
                .setY(19)
                .setPastText(this.pastText)
                .setLineLimit(MAX_LINES)
                .build(this.font, 122, 134, CommonComponents.EMPTY);
        this.box.setCharacterLimit(remainingFill);
        this.box.setEditable(editable);

        Button done = Button
                .builder(CommonComponents.GUI_DONE, _ -> this.onClose())
                .pos(
                        (this.width - Button.DEFAULT_WIDTH) / 2 - 3,
                        this.box.getHeight() + 4 + 9 + 4 + 28)
                .build();

        this.addRenderableWidget(this.box);
        this.addRenderableWidget(done);

        if (editable) {
            this.setInitialFocus(this.box);
        }
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
                this.background,
                this.backgroundLeft(),
                this.backgroundTop(),
                0F,
                0F,
                192,
                192,
                256,
                256);
    }

    private int backgroundLeft() {
        return (this.width - 192) / 2;
    }

    private int backgroundTop() {
        return 2;
    }

    @Override
    public void onClose() {
        this.save();
        super.onClose();
    }

    protected void save() {
        if (this.saved) return;
        Component newText = this.box.getValue();
        if (newText.equals(Component.empty())) return;

        Services.PLATFORM.sendPacketToServer(new SaveWritablePayload(newText, this.hand));
        this.saved = true;
    }
}
