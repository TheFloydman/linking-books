package thefloydman.linkingbooks.api.capability;

import net.minecraft.nbt.CompoundNBT;

public interface IColorCapability {

    public void setColor(int color);

    public int getColor();

    public CompoundNBT writeToShareTag(CompoundNBT nbt);

    public void readFromShareTag(CompoundNBT nbt);

}
