/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.text;


public class SimpleAnimatedText {

    private final String text;
    private final String defaultColor;
    private final String colorBefore;
    private final String highlightColor;
    private final String colorAfter;
    private int currentIndex;


    public SimpleAnimatedText(String text, String defaultColor, String colorBefore, String highlightColor, String colorAfter) {
        this.text = text;
        this.highlightColor = highlightColor;
        this.defaultColor = defaultColor;
        this.colorBefore = colorBefore;
        this.colorAfter = colorAfter;

    }

    public String next() {
        currentIndex++;
        if (currentIndex == text.length()) {
            currentIndex = 0;
        }
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String chatColor;
            String colorAfter = "";
            if (currentIndex == i) {
                chatColor = highlightColor;
                colorAfter = "";
            } else if (currentIndex == i - 1) {
                chatColor = colorBefore;
                colorAfter = defaultColor;
            } else if (currentIndex == i + 1) {
                chatColor = this.colorAfter;
            } else {
                if (i == 0)
                    chatColor = defaultColor;
                else chatColor = "";
            }
            char c = text.charAt(i);
            stringBuilder.append(chatColor).append(c).append(colorAfter);
        }
        return stringBuilder.toString();
    }

    public String last() {
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = text.length() - 1;
        }
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String chatColor;
            String colorAfter = "";
            if (currentIndex == i) {
                chatColor = highlightColor;
                colorAfter = "";
            } else if (currentIndex == i - 1) {
                chatColor = colorBefore;
                colorAfter = defaultColor;
            } else if (currentIndex == i + 1) {
                chatColor = this.colorAfter;
            } else {
                if (i == 0)
                    chatColor = defaultColor;
                else chatColor = "";
            }
            char c = text.charAt(i);
            stringBuilder.append(chatColor).append(c).append(colorAfter);
        }
        return stringBuilder.toString();
    }
}