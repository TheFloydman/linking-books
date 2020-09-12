package thefloydman.linkingbooks.client.gui.widget;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class NestedWidget extends Widget {

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

}
