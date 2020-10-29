package thefloydman.linkingbooks.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import thefloydman.linkingbooks.client.gui.widget.LinkingBookWidget;
import thefloydman.linkingbooks.client.gui.widget.NestedWidget;
import thefloydman.linkingbooks.inventory.container.LinkingBookContainer;

public class LinkingBookScreen extends ContainerScreen<LinkingBookContainer> {

    public LinkingBookScreen(LinkingBookContainer container, PlayerInventory inventory, ITextComponent narration) {
        super(container, inventory, narration);
    }

    @Override
    protected void init() {
        NestedWidget linkingBook = this.addButton(new LinkingBookWidget((this.width - 256) / 2, (this.height - 192) / 2,
                0.0F, 256, 192, new StringTextComponent("Linking Book"), this.getContainer().holdingBook,
                this.getContainer().bookColor, this.getContainer().linkData, this.getContainer().canLink,
                this.getContainer().linkingPanelImage));
        linkingBook.addListener(this);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
    }

}
