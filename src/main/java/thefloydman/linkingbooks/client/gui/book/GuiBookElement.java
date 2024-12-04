/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
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
package thefloydman.linkingbooks.client.gui.book;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.client.gui.widget.NestedWidget;

@OnlyIn(Dist.CLIENT)
public abstract class GuiBookElement<T extends NestedWidget> {

    private final String name;

    public GuiBookElement(String name) {
        this.name = name;
    }

    public abstract T getAsWidget(String id, int x, int y, float z, int width, int height, Screen parentScreen,
                                  float scale, Font font);

    public String getName() {
        return this.name;
    }

}