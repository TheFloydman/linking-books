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
package thefloydman.linkingbooks.client.gui.book;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.client.gui.widget.ImageWidget;

@OnlyIn(Dist.CLIENT)
public class GuiBookImage extends GuiBookElement<ImageWidget> {

    private final ResourceLocation resourceLocation;
    private final float scale;
    private final int sourceWidth;
    private final int sourceHeight;

    public GuiBookImage(ResourceLocation resourceLocation, float scale, int sourceWidth, int sourceHeight) {
        super("image");
        this.resourceLocation = resourceLocation;
        this.scale = scale;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
    }

    @Override
    public ImageWidget getAsWidget(String id, int x, int y, float z, int width, int height, Screen parentScreen,
                                   float scale, Font font) {
        float localScale = (float) width / (float) this.sourceWidth;
        float scaledHeight = this.sourceHeight * localScale;
        return new ImageWidget(id, x, y, z, (int) (width * this.scale / scale),
                (int) (scaledHeight * this.scale / scale), Component.literal("Image"), parentScreen, scale,
                this.resourceLocation, this.sourceWidth, this.sourceHeight, 0, 0);
    }

}