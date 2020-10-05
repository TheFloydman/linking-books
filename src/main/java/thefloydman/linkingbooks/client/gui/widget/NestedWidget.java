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
    public void func_230431_b_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderChildren(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void renderChildren(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (NestedWidget widget : this.children) {
            widget.func_230431_b_(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void func_230982_a_(double mouseX, double mouseY) {
        this.onMouseClickChildren(mouseX, mouseY);
    }

    public void onMouseClickChildren(double mouseX, double mouseY) {
        for (NestedWidget widget : this.children) {
            widget.func_230982_a_(mouseX, mouseY);
        }
    }

    public <T extends NestedWidget> T addChild(T widget) {
        this.children.add(widget);
        return widget;
    }

    public void addListener(IGuiEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * A z-level-dependent version of AbstractGui::func_238467_a_.
     */
    /**
     * Z-sensitive fill method.
     */
    public void fill(final MatrixStack matrixStack, int xStart, int yStart, int xEnd, int yEnd, final int color) {

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

        final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        Matrix4f matrix = matrixStack.getLast().getMatrix();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(matrix, xStart, yEnd, this.zLevel).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(matrix, xEnd, yEnd, this.zLevel).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(matrix, xEnd, yStart, this.zLevel).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(matrix, xStart, yStart, this.zLevel).color(red, green, blue, alpha).endVertex();
        bufferBuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public void point(final MatrixStack matrixStack, int x, int y, final int color) {
        this.fill(matrixStack, x, y, x + 1, y + 1, color);
    }

    /**
     * Returns a positive difference if the zLevel needs to be raised and a negative
     * difference if it should be lowered.
     */
    public static float zDifference(MatrixStack matrixStack, float zLevel) {
        FloatBuffer floatBuffer = FloatBuffer.allocate(16);
        matrixStack.getLast().getMatrix().write(floatBuffer);
        float currentZ = floatBuffer.get(10);
        return zLevel - currentZ < 0 ? zLevel - MathHelper.abs(currentZ) : zLevel + MathHelper.abs(currentZ);
    }

}
