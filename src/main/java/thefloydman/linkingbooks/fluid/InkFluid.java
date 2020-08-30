package thefloydman.linkingbooks.fluid;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.FluidAttributes;
import thefloydman.linkingbooks.block.ModBlocks;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.util.Reference;

public abstract class InkFluid extends FlowingFluid {

    @Override
    public Fluid getFlowingFluid() {
        return ModFluids.FLOWING_INK.get();
    }

    @Override
    public boolean isEquivalentTo(Fluid fluid) {
        return fluid == ModFluids.INK.get() || fluid == ModFluids.FLOWING_INK.get();
    }

    @Override
    public Fluid getStillFluid() {
        return ModFluids.INK.get();
    }

    @Override
    protected FluidAttributes createAttributes() {
        return FluidAttributes
                .builder(Reference.getAsResourceLocation("block/ink_still"),
                        Reference.getAsResourceLocation("block/ink_flow"))
                .color(new Color(0.1F, 0.1F, 0.1F, 1.0F).getRGB()).build(this);
    }

    @Override
    protected boolean canSourcesMultiply() {
        return false;
    }

    @Override
    protected void beforeReplacingBlock(IWorld world, BlockPos pos, BlockState state) {
        TileEntity tileentity = state.hasTileEntity() ? world.getTileEntity(pos) : null;
        Block.spawnDrops(state, world, pos, tileentity);
    }

    @Override
    protected int getSlopeFindDistance(IWorldReader world) {
        return 2;
    }

    @Override
    protected int getLevelDecreasePerBlock(IWorldReader world) {
        return 1;
    }

    @Override
    public Item getFilledBucket() {
        return ModItems.INK_BUCKET.get();
    }

    @Override
    protected boolean canDisplace(FluidState state, IBlockReader reader, BlockPos pos, Fluid fluid,
            Direction direction) {
        return true;
    }

    @Override
    public int getTickRate(IWorldReader reader) {
        return 15;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    protected BlockState getBlockState(FluidState state) {
        return ModBlocks.INK.get().getDefaultState().with(FlowingFluidBlock.LEVEL,
                Integer.valueOf(getLevelFromState(state)));
    }

    public static class Flowing extends InkFluid {
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
