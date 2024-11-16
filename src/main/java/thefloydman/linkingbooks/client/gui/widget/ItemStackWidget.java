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

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.List;

public class ItemStackWidget extends NestedWidget {

    public long creationTime;
    public long changeTime = 2000L; // 2 seconds
    protected List<ItemStack> itemStacks;

    public ItemStackWidget(String id, int x, int y, float z, int width, int height, Component narration,
                           Screen parentScreen, float scale, List<ItemStack> itemStacks) {
        super(id, x, y, z, width, height, narration, parentScreen, scale);
        this.itemStacks = itemStacks;
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible() && !this.itemStacks.isEmpty()) {
            int changeIndex = Mth.floor((float) (System.currentTimeMillis() - this.creationTime) / this.changeTime);
            ItemStack stack = this.itemStacks.get(changeIndex % this.itemStacks.size());
            if (stack.isEmpty()) {
                return;
            }
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(this.getX() + this.scale * 2.0D, this.getY() + this.scale * 2.0D, 150 + this.zLevel);
            guiGraphics.renderItem(stack, this.getX(), this.getY());
            if (stack.getCount() > 1) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0.0D, 0.0D, 10.0D);
                int xOff = 9;
                int yOff = 6;
                guiGraphics.drawString(this.minecraft.font, String.valueOf(stack.getCount()), (this.getX() + xOff) / this.scale, (this.getY() + yOff) / this.scale, Color.BLACK.getRGB(), false);
                guiGraphics.pose().popPose();
            }
            if (this.isInside(mouseX, mouseY)) {
                guiGraphics.renderTooltip(this.minecraft.font, stack, mouseX, mouseY);
            }
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public void restore(NestedWidget backup) {
        if (backup instanceof ItemStackWidget old) {
            this.creationTime = old.creationTime;
        }
    }

}
