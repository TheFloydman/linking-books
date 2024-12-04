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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public abstract class NestedWidget extends AbstractWidget {

    protected final String id;
    protected final Map<String, NestedWidget> children = Maps.newHashMap();
    protected final List<GuiEventListener> listeners = Lists.newArrayList();
    protected final Minecraft minecraft;
    protected final Screen parentScreen;
    public final float zLevel;
    protected final float scale;

    public NestedWidget(String id, int x, int y, float z, int width, int height, Component narration,
                        Screen parentScreen, float scale) {
        super(x, y, width, height, narration);
        this.zLevel = z;
        this.id = id;
        this.parentScreen = parentScreen;
        this.scale = scale;
        this.minecraft = Minecraft.getInstance();
    }

    /**
     * Returns a positive difference if the zLevel needs to be raised and a negative
     * difference if it should be lowered.
     */
    public static float zDifference(GuiGraphics guiGraphics, float zLevel) {
        FloatBuffer floatBuffer = FloatBuffer.allocate(16);
        guiGraphics.pose().last().pose().set(floatBuffer);
        float currentZ = floatBuffer.get(10);
        return zLevel - currentZ < 0 ? zLevel - Mth.abs(currentZ) : zLevel + Mth.abs(currentZ);
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
            this.renderChildren(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    public void renderChildren(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        for (NestedWidget widget : this.children.values()) {
            widget.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.onMouseClickChildren(mouseX, mouseY, button);
    }

    public boolean onMouseClickChildren(double mouseX, double mouseY, int button) {
        boolean eatenGeneral = false;
        for (NestedWidget widget : this.children.values()) {
            boolean eatenChild = widget.mouseClicked(mouseX, mouseY, button);
            eatenGeneral = eatenChild ? eatenChild : eatenGeneral;
        }
        return eatenGeneral;
    }

    public boolean isInside(double x, double y) {
        return this.getVisible() && x >= this.getX() && x < this.getX() + this.width * this.scale && y >= this.getY()
                && y < this.getY() + this.height * this.scale;
    }

    public <T extends NestedWidget> T addChild(T widget) {
        this.children.put(widget.getId(), widget);
        return widget;
    }

    public void addListener(GuiEventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationOutput) {
    }

    /**
     * Used to restore important information when the main GUI is resized, like the
     * text in a text field.
     */
    public void restore(NestedWidget backup) {
    }

    public void restoreChildren(NestedWidget backup) {
        for (NestedWidget element : this.children.values()) {
            element.restore(backup.children.get(element.id));
            element.restoreChildren(backup.children.get(element.id));
        }

    }

    public String getId() {
        return this.id;
    }

    public boolean getVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        for (NestedWidget element : this.children.values()) {
            element.setVisible(visible);
        }
    }

}
