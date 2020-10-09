package thefloydman.linkingbooks.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants.NBT;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;

public class LinkingLecternTileEntity extends TileEntity {

    private ItemStack book = ItemStack.EMPTY;

    public LinkingLecternTileEntity() {
        super(ModTileEntityTypes.LINKING_LECTERN.get());
    }

    /**
     * Returns the book in this display without removing it.
     */
    public ItemStack getBook() {
        return this.book;
    }

    public boolean setBook(ItemStack stack) {
        this.book = stack.split(1);
        this.markDirty();
        return true;
    }

    public boolean hasBook() {
        return this.getBook().getItem() instanceof WrittenLinkingBookItem;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public void func_230337_a_(BlockState state, CompoundNBT nbt) {
        super.func_230337_a_(state, nbt);
        if (nbt.contains("book", NBT.TAG_COMPOUND)) {
            this.book = ItemStack.read(nbt.getCompound("book"));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);
        nbt.put("book", this.book.write(new CompoundNBT()));
        return nbt;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.write(nbt);
        return new SUpdateTileEntityPacket(this.getPos(), 462, nbt);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        return this.write(nbt);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
        super.handleUpdateTag(state, nbt);
        this.func_230337_a_(state, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager manager, SUpdateTileEntityPacket packet) {
        this.func_230337_a_(Blocks.AIR.getDefaultState(), packet.getNbtCompound());
    }

}
