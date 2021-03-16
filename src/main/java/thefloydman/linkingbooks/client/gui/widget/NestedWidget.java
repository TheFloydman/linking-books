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

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)

public abstract class NestedWidget extends Widget {

    public float zLevel = 0.0F;
    protected final List<NestedWidget> children = Lists.newArrayList();
    protected final List<IGuiEventListener> listeners = Lists.newArrayList();

    public NestedWidget(int x, int y, int width, int height, ITextComponent narration) {
        super(x, y, width, height, narration);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderChildren(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void renderChildren(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (NestedWidget widget : this.children) {
            widget.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.onMouseClickChildren(mouseX, mouseY, button);
    }

    public boolean onMouseClickChildren(double mouseX, double mouseY, int button) {
        boolean eatenGeneral = false;
        for (NestedWidget widget : this.children) {
            boolean eatenChild = widget.mouseClicked(mouseX, mouseY, button);
            eatenGeneral = eatenChild == true ? eatenChild : eatenGeneral;
        }
        return eatenGeneral;
    }

    public boolean isInside(double x, double y) {
        return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
    }

    public <T extends NestedWidget> T addChild(T widget) {
        this.children.add(widget);
        return widget;
    }

    public void addListener(IGuiEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * A z-level-dependent version of AbstractGui::fill.
     */
    /**
     * Z-sensitive fill method.
     */
    public void zFill(final MatrixStack matrixStack, int xStart, int yStart, int xEnd, int yEnd, final int color) {

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

        final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        Matrix4f matrix = matrixStack.last().pose();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, xStart, yEnd, this.zLevel).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(matrix, xEnd, yEnd, this.zLevel).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(matrix, xEnd, yStart, this.zLevel).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(matrix, xStart, yStart, this.zLevel).color(red, green, blue, alpha).endVertex();
        bufferBuilder.end();
        WorldVertexBufferUploader.end(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public void point(final MatrixStack matrixStack, int x, int y, final int color) {
        this.zFill(matrixStack, x, y, x + 1, y + 1, color);
    }

    /**
     * Returns a positive difference if the zLevel needs to be raised and a negative
     * difference if it should be lowered.
     */
    public static float zDifference(MatrixStack matrixStack, float zLevel) {
        FloatBuffer floatBuffer = FloatBuffer.allocate(16);
        matrixStack.last().pose().store(floatBuffer);
        float currentZ = floatBuffer.get(10);
        return zLevel - currentZ < 0 ? zLevel - MathHelper.abs(currentZ) : zLevel + MathHelper.abs(currentZ);
    }

}
