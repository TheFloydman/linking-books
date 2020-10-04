package thefloydman.linkingbooks.client.gui.widget;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LinkingPanelWidget extends NestedWidget {

    private ItemStack book = ItemStack.EMPTY;

    public LinkingPanelWidget(int x, int y, float zLevel, int width, int height, ITextComponent narration,
            ItemStack book) {
        super(x, y, width, height, narration);
        this.book = book;
    }

    @Override
    public void func_230431_b_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!this.field_230694_p_) {
            return;
        }
        this.fill(matrixStack, this.field_230690_l_, this.field_230691_m_, this.field_230690_l_ + this.field_230688_j_,
                this.field_230691_m_ + this.field_230689_k_, Color.BLACK.getRGB());

        this.renderChildren(matrixStack, mouseX, mouseY, partialTicks);
    }

}
