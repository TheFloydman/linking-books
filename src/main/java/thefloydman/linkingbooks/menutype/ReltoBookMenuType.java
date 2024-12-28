/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
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

package thefloydman.linkingbooks.menutype;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import thefloydman.linkingbooks.Reference;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.network.server.AddChunkLoaderMessage;
import thefloydman.linkingbooks.network.server.RemoveChunkLoaderMessage;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class ReltoBookMenuType extends AbstractContainerMenu {

    public UUID owner = UUID.randomUUID();

    public ReltoBookMenuType(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.RELTO_BOOK.get(), containerId);
    }

    public ReltoBookMenuType(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        super(ModMenuTypes.LINKING_BOOK.get(), containerId);
        this.owner = extraData.readUUID();
        LinkData linkData = new LinkData(Reference.getAsResourceLocation(String.format("relto_%s", this.owner)), new BlockPos(-11, 200, 23), -90.0F, UUID.randomUUID(), List.of(Reference.getAsResourceLocation("intraage_linking")));
        if (Reference.isImmersivePortalsLoaded()) {
            PacketDistributor.sendToServer(new AddChunkLoaderMessage(linkData));
        }
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    @Override
    public void removed(@Nonnull Player player) {
        if (Reference.isImmersivePortalsLoaded()) {
            PacketDistributor.sendToServer(new RemoveChunkLoaderMessage());
        }
        super.removed(player);
    }

    @Override
    public @Nonnull ItemStack quickMoveStack(@Nonnull Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }

}