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
package thefloydman.linkingbooks.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants.NBT;

public class MarkerSwitchTileEntity extends TileEntity implements IInventory {

    private ItemStack item = ItemStack.EMPTY;

    public MarkerSwitchTileEntity() {
        super(ModTileEntityTypes.MARKER_SWITCH.get());
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
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        if (nbt.contains("item", NBT.TAG_COMPOUND)) {
            this.item = ItemStack.of(nbt.getCompound("item"));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.put("item", this.item.save(new CompoundNBT()));
        return nbt;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.save(nbt);
        return new SUpdateTileEntityPacket(this.getBlockPos(), 462, nbt);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        return this.save(nbt);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
        super.handleUpdateTag(state, nbt);
        this.load(state, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager manager, SUpdateTileEntityPacket packet) {
        this.load(Blocks.AIR.defaultBlockState(), packet.getTag());
    }

    @Override
    public void clearContent() {
        this.item = ItemStack.EMPTY;
        this.setChanged();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.item.isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        return this.item;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack stack = this.item.split(count);
        this.setChanged();
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = this.item.copy();
        this.item = ItemStack.EMPTY;
        this.setChanged();
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.item = stack;
        this.setChanged();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

}
