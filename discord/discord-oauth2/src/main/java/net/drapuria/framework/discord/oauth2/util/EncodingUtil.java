package net.drapuria.framework.discord.oauth2.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.stream.Collectors;

/**
 * Cropped from jda
 */
public class EncodingUtil {
    public EncodingUtil() {
    }

    public static String encodeUTF8(String chars) {
        try {
            return URLEncoder.encode(chars, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            throw new AssertionError(var2);
        }
    }

    public static String encodeCodepointsUTF8(String input) {
        if (!input.startsWith("U+")) {
            throw new IllegalArgumentException("Invalid format");
        } else {
            String[] codePoints = input.substring(2).split("\\s*U\\+\\s*");
            StringBuilder encoded = new StringBuilder();
            int var4 = codePoints.length;

            for (String part : codePoints) {
                String utf16 = decodeCodepoint(part, 16);
                String urlEncoded = encodeUTF8(utf16);
                encoded.append(urlEncoded);
            }

            return encoded.toString();
        }
    }

    public static String decodeCodepoint(String codepoint) {
        if (!codepoint.startsWith("U+")) {
            throw new IllegalArgumentException("Invalid format");
        } else {
            return decodeCodepoint(codepoint.substring(2), 16);
        }
    }

    public static String encodeCodepoints(String unicode) {
        return unicode.codePoints().mapToObj((code) -> "U+" + Integer.toHexString(code)).collect(Collectors.joining());
    }

    private static String decodeCodepoint(String hex, int radix) {
        int codePoint = Integer.parseUnsignedInt(hex, radix);
        return String.valueOf(Character.toChars(codePoint));
    }

    public static String encodeReaction(String unicode) {
        return !unicode.startsWith("U+") && !unicode.startsWith("u+") ? encodeUTF8(unicode) : encodeCodepointsUTF8(unicode);
    }
}
