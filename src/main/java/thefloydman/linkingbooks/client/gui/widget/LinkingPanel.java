package thefloydman.linkingbooks.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

public class LinkingPanel extends Widget {

    public LinkingPanel(int x, int y, int width, int height, ITextComponent narration) {
        super(x, y, width, height, narration);
    }

    @Override
    public void func_230431_b_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.func_230431_b_(matrixStack, mouseX, mouseY, partialTicks);
    }

}
