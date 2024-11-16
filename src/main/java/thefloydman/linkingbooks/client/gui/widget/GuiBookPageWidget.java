/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2019-2024 Dan Floyd ("TheFloydman").
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package thefloydman.linkingbooks.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.client.gui.book.GuiBookElement;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GuiBookPageWidget extends NestedWidget {

    public long changeTime = 2000L;

    public GuiBookPageWidget(String id, int x, int y, float z, int width, int height, Component narration,
                             Screen parentScreen, Float scale, Font font, List<Object> elements) {
        super(id, x, y, z, width, height, narration, parentScreen, scale);
        int lineSpacing = 6;
        int currentY = (int) (this.getY() / this.scale);
        for (int i = 0; i < elements.size(); i++) {
            Object element = elements.get(i);
            if (element instanceof GuiBookElement<?> guiBookElement) {
                NestedWidget widget = guiBookElement.getAsWidget(this.id + guiBookElement.getName() + i, this.getX(),
                        currentY, z + 1.0F, this.width, 0, parentScreen, 0.5F, font);
                this.addChild(widget);
                currentY += (int) (widget.getHeight() * 0.5F + lineSpacing);
            }
        }
    }

}
