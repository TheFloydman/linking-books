/*******************************************************************************
 * Linking Books
 * Copyright (C) 2021  TheFloydman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You can reach TheFloydman on Discord at Floydman#7171.
 *******************************************************************************/
package thefloydman.linkingbooks.client.gui.widget;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)

public abstract class NestedWidget extends AbstractWidget {

    protected final String id;
    public float zLevel = 0.0F;
    protected final Map<String, NestedWidget> children = Maps.newHashMap();
    protected final List<GuiEventListener> listeners = Lists.newArrayList();
    protected final Minecraft minecraft;

    public NestedWidget(String id, int x, int y, int width, int height, Component narration) {
        super(x, y, width, height, narration);
        this.id = id;
        this.minecraft = Minecraft.getInstance();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderChildren(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void renderChildren(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (NestedWidget widget : this.children.values()) {
            widget.render(matrixStack, mouseX, mouseY, partialTicks);
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
            eatenGeneral = eatenChild == true ? eatenChild : eatenGeneral;
        }
        return eatenGeneral;
    }

    public boolean isInside(double x, double y) {
        return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
    }

    public <T extends NestedWidget> T addChild(T widget) {
        this.children.put(widget.getId(), widget);
        return widget;
    }

    public void addListener(GuiEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * A z-level-dependent version of AbstractGui::fill.
     */
    /**
     * Z-sensitive fill method.
     */
    public void zFill(final PoseStack poseStack, int xStart, int yStart, int xEnd, int yEnd, final int color) {

        if (xStart < xEnd) {
            int endUpdated = xStart;
            xStart = xEnd;
            xEnd = endUpdated;
        }

        if (yStart < yEnd) {
            int endUpdated = yStart;
            yStart = yEnd;
            yEnd = endUpdated;
        }

        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        final float alpha = (color >> 24 & 0xFF) / 255.0f;

        final BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f matrix = poseStack.last().pose();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(matrix, xStart, yEnd, this.zLevel).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(matrix, xEnd, yEnd, this.zLevel).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(matrix, xEnd, yStart, this.zLevel).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(matrix, xStart, yStart, this.zLevel).color(red, green, blue, alpha).endVertex();
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public void point(final PoseStack matrixStack, int x, int y, final int color) {
        this.zFill(matrixStack, x, y, x + 1, y + 1, color);
    }

    /**
     * Returns a positive difference if the zLevel needs to be raised and a negative
     * difference if it should be lowered.
     */
    public static float zDifference(PoseStack matrixStack, float zLevel) {
        FloatBuffer floatBuffer = FloatBuffer.allocate(16);
        matrixStack.last().pose().store(floatBuffer);
        float currentZ = floatBuffer.get(10);
        return zLevel - currentZ < 0 ? zLevel - Mth.abs(currentZ) : zLevel + Mth.abs(currentZ);
    }

    @Override
    public void updateNarration(NarrationElementOutput foo) {
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

}
