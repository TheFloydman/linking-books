package thefloydman.linkingbooks.fluid;

import net.minecraft.fluid.IFluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class InkFluid extends ForgeFlowingFluid {

    protected InkFluid(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isSource(IFluidState state) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getLevel(IFluidState p_207192_1_) {
        // TODO Auto-generated method stub
        return 0;
    }

}
