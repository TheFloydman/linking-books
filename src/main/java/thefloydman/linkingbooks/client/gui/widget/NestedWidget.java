package thefloydman.linkingbooks.client.gui.widget;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class NestedWidget extends Widget {

    protected final List<NestedWidget> children = Lists.newArrayList();
    protected final NestedWidget parent;

    public NestedWidget(int x, int y, int width, int height, ITextComponent narration, NestedWidget parent) {
        super(x, y, width, height, narration);
        this.parent = parent;
    }

    @Override
    public final void func_230431_b_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderThis(matrixStack, mouseX, mouseY, partialTicks);
        this.renderChildren(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void renderThis(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    }

    public final void renderChildren(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < this.children.size(); ++i) {
            this.children.get(i).func_230431_b_(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    protected <T extends NestedWidget> T addChild(T widget) {
        this.children.add(widget);
        return widget;
    }

}
