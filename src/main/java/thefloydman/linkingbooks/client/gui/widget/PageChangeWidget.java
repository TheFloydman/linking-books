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
package thefloydman.linkingbooks.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class PageChangeWidget extends NestedWidget {

    private final Type type;

    public PageChangeWidget(String id, int x, int y, float z, Component narration, Screen parentScreen, float scale,
                            Type type) {
        super(id, x, y, z, 23, 13, narration, parentScreen, scale);
        this.type = type;
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
            guiGraphics.pose().pushPose();
            if (this.isInside(mouseX, mouseY)) {
                guiGraphics.blitSprite(this.type.spriteHover, this.getX(), this.getY(), 1, 23, 13);
            } else {
                RenderSystem.setShaderColor(0.9F, 0.9F, 0.9F, 1.0F);
                guiGraphics.blitSprite(this.type.spriteNormal, this.getX(), this.getY(), 1, 23, 13);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (!this.isInside(x, y))
            return false;
        for (GuiEventListener listener : this.listeners) {
            if (listener instanceof BookWidget book) {
                switch (this.type) {
                    case BACKWARD:
                        book.previousPage();
                        break;
                    default:
                        book.nextPage();
                        break;
                }
            }
        }
        return true;
    }

    public enum Type {
        BACKWARD(
                ResourceLocation.withDefaultNamespace("widget/page_backward"),
                ResourceLocation.withDefaultNamespace("widget/page_backward_highlighted")),
        FORWARD(
                ResourceLocation.withDefaultNamespace("widget/page_forward"),
                ResourceLocation.withDefaultNamespace("widget/page_forward_highlighted"));

        public final ResourceLocation spriteNormal;
        public final ResourceLocation spriteHover;

        Type(ResourceLocation spriteNormal, ResourceLocation spriteHover) {
            this.spriteNormal = spriteNormal;
            this.spriteHover = spriteHover;
        }
    }

}
