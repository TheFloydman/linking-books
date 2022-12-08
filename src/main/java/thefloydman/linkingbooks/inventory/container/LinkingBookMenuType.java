/*******************************************************************************
 * Copyright 2019-2022 Dan Floyd ("TheFloydman")
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package thefloydman.linkingbooks.inventory.container;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;

public class LinkingBookMenuType extends AbstractContainerMenu {

    public boolean holdingBook = false;
    public int bookColor = DyeColor.GREEN.getFireworkColor();
    public ILinkData linkData = new LinkData();
    public boolean canLink = false;
    public CompoundTag linkingPanelImage = new CompoundTag();

    public LinkingBookMenuType(int windowId, Inventory playerInventory) {
        super(ModMenuTypes.LINKING_BOOK.get(), windowId);
    }

    public LinkingBookMenuType(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(windowId, playerInventory);
        this.holdingBook = extraData.readBoolean();
        this.bookColor = extraData.readInt();
        this.linkData.read(extraData);
        this.canLink = extraData.readBoolean();
        this.linkingPanelImage = extraData.readNbt();
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return null;
    }

}
