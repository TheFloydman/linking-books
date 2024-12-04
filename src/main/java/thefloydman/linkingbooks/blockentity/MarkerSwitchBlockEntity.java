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

package thefloydman.linkingbooks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class MarkerSwitchBlockEntity extends BlockEntity implements IItemHandler {

    private @Nonnull ItemStack item = ItemStack.EMPTY;

    public MarkerSwitchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.MARKER_SWITCH.get(), pos, state);
    }

    public boolean hasItem() {
        return !this.getStackInSlot(0).isEmpty();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        Level level = this.getLevel();
        if (level != null) {
            level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public void loadAdditional(@Nonnull CompoundTag nbt, @Nonnull HolderLookup.Provider registryAccess) {
        super.loadAdditional(nbt, registryAccess);
        this.item = ItemStack.parseOptional(registryAccess, nbt.getCompound("item"));
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag nbt, @Nonnull HolderLookup.Provider registryAccess) {
        super.saveAdditional(nbt, registryAccess);
        nbt.put("item", this.item.saveOptional(registryAccess));
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @Nonnull CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registryAccess) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, registryAccess);
        return tag;
    }

    @Override
    public void handleUpdateTag(@Nonnull CompoundTag nbt, @Nonnull HolderLookup.Provider registryAccess) {
        super.handleUpdateTag(nbt, registryAccess);
        this.loadAdditional(nbt, registryAccess);
    }

    @Override
    public void onDataPacket(@Nonnull Connection net, @Nonnull ClientboundBlockEntityDataPacket packet, @Nonnull HolderLookup.Provider registryAccess) {
        super.onDataPacket(net, packet, registryAccess);
        this.loadAdditional(packet.getTag(), registryAccess);
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
    public @Nonnull ItemStack getStackInSlot(int slot) {
        return this.item;
    }

    @Override
    public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        int slotLimit = this.getSlotLimit(slot);
        this.item = stack.copyWithCount(slotLimit);
        this.setChanged();
        ItemStack returnStack = stack.copy();
        returnStack.shrink(slotLimit);
        return returnStack;
    }

    @Override
    public @Nonnull ItemStack extractItem(int slot, int count, boolean simulate) {
        ItemStack stack = this.item.split(count);
        this.setChanged();
        return stack;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack itemStack) {
        return true;
    }

}