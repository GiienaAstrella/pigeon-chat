package com.ghifari160.pigeonchat.client.screen.component;

import com.ghifari160.pigeonchat.util.StringUtil;
import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import net.minecraft.client.gui.components.TextCursorUtils;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class InkyEditBox extends AbstractTextAreaWidget {
    private static final int LINE_HEIGHT = 9;
    private static final int PADDING = 4;

    private final Font font;
    private final int inkColor;
    private final int cursorColor;
    private final List<FormattedCharSequence> pastLines;
    private final InkyTextField textField;
    private final long focusedTime = Util.getMillis();

    private boolean editable;

    public InkyEditBox(
            Font font,
            int x, int y, int width, int height,
            Component pastText,
            int inkColor, int cursorColor,
            int characterLimit,
            Component narration) {
        super(x, y, width, height, narration, AbstractScrollArea.defaultSettings(4), false, true);

        this.font = font;
        this.inkColor = inkColor;
        this.cursorColor = cursorColor;

        int innerWidth = width - PADDING * 4;
        this.pastLines = font.split(pastText, innerWidth);
        String lastPastLineString = this.pastLines.isEmpty() ? "" :
                StringUtil.extractPlain(this.pastLines.getLast());
        int firstLineIndent = font.width(lastPastLineString);

        this.textField = new InkyTextField(font, innerWidth);
        this.textField.setFirstLineIndent(firstLineIndent);
        this.textField.setCharacterLimit(characterLimit);
        this.textField.setValueListener(_ -> {});
        this.textField.setCursorListener(this::scrollToCursor);

        this.editable = true;
    }

    public int getCharacterLimit() {
        return this.textField.characterLimit();
    }

    public void setCharacterLimit(int limit) {
        this.textField.setCharacterLimit(limit);
    }

    public void setLineLimit(int limit) {
        Preconditions.checkArgument(limit >= 0, "Line limit cannot be negative");
        int pastLineOffset = Math.max(0, this.pastLines.size() - 1);
        this.textField.setLineLimit(Math.max(limit - pastLineOffset, 0));
    }

    public Component getValue() {
        if (this.textField.value().isBlank()) return Component.empty();
        return Component.literal(this.textField.value().stripTrailing()).withColor(this.inkColor);
    }

    public void setValueListener(Consumer<String> listener) {
        this.textField.setValueListener(listener);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.editBox",
                this.getMessage(), this.getValue()));
    }

    @Override
    public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (doubleClick) {
            this.textField.selectWordAtCursor();
        } else {
            this.textField.setSelecting(event.hasShiftDown());
            this.seekCursorScreen(event.x(), event.y());
        }
    }

    @Override
    public void onDrag(MouseButtonEvent event, double x, double y) {
        this.textField.setSelecting(true);
        this.seekCursorScreen(event.x(), event.y());
        this.textField.setSelecting(event.hasShiftDown());
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        return this.textField.keyPressed(event);
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        if (this.visible && this.isFocused() && event.isAllowedChatCharacter()) {
            this.textField.insertText(event.codepointAsString());
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void extractContents(
            @NonNull GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta) {
        int innerLeft = this.getInnerLeft();
        int innerTop = this.getInnerTop();

        for (int i = 0; i < this.pastLines.size(); i++) {
            graphics.text(this.font, this.pastLines.get(i), innerLeft, innerTop + i * LINE_HEIGHT,
                    0xFFFFFFFF, false);
        }

        if (!this.editable) return;

        String value = this.textField.value();
        int cursor = this.textField.cursor();
        boolean showCursor = this.isFocused() &&
                TextCursorUtils.isCursorVisible(Util.getMillis() - this.focusedTime);
        boolean insertCursor = cursor < value.length();
        int cursorX = 0;
        int cursorY = 0;
        boolean hasDrawnCursor = false;
        int pastLineOffset = Math.max(0, this.pastLines.size() - 1);

        int lineIndex = 0;
        for (InkyTextField.StringView lineView : this.textField.iterateLines()) {
            int lineX = innerLeft + ((lineIndex == 0) ? this.textField.getFirstLineIndent() : 0);
            int lineY = innerTop + (pastLineOffset + lineIndex) * LINE_HEIGHT;
            boolean visible = this.withinContentAreaTopBottom(lineY, lineY + LINE_HEIGHT);
            if (!hasDrawnCursor && showCursor && insertCursor &&
                    cursor >= lineView.begin() && cursor <= lineView.end()) {
                if (visible) {
                    String before = value.substring(lineView.begin(), cursor);
                    String after = value.substring(cursor, lineView.end());
                    int cursorPixelX = lineX + this.font.width(before);
                    graphics.text(this.font, before, lineX, lineY, this.inkColor, false);
                    graphics.text(this.font, after, cursorPixelX, lineY, this.inkColor, false);
                    cursorX = cursorPixelX;
                    cursorY = lineY;
                    TextCursorUtils.extractInsertCursor(graphics, cursorX, cursorY,
                            this.cursorColor, LINE_HEIGHT + 1);
                    hasDrawnCursor = true;
                }
            } else if (visible) {
                String lineText = value.substring(lineView.begin(), lineView.end());
                graphics.text(this.font, lineText, lineX, lineY, this.inkColor, false);
                if (showCursor && !insertCursor) {
                    cursorX = lineX + this.font.width(lineText);
                    cursorY = lineY;
                }
            }
            lineIndex++;
        }

        if (showCursor && !insertCursor &&
                this.withinContentAreaTopBottom(cursorY, cursorY + LINE_HEIGHT)) {
            TextCursorUtils.extractAppendCursor(graphics, this.font, cursorX, cursorY,
                    this.cursorColor, false);
        }

        if (this.textField.hasSelection()) {
            InkyTextField.StringView selection = this.textField.getSelected();

            int selLine = 0;
            for (InkyTextField.StringView lineView : this.textField.iterateLines()) {
                if (selection.begin() > lineView.end()) {
                    selLine++;
                    continue;
                }
                if (lineView.begin() > selection.end()) break;

                int selX = this.getInnerLeft() +
                        ((selLine == 0) ? this.textField.getFirstLineIndent() : 0);
                int selY = this.getInnerTop() + (pastLineOffset + selLine) * LINE_HEIGHT;
                if (this.withinContentAreaTopBottom(selY, selY + LINE_HEIGHT)) {
                    int drawBegin = this.font.width(value.substring(lineView.begin(),
                            Math.max(selection.begin(), lineView.begin())));
                    int drawEnd = selection.end() > lineView.end() ?
                            this.width - this.innerPadding() :
                            this.font.width(value.substring(lineView.begin(), selection.end()));
                    graphics.textHighlight(
                            selX + drawBegin, selY,
                            selX + drawEnd, selY + LINE_HEIGHT,
                            true);
                }
                selLine++;
            }
        }

        if (this.isHovered()) {
            graphics.requestCursor(CursorTypes.IBEAM);
        }
    }

    @Override
    protected void extractDecorations(@NonNull GuiGraphicsExtractor graphics) {
        if (!this.editable) return;
        super.extractDecorations(graphics);
        if (this.textField.hasCharacterLimit()) {
            int characterLimit = this.textField.characterLimit();
            Component countText = Component.translatable("gui.multiLineEditBox.character_limit",
                    StringUtil.countIgnoreWhitespace(this.textField.value()), characterLimit);
            graphics.text(
                    this.font,
                    countText,
                    this.getX() + PADDING,
                    this.getY() + this.height + PADDING,
                    this.cursorColor,
                    false);
        }
    }

    @Override
    public int getInnerHeight() {
        return this.textField.getLineCount() * LINE_HEIGHT;
    }

    protected void seekCursorScreen(double x, double y) {
        int pastLineOffset = Math.max(0, this.pastLines.size() - 1);
        double localY = y - this.getY() - PADDING + this.scrollAmount();
        double editLocalY = localY - pastLineOffset * LINE_HEIGHT;
        editLocalY = Math.max(0, editLocalY);

        int editLine = (int)(editLocalY / LINE_HEIGHT);
        double localX = x - this.getX() - PADDING -
                ((editLine == 0) ? this.textField.getFirstLineIndent() : 0);
        localX = Math.max(0, localX);

        this.textField.seekCursorToPoint(localX, editLocalY);
    }

    protected void scrollToCursor() {
        double scrollAmount = this.scrollAmount();
        int pastLineOffset = Math.max(0, this.pastLines.size() - 1);
        int cursorLine = this.textField.getLineAtCursor();
        int visualLine = pastLineOffset + cursorLine;

        if (visualLine * LINE_HEIGHT < scrollAmount) {
            this.setScrollAmount(visualLine * LINE_HEIGHT);
        } else if ((visualLine + 1) * LINE_HEIGHT > scrollAmount + this.height) {
            this.setScrollAmount((visualLine + 1) * LINE_HEIGHT - this.height + PADDING * 2);
        }
    }
    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("unused")
    public static class Builder {
        private int x;
        private int y;
        private int inkColor;
        private int cursorColor;
        private Component pastText;
        private int characterLimit;
        private int lineLimit;

        private Builder() {
            this.inkColor = DyeColor.BLACK.getTextColor();
            this.cursorColor = this.inkColor;
            this.pastText = Component.empty();
        }

        public Builder setX(int x) {
            this.x = x;
            return this;
        }

        public Builder setY(int y) {
            this.y = y;
            return this;
        }

        public Builder setInkColor(DyeColor color) {
            return this.setInkColor(color.getTextColor());
        }

        public Builder setInkColor(int color) {
            this.inkColor = color;
            this.cursorColor = color;
            return this;
        }

        public Builder setCursorColor(DyeColor color) {
            return this.setCursorColor(color.getTextColor());
        }

        public Builder setCursorColor(int color) {
            this.cursorColor = color;
            return this;
        }

        public Builder setCharacterLimit(int limit) {
            this.characterLimit = limit;
            return this;
        }

        public Builder setLineLimit(int limit) {
            this.lineLimit = limit;
            return this;
        }

        public Builder setPastText(Component pastText) {
            this.pastText = pastText;
            return this;
        }

        public InkyEditBox build(Font font, int width, int height, Component narration) {
            InkyEditBox box = new InkyEditBox(
                    font,
                    this.x,
                    this.y,
                    width,
                    height,
                    this.pastText,
                    this.inkColor,
                    this.cursorColor,
                    this.characterLimit,
                    narration);
            box.setLineLimit(this.lineLimit);
            return box;
        }
    }
}
