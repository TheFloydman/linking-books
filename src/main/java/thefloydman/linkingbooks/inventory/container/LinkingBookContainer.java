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
package thefloydman.linkingbooks.inventory.container;

import com.qouteall.immersive_portals.chunk_loading.ChunkVisibilityManager.ChunkLoader;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;

public class LinkingBookContainer extends Container {

    public boolean holdingBook = false;
    public int bookColor = DyeColor.GREEN.getColorValue();
    public ILinkData linkData = LinkData.LINK_DATA.getDefaultInstance();
    public boolean canLink = false;
    public CompoundNBT linkingPanelImage = new CompoundNBT();
    private ChunkLoader chunkLoader;

    public LinkingBookContainer(int windowId, PlayerInventory playerInventory) {
        super(ModContainerTypes.LINKING_BOOK.get(), windowId);
    }

    public LinkingBookContainer(int windowId, PlayerInventory playerInventory, PacketBuffer extraData) {
        this(windowId, playerInventory);
        this.holdingBook = extraData.readBoolean();
        this.bookColor = extraData.readInt();
        this.linkData.read(extraData);
        this.canLink = extraData.readBoolean();
        this.linkingPanelImage = extraData.readCompoundTag();
        /*
         * TODO: Enable Immersive Portals support when chunkloading is working
         * correctly. if (ModList.get().isLoaded("immersive_portals") &&
         * !playerInventory.player.getEntityWorld().isRemote() && this.canLink) {
         * this.chunkLoader = new ChunkVisibilityManager.ChunkLoader( new
         * DimensionalChunkPos( RegistryKey.getOrCreateKey(Registry.WORLD_KEY,
         * this.linkData.getDimension()), new ChunkPos(this.linkData.getPosition())),
         * ModConfig.COMMON.linkingPanelChunkLoadRadius.get());
         * NewChunkTrackingGraph.addGlobalAdditionalChunkLoader(this.chunkLoader); }
         */
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        /*
         * TODO: Enable Immersive Portals support when chunkloading is working if
         * (ModList.get().isLoaded("immersive_portals") &&
         * !player.getEntityWorld().isRemote() && this.canLink && this.chunkLoader !=
         * null) {
         * NewChunkTrackingGraph.removeGlobalAdditionalChunkLoader(this.chunkLoader); }
         */
        super.onContainerClosed(player);
    }

}
