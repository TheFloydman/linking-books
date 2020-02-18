package thefloydman.linkingbooks.fluid;

import net.minecraft.fluid.IFluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class InkFluid extends ForgeFlowingFluid {

    protected InkFluid(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isSource(IFluidState state) {
        return false;
    }

    @Override
    public int getLevel(IFluidState p_207192_1_) {
        return 0;
    }

}
