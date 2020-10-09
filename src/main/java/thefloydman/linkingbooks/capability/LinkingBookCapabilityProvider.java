package thefloydman.linkingbooks.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.api.capability.ILinkData;

public class LinkingBookCapabilityProvider implements ICapabilitySerializable<INBT> {

    private LazyOptional<IColorCapability> bookColor = LazyOptional
            .of(() -> ColorCapability.COLOR.getDefaultInstance());
    private LazyOptional<ILinkData> linkData = LazyOptional.of(() -> LinkData.LINK_DATA.getDefaultInstance());

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap.equals(LinkData.LINK_DATA) && LinkData.LINK_DATA != null) {
            return this.linkData.cast();
        } else if (cap.equals(ColorCapability.COLOR) && ColorCapability.COLOR != null) {
            return this.bookColor.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        INBT colorNBT = ColorCapability.COLOR.getStorage().writeNBT(ColorCapability.COLOR,
                this.bookColor.orElse(ColorCapability.COLOR.getDefaultInstance()), null);
        INBT linkNBT = LinkData.LINK_DATA.getStorage().writeNBT(LinkData.LINK_DATA,
                this.linkData.orElse(LinkData.LINK_DATA.getDefaultInstance()), null);
        CompoundNBT compound = new CompoundNBT();
        compound.put("color", colorNBT);
        compound.put("link_data", linkNBT);
        return compound;
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        if (nbt.getType().equals(CompoundNBT.TYPE)) {
            CompoundNBT compound = (CompoundNBT) nbt;
            if (compound.contains("color", NBT.TAG_INT)) {
                ColorCapability.COLOR.getStorage().readNBT(ColorCapability.COLOR,
                        this.bookColor.orElse(ColorCapability.COLOR.getDefaultInstance()), null, compound.get("color"));
            }
            if (compound.contains("link_data", NBT.TAG_COMPOUND)) {
                LinkData.LINK_DATA.getStorage().readNBT(LinkData.LINK_DATA,
                        this.linkData.orElse(LinkData.LINK_DATA.getDefaultInstance()), null, compound.get("link_data"));
            }
        }
    }

}