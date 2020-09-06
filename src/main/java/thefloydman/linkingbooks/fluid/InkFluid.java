package thefloydman.linkingbooks.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateContainer;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class InkFluid extends ForgeFlowingFluid {

    protected InkFluid(Properties properties) {
        super(properties);
    }

    public static class Flowing extends InkFluid {

        protected Flowing(Properties properties) {
            super(properties);
        }

        @Override
        protected void fillStateContainer(StateContainer.Builder<Fluid, FluidState> builder) {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL_1_8);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends InkFluid {
        protected Source(Properties properties) {
            super(properties);
        }

        @Override
        public int getLevel(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

}
