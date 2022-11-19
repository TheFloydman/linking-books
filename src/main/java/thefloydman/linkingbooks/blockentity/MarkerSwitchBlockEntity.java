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
package thefloydman.linkingbooks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;

public class MarkerSwitchBlockEntity extends BlockEntity implements IItemHandler {

    private ItemStack item = ItemStack.EMPTY;

    public MarkerSwitchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.MARKER_SWITCH.get(), pos, state);
    }

    /**
     * Returns the item in this block without removing it.
     */
    public ItemStack getItem() {
        return this.item;
    }

    public boolean setItem(ItemStack stack) {
        this.item = stack.copy();
        this.setChanged();
        return true;
    }

    public boolean hasItem() {
        return !this.getItem().isEmpty();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("item", Tag.TAG_COMPOUND)) {
            this.item = ItemStack.of(nbt.getCompound("item"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("item", this.item.save(new CompoundTag()));
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithFullMetadata();
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
        super.handleUpdateTag(nbt);
        this.load(nbt);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        this.load(packet.getTag());
    }

    public void clearContent() {
        this.item = ItemStack.EMPTY;
        this.setChanged();
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.item;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        this.item = stack;
        this.setChanged();
        return this.item;
    }

    @Override
    public ItemStack extractItem(int slot, int count, boolean simulate) {
        ItemStack stack = this.item.split(count);
        this.setChanged();
        return stack;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

}
