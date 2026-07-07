package me.giiena.pigeonchat.client.screen;

import com.mojang.authlib.GameProfile;
import me.giiena.pigeonchat.inventory.AbstractMessengerMenu;
import me.giiena.pigeonchat.network.AssignMessengerPayload;
import me.giiena.pigeonchat.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class TargetSelectionScreen extends AbstractContainerScreen<AbstractMessengerMenu> {
    private static final int FACE_SIZE = 16;
    private static final int ROW_WIDTH = 130;
    private static final int ROW_HEIGHT = 24;
    private static final int NAME_PADDING = 6;

    private final List<GameProfile> candidates;

    private TargetList list;
    private Button done;

    public TargetSelectionScreen(AbstractMessengerMenu menu,
                                 Inventory inventory,
                                 Component title) {
        super(menu, inventory, title);
        this.candidates = collectCandidates();
    }

    private List<GameProfile> collectCandidates() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) return List.of();

        return connection.getOnlinePlayers()
                .stream()
                .map(PlayerInfo::getProfile)
                .filter(profile -> this.menu.isValidTarget(profile.id()))
                .toList();
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int xm, int ym) {}

    @Override
    protected void init() {
        HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 32);
        StringWidget title = layout.addToHeader(new StringWidget(this.title, this.font));
        this.addRenderableWidget(title);

        LinearLayout footerLayout = new LinearLayout(
                layout.getX(),
                layout.getY(),
                LinearLayout.Orientation.HORIZONTAL)
                .spacing(NAME_PADDING);
        layout.addToFooter(footerLayout);

        Button reset = Button.builder(CommonComponents.GUI_CANCEL, _ -> this.onClose()).build();
        footerLayout.addChild(reset);
        this.addRenderableWidget(reset);

        this.done = Button.builder(CommonComponents.GUI_DONE, _ -> {
            TargetList.Entry entry = this.list.getSelected();
            if (entry != null) {
                Services.PLATFORM.sendPacketToServer(new AssignMessengerPayload(entry.profile.id()));
            } else {
                this.onClose();
            }
        }).build();
        this.done.active = false;
        footerLayout.addChild(this.done);
        this.addRenderableWidget(this.done);

        this.list = new TargetList(
                this.minecraft,
                this.width,
                layout.getContentHeight(),
                layout.getY() + layout.getHeaderHeight(),
                ROW_HEIGHT);
        this.addRenderableWidget(this.list);
        layout.addToContents(this.list);

        List<GameProfile> sortedCandidates = this.candidates.stream()
                .sorted(Comparator.comparing(GameProfile::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
        for (GameProfile profile : sortedCandidates) {
            this.list.addEntry(this.list.new Entry(profile));
        }

        layout.arrangeElements();
        footerLayout.arrangeElements();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private class TargetList extends ObjectSelectionList<TargetList.Entry> {
        public TargetList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
        }

        @Override
        public void setSelected(TargetSelectionScreen.TargetList.@Nullable Entry selected) {
            super.setSelected(selected);
            TargetSelectionScreen.this.done.active = selected != null;
        }

        @Override
        protected int addEntry(@NonNull Entry entry) {
            return super.addEntry(entry);
        }

        @Override
        public int getRowWidth() {
            return ROW_WIDTH;
        }

        private class Entry extends ObjectSelectionList.Entry<Entry> {
            private final Font font;
            private final GameProfile profile;

            public Entry(GameProfile profile) {
                this.font = TargetSelectionScreen.this.font;
                this.profile = profile;
            }

            @Override
            @NonNull
            public Component getNarration() {
                return Component.literal(this.profile.name());
            }

            @Override
            public void extractContent(
                    @NonNull GuiGraphicsExtractor graphics,
                    int mouseX,
                    int mouseY,
                    boolean hovered,
                    float delta) {
                PlayerFaceExtractor.extractRenderState(
                        graphics,
                        ResolvableProfile.createResolved(this.profile),
                        this.getX() + NAME_PADDING,
                        this.getY() + (ROW_HEIGHT - FACE_SIZE) / 2,
                        FACE_SIZE);

                graphics.text(
                        this.font,
                        truncateIfNecessary(this.profile.name()),
                        this.getX() + FACE_SIZE + NAME_PADDING * 2,
                        this.getY() + (ROW_HEIGHT - this.font.lineHeight) / 2,
                        0xFFFFFFFF,
                        false);
            }

            private String truncateIfNecessary(String text) {
                final int maxWidth = ROW_WIDTH - FACE_SIZE - NAME_PADDING * 3;
                if (this.font.width(text) <= maxWidth) {
                    return text;
                }
                text = this.font.plainSubstrByWidth(text, maxWidth - this.font.width("..."));
                return text + "...";
            }
        }
    }
}
