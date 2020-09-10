package thefloydman.linkingbooks.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LinkingPanelWidget extends NestedWidget {

    public LinkingPanelWidget(int x, int y, int width, int height, ITextComponent narration, NestedWidget parent) {
        super(x, y, width, height, narration, parent);
    }

    @Override
    public void renderThis(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

    }

}
