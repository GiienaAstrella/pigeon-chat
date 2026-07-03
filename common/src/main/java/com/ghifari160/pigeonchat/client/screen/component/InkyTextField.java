package com.ghifari160.pigeonchat.client.screen.component;

import com.ghifari160.pigeonchat.Constants;
import com.ghifari160.pigeonchat.util.StringUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class InkyTextField {
    private final Font font;
    private final int width;
    private final List<StringView> displayLines = new ArrayList<>();

    private int firstLineIndent;
    private String value;
    private int cursor;
    private int selectCursor;
    private boolean selecting;
    private int inkLimit = Integer.MAX_VALUE;
    private int characterLimit = Integer.MAX_VALUE;
    private int lineLimit = Integer.MAX_VALUE;
    private Consumer<String> valueListener = _ -> {};
    private Runnable cursorListener = () -> {};

    public InkyTextField(Font font, int width) {
        this.font = font;
        this.width = width;
        this.firstLineIndent = 0;
        this.setValue("");
    }

    public int getFirstLineIndent() {
        return this.firstLineIndent;
    }

    public void setFirstLineIndent(int indent) {
        Preconditions.checkArgument(indent >= 0, "indent cannot be negative");
        this.firstLineIndent = indent;
    }

    public boolean hasInkLimit() {
        return this.inkLimit != Integer.MAX_VALUE;
    }

    public int inkLimit() {
        return this.inkLimit;
    }

    public void inkLimit(int limit) {
        Preconditions.checkArgument(limit >= 0, "Ink limit cannot be negative");
        this.inkLimit = limit;
    }

    public boolean hasCharacterLimit() {
        return this.characterLimit != Integer.MAX_VALUE;
    }

    public int characterLimit() {
        return this.characterLimit;
    }

    public void characterLimit(int limit) {
        Preconditions.checkArgument(limit >= 0, "Character limit cannot be negative");
        this.characterLimit = limit;
    }

    public boolean hasLineLimit() {
        return this.lineLimit != Integer.MAX_VALUE;
    }

    public void setLineLimit(int lineLimit) {
        Preconditions.checkArgument(lineLimit >= 0, "Line limit cannot be negative");
        this.lineLimit = lineLimit;
    }

    public void setValueListener(Consumer<String> listener) {
        this.valueListener = listener;
    }

    public void setCursorListener(Runnable listener) {
        this.cursorListener = listener;
    }

    public void setValue(String value) {
        this.setValue(value, false);
    }

    public void setValue(String value, boolean allowOverflowLineLimit) {
        String newValue = this.truncateFullText(value);
        if (allowOverflowLineLimit || !this.overflowsLineLimit(newValue)) {
            this.value = newValue;
            this.cursor = this.value.length();
            this.selectCursor = this.cursor;
            this.onValueChange();
        }
    }

    public String value() {
        return this.value;
    }

    public void insertText(String input) {
        if (!input.isEmpty() || this.hasSelection()) {
            String text = this.truncateInsertionText(StringUtil.filterText(input, true));
            StringView selected = this.getSelected();
            String newValue = (new StringBuilder(this.value)).replace(selected.begin, selected.end, text)
                    .toString();
            if (!this.overflowsLineLimit(newValue)) {
                this.value = newValue;
                this.cursor = selected.begin + text.length();
                this.selectCursor = cursor;
                this.onValueChange();
            }
        }
    }

    public void deleteText(int dir) {
        if (!this.hasSelection()) {
            this.selectCursor = Mth.clamp(this.cursor + dir, 0, this.value.length());
        }
        this.insertText("");
    }

    public int cursor() {
        return this.cursor;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }

    public StringView getSelected() {
        return new StringView(Math.min(this.selectCursor, this.cursor),
                Math.max(this.selectCursor, this.cursor));
    }

    public int getLineCount() {
        return this.displayLines.size();
    }

    public int getLineAtCursor() {
        for (int i = 0; i < this.displayLines.size(); i++) {
            StringView view = this.displayLines.get(i);
            if (this.cursor >= view.begin && this.cursor <= view.end) {
                return i;
            }
        }
        return -1;
    }

    public StringView getLineView(int lineIndex) {
        return this.displayLines.get(Mth.clamp(lineIndex, 0, this.displayLines.size() -1));
    }

    public void seekCursor(Whence whence, int cursor) {
        switch (whence) {
            case ABSOLUTE -> this.cursor = cursor;
            case RELATIVE -> this.cursor += cursor;
            case END -> this.cursor = this.value.length() + cursor;
        }

        this.cursor = Mth.clamp(this.cursor, 0, this.value.length());
        this.cursorListener.run();
        if (!this.selecting) {
            this.selectCursor = this.cursor;
        }
    }

    public void seekCursorLine(int lineOffset) {
        if (this.getLineAtCursor() == 0 && lineOffset > 0) {
            int oldCursorLeft = this.font.width(
                    this.value.substring(this.getLineView(0).begin, this.cursor)) +
                    this.firstLineIndent + 2;
            StringView lineView = this.getLineView(1);
            int newCursor = this.font.plainSubstrByWidth(
                    this.value.substring(lineView.begin, lineView.end), oldCursorLeft).length();
            this.seekCursor(Whence.ABSOLUTE, lineView.begin + newCursor);
        } else if (getLineAtCursor() == 1 && lineOffset < 0) {
            StringView currentLine = this.getLineView(1);
            int oldCursorLeft = this.font.width(
                    this.value.substring(currentLine.begin, this.cursor)) - firstLineIndent + 2;
            oldCursorLeft = Math.max(0, oldCursorLeft);
            StringView lineView = this.getLineView(0);
            int newCursor = this.font.plainSubstrByWidth(
                    this.value.substring(lineView.begin, lineView.end), oldCursorLeft).length();
            this.seekCursor(Whence.ABSOLUTE, lineView.begin + newCursor);
        } else if (lineOffset != 0) {
            int oldCursorLeft = this.font.width(
                    this.value.substring(this.getCursorLineView().begin, this.cursor)) + 2;
            StringView lineView = this.getCursorLineView(lineOffset);
            int newCursor = this.font.plainSubstrByWidth(
                    this.value.substring(lineView.begin, lineView.end), oldCursorLeft).length();
            this.seekCursor(Whence.ABSOLUTE, lineView.begin + newCursor);
        }
    }

    public void seekCursorToPoint(double x, double y) {
        int left = Mth.floor(x);
        int top = Mth.floor(y / (double) 9.0F);
        StringView lineView =
                this.displayLines.get(Mth.clamp(top, 0, this.displayLines.size() - 1));
        int clickedColumn = this.font.plainSubstrByWidth(
                this.value.substring(lineView.begin, lineView.end),
                left).length();
        this.seekCursor(Whence.ABSOLUTE, lineView.begin + clickedColumn);
    }

    public void selectWordAtCursor() {
        StringView wordView = this.getPreviousWord();
        this.seekCursor(Whence.ABSOLUTE, wordView.begin);
        this.setSelecting(true);
        this.seekCursor(Whence.ABSOLUTE, wordView.end);
    }

    public boolean keyPressed(KeyEvent event) {
        this.selecting = event.hasShiftDown();
        if (event.isSelectAll()) {
            this.cursor = this.value.length();
            this.selectCursor = 0;
            return true;
        } else if (event.isCopy()) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            return true;
        } else if (event.isPaste()) {
            this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            return true;
        } else if (event.isCut()) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            this.insertText("");
            return true;
        } else {
            return switch (event.key()) {
                case 257, 335 -> {
                    this.insertText("\n");
                    yield true;
                }
                case 259 -> {
                    if (event.hasControlDownWithQuirk()) {
                        StringView wordView = this.getPreviousWord();
                        this.deleteText(wordView.begin - this.cursor);
                    } else {
                        this.deleteText(-1);
                    }
                    yield true;
                }
                case 261 -> {
                    if (event.hasControlDownWithQuirk()) {
                        StringView wordView = this.getNextWord();
                        this.deleteText(wordView.begin - this.cursor);
                    } else {
                        this.deleteText(1);
                    }
                    yield true;
                }
                case 262 -> {
                    if (event.hasControlDownWithQuirk()) {
                        StringView wordView = this.getNextWord();
                        this.seekCursor(Whence.ABSOLUTE, wordView.begin);
                    } else {
                        this.seekCursor(Whence.RELATIVE, 1);
                    }
                    yield true;
                }
                case 263 -> {
                    if (event.hasControlDownWithQuirk()) {
                        StringView wordView = this.getPreviousWord();
                        this.seekCursor(Whence.ABSOLUTE, wordView.begin);
                    } else {
                        this.seekCursor(Whence.RELATIVE, -1);
                    }
                    yield true;
                }
                case 264 -> {
                    if (!event.hasControlDownWithQuirk()) {
                        this.seekCursorLine(1);
                    }
                    yield true;
                }
                case 265 -> {
                    if (!event.hasControlDownWithQuirk()) {
                        this.seekCursorLine(-1);
                    }
                    yield true;
                }
                case 266 -> {
                    this.seekCursor(Whence.ABSOLUTE, 0);
                    yield true;
                }
                case 267 -> {
                    this.seekCursor(Whence.END, 0);
                    yield true;
                }
                case 268 -> {
                    if (event.hasControlDownWithQuirk()) {
                        this.seekCursor(Whence.ABSOLUTE, 0);
                    } else {
                        this.seekCursor(Whence.ABSOLUTE, this.getCursorLineView().begin);
                    }
                    yield true;
                }
                case 269 -> {
                    if (event.hasControlDownWithQuirk()) {
                        this.seekCursor(Whence.END, 0);
                    } else {
                        this.seekCursor(Whence.ABSOLUTE, this.getCursorLineView().end);
                    }
                    yield true;
                }
                default -> false;
            };
        }
    }

    public Iterable<StringView> iterateLines() {
        return this.displayLines;
    }

    public boolean hasSelection() {
        return this.selectCursor != this.cursor;
    }

    @VisibleForTesting
    public String getSelectedText() {
        StringView selected = this.getSelected();
        return this.value.substring(selected.begin, selected.end);
    }

    private StringView getCursorLineView() {
        return this.getCursorLineView(0);
    }

    private StringView getCursorLineView(int lineOffset) {
        int lineIndex = this.getLineAtCursor();
        if (lineIndex < 0) {
            Constants.LOG.error("Cursor is not within text (cursor = {}, length = {})",
                    this.cursor, this.value.length());
            return this.displayLines.getLast();
        } else {
            return this.displayLines.get(
                    Mth.clamp(lineIndex + lineOffset, 0, this.displayLines.size() - 1));
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @VisibleForTesting
    public StringView getPreviousWord() {
        if (this.value.isEmpty()) {
            return StringView.EMPTY;
        } else {
            int startPos;
            for (startPos = Mth.clamp(this.cursor, 0, this.value.length() -1);
                 startPos > 0 && Character.isWhitespace(this.value.charAt(startPos - 1));
                 startPos--) {}

            while (startPos > 0 && !Character.isWhitespace(this.value.charAt(startPos - 1))) {
                startPos--;
            }

            return new StringView(startPos, this.getWordEndPosition(startPos));
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @VisibleForTesting
    public StringView getNextWord() {
        if (this.value.isEmpty()) {
            return StringView.EMPTY;
        } else {
            int startPos;
            for (startPos = Mth.clamp(this.cursor, 0, this.value.length() - 1);
                 startPos < this.value.length() &&
                         !Character.isWhitespace(this.value.charAt(startPos));
                 startPos++) {}

            while (startPos < this.value.length() &&
                    Character.isWhitespace(this.value.charAt(startPos))) {
                startPos++;
            }

            return new StringView(startPos, this.getWordEndPosition(startPos));
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private int getWordEndPosition(int from) {
        int end;
        for (end = from;
             end < this.value.length() && !Character.isWhitespace(this.value.charAt(end));
             end++) {}
        return end;
    }

    private void onValueChange() {
        this.reflowDisplayLines();
        this.valueListener.accept(this.value);
        this.cursorListener.run();
    }

    protected void reflowDisplayLines() {
        this.displayLines.clear();
        if (value.isEmpty()) {
            this.displayLines.add(new StringView(0, 0));
            return;
        }

        int firstLineWidth = this.width - this.firstLineIndent;
        String firstSegment = this.font.plainSubstrByWidth(this.value, firstLineWidth);
        int firstEnd = firstSegment.length();

        int newLinePos = this.value.indexOf('\n');
        if (newLinePos != -1 && newLinePos < firstEnd) {
            firstEnd = newLinePos;
        }

        if (firstEnd < this.value.length() && this.value.charAt(firstEnd) != '\n' &&
                this.value.charAt(firstEnd) != ' ') {
            int wordBreak = this.value.lastIndexOf(' ', firstEnd);
            if (wordBreak > 0) firstEnd = wordBreak + 1;
        } else if (firstEnd < this.value.length() && this.value.charAt(firstEnd) == ' ') {
            firstEnd++;
        }

        this.displayLines.add(new StringView(0, firstEnd));

        if (firstEnd < this.value.length() && this.value.charAt(firstEnd) == '\n') {
            firstEnd++;
        }

        // Lambda vars have to be final.
        final int fe = firstEnd;
        if (fe < this.value.length()) {
            String remainder = this.value.substring(fe);
            this.font.getSplitter().splitLines(
                    remainder,
                    this.width,
                    Style.EMPTY,
                    false,
                    (_, start, end) ->
                            this.displayLines.add(new StringView(fe + start, fe + end)));
        }

        if (this.value.charAt(this.value.length() - 1) == '\n') {
            this.displayLines.add(new StringView(this.value.length(), this.value.length()));
        }
    }

    private String truncateFullText(String input) {
        return truncateInsertionText(input);
    }

    private String truncateInsertionText(String input) {
        String truncatedInput = input;
        if (this.hasInkLimit()) {
            int remainingCharacters = this.inkLimit -
                    StringUtil.countIgnoreWhitespace(this.value);
            truncatedInput =
                    StringUtil.truncateIgnoreWhitespace(input, remainingCharacters);
        }
        if (this.hasCharacterLimit()) {
            int remainingCharacters = this.characterLimit - this.value.length();
            truncatedInput = StringUtil.
                    truncateStringIfNecessary(truncatedInput, remainingCharacters, false);
        }
        return truncatedInput;
    }

    private boolean overflowsLineLimit(String newValue) {
        return this.hasLineLimit() &&
                this.font.getSplitter().splitLines(newValue, this.width, Style.EMPTY).size() +
                        (StringUtil.endsWithNewLine(newValue) ? 1 : 0) > this.lineLimit;
    }

    public record StringView(int begin, int end) {
        public static final StringView EMPTY = new StringView(0, 0);
    }
}
