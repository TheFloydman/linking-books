package thefloydman.linkingbooks.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import thefloydman.linkingbooks.client.gui.widget.LinkingBookWidget;
import thefloydman.linkingbooks.inventory.container.LinkingBookContainer;

public class LinkingBookScreen extends ContainerScreen<LinkingBookContainer> {

    public LinkingBookScreen(LinkingBookContainer container, PlayerInventory inventory, ITextComponent narration) {
        super(container, inventory, narration);
    }

    @Override
    protected void func_231160_c_() {
        this.func_230480_a_(new LinkingBookWidget((this.field_230708_k_ - 256) / 2, (this.field_230709_l_ - 192) / 2,
                256, 192, new StringTextComponent("Linking Book"), null));
    }

    @Override
    protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {

    }

}
