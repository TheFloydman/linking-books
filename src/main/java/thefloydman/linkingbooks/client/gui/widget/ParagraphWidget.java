/*
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 */
package thefloydman.linkingbooks.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ParagraphWidget extends NestedWidget {

    public List<Component> lines;
    public int lineSpacing;
    public Font font;

    public ParagraphWidget(String id, int x, int y, float z, int width, int height, Component narration,
                           Screen parentScreen, float scale, List<Component> lines, int lineSpacing, Font font) {
        super(id, x, y, z, width, height, narration, parentScreen, scale);
        this.lines = lines;
        this.lineSpacing = lineSpacing;
        this.font = font;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(this.scale, this.scale, 1.0F);
            float currentY = this.getY();
            for (int k = 0; k < this.lines.size(); k++) {
                currentY = this.getY() + (lineSpacing * k);
                guiGraphics.drawString(font, this.lines.get(k).getVisualOrderText(), this.getX() / this.scale, currentY / this.scale, 0, false);
            }
            guiGraphics.pose().popPose();
        }
    }

}
