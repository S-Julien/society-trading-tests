package io.github.chakyl.societytrading.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class ScreenUtils {
    public static void drawWordWrapShadow(GuiGraphics pGuiGraphics, Font pFont, FormattedText pText, int pX, int pY, int pLineWidth, int pColor) {
        for(FormattedCharSequence formattedcharsequence : pFont.split(pText, pLineWidth)) {
            pGuiGraphics.drawString(pFont, formattedcharsequence, pX, pY, pColor, true);
            pY += 9;
        }

    }
}
