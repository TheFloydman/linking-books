package thefloydman.linkingbooks.fluid;

import net.minecraft.fluid.IFluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class InkFluid extends ForgeFlowingFluid {

    public InkFluid(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isSource(IFluidState state) {
        return true;
    }

    @Override
    public int getLevel(IFluidState state) {
        return 8;
    }

}
