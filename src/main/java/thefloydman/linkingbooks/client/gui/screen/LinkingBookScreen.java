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

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import thefloydman.linkingbooks.client.gui.widget.LinkingBookWidget;
import thefloydman.linkingbooks.client.gui.widget.NestedWidget;
import thefloydman.linkingbooks.inventory.container.LinkingBookMenuType;

public class LinkingBookScreen extends AbstractContainerScreen<LinkingBookMenuType> {

    public LinkingBookScreen(LinkingBookMenuType container, Inventory inventory, Component narration) {
        super(container, inventory, narration);
        this.imageWidth = 256;
        this.imageHeight = 180;
    }

    @Override
    protected void init() {
        super.init();
        NestedWidget linkingBook = this.addRenderableWidget(new LinkingBookWidget("linking book", this.leftPos,
                this.topPos, 100.0F, this.imageWidth, this.imageHeight, Component.literal("Linking Book"), this, 1.0F,
                this.getMenu().holdingBook, this.getMenu().bookColor, this.getMenu().linkData, this.getMenu().canLink,
                this.getMenu().linkingPanelImage));
        linkingBook.addListener(this);
    }

    @Override
    protected void renderLabels(PoseStack p_97808_, int p_97809_, int p_97810_) {
    }

    @Override
    protected void renderBg(PoseStack p_97787_, float p_97788_, int p_97789_, int p_97790_) {
    }

}
