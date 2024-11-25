/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2019-2024 Dan Floyd ("TheFloydman").
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package thefloydman.linkingbooks.world.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.network.server.AddChunkLoaderMessage;
import thefloydman.linkingbooks.network.server.RemoveChunkLoaderMessage;

import javax.annotation.Nonnull;

public class LinkingBookMenuType extends AbstractContainerMenu {

    public boolean holdingBook = false;
    public int bookColor = DyeColor.GREEN.getFireworkColor();
    public LinkData linkData = LinkData.EMPTY;
    public boolean canLink = false;
    public CompoundTag linkingPanelImage = new CompoundTag();

    public LinkingBookMenuType(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.LINKING_BOOK.get(), containerId);
    }

    public LinkingBookMenuType(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        super(ModMenuTypes.LINKING_BOOK.get(), containerId);
        this.holdingBook = extraData.readBoolean();
        this.bookColor = extraData.readInt();
        this.linkData = extraData.readJsonWithCodec(LinkData.CODEC);
        this.canLink = extraData.readBoolean();
        this.linkingPanelImage = extraData.readNbt();
        PacketDistributor.sendToServer(new AddChunkLoaderMessage(this.linkData));
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    @Override
    public void removed(@Nonnull Player player) {
        PacketDistributor.sendToServer(new RemoveChunkLoaderMessage());
        super.removed(player);
    }

    @Override
    public @Nonnull ItemStack quickMoveStack(@Nonnull Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }

}