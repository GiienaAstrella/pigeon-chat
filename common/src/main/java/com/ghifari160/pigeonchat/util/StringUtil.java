package com.ghifari160.pigeonchat.util;

import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class StringUtil extends net.minecraft.util.StringUtil {
    public static int countIgnoreWhitespace(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) count++;
        }
        return count;
    }

    public static int countIgnoreWhitespace(Component c) {
        return countIgnoreWhitespace(c.getString());
    }

    public static String truncateIgnoreWhitespace(String s, int maxLength) {
        int count = 0;
        int i = 0;
        for (; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) count++;
            if (count > maxLength) break;
        }
        return s.substring(0, i);
    }

    public static String extractPlain(FormattedCharSequence seq) {
        StringBuilder sb = new StringBuilder();
        seq.accept((_, _, codePoint) -> {
            sb.appendCodePoint(codePoint);
            return true;
        });
        return sb.toString();
    }
}
