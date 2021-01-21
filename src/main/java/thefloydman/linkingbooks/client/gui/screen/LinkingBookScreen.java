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
        this.xSize = 256;
        this.ySize = 192;
    }

    @Override
    protected void init() {
        super.init();
        NestedWidget linkingBook = this.addButton(new LinkingBookWidget(this.guiLeft, this.guiTop, 0.0F, this.xSize,
                this.ySize, new StringTextComponent("Linking Book"), this.getContainer().holdingBook,
                this.getContainer().bookColor, this.getContainer().linkData, this.getContainer().canLink,
                this.getContainer().linkingPanelImage));
        linkingBook.addListener(this);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
    }

}
