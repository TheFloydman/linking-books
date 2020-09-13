package thefloydman.linkingbooks.client.gui.widget;

import java.nio.FloatBuffer;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;
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
        for (int i = 0; i < this.children.size(); ++i) {
            this.children.get(i).func_230431_b_(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    public <T extends NestedWidget> T addChild(T widget) {
        this.children.add(widget);
        return widget;
    }

    public void addListener(IGuiEventListener listener) {
        this.listeners.add(listener);
    }

    public void fill(final MatrixStack matrixStack, final int x, final int y, final int width, final int height,
            final int color) {
        float difference = zDifference(matrixStack);
        matrixStack.translate(0, 0, difference);
        AbstractGui.func_238467_a_(matrixStack, x, y, width, height, color);
        matrixStack.translate(0, 0, -difference);
    }

    /**
     * Returns a positive difference if the zLevel needs to be raised and a negative
     * difference if it should be lowered.
     */
    private float zDifference(MatrixStack matrixStack) {
        FloatBuffer floatBuffer = FloatBuffer.allocate(16);
        matrixStack.getLast().getMatrix().write(floatBuffer);
        int currentZ = (int) floatBuffer.get(10);
        return this.zLevel - currentZ < 0 ? this.zLevel - MathHelper.abs(currentZ)
                : this.zLevel + MathHelper.abs(currentZ);
    }

}
