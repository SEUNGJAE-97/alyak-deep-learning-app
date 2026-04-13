package com.github.seungjae97.alyak.alyakapiserver.global.util;

public class HangulUtils {
    private static final char[] CHO = {'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ','ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'};
    private static final char[] JUNG = {'ㅏ','ㅐ','ㅑ','ㅒ','ㅓ','ㅔ','ㅕ','ㅖ','ㅗ','ㅘ','ㅙ','ㅚ','ㅛ','ㅜ','ㅝ','ㅞ','ㅟ','ㅠ','ㅡ','ㅢ','ㅣ'};
    private static final char[] JONG = {'\0','ㄱ','ㄲ','ㄳ','ㄴ','ㄵ','ㄶ','ㄷ','ㄹ','ㄺ','ㄻ','ㄼ','ㄽ','ㄾ','ㄿ','ㅀ','ㅁ','ㅂ','ㅄ','ㅅ','ㅆ','ㅇ','ㅈ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'};
    private static final int HANGUL_BASE = 0xAC00;
    private static final int HANGUL_END = 0xD7A3;

    public static String decompose(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        String normalizedText = text.replaceAll("\\s+", "").toLowerCase();
        StringBuilder sb = new StringBuilder();

        for (char c : normalizedText.toCharArray()) {
            if (c >= HANGUL_BASE && c <= HANGUL_END) {
                int base = c - HANGUL_BASE;
                int choIndex = base / (21 * 28);
                int jungIndex = (base % (21 * 28)) / 28;
                int jongIndex = base % 28;

                sb.append(CHO[choIndex]);
                sb.append(JUNG[jungIndex]);

                if (jongIndex != 0) {
                    sb.append(JONG[jongIndex]);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}