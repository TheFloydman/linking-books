package thefloydman.linkingbooks.fluid;

import java.awt.Color;

import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import thefloydman.linkingbooks.block.ModBlocks;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.FluidNames;
import thefloydman.linkingbooks.util.Reference.Resources;

public class ModFluids {

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS,
            Reference.MOD_ID);

    public static final RegistryObject<FlowingFluid> INK = FLUIDS.register(FluidNames.INK,
            () -> new InkFluid.Source(makeInkProperties()));
    public static final RegistryObject<FlowingFluid> FLOWING_INK = FLUIDS.register(FluidNames.FLOWING_INK,
            () -> new InkFluid.Flowing(makeInkProperties()));

    private static ForgeFlowingFluid.Properties makeInkProperties() {
        return new ForgeFlowingFluid.Properties(INK, FLOWING_INK,
                FluidAttributes.builder(Resources.INK_TEXTURE, Resources.FLOWING_INK_TEXTURE)
                        .color(new Color(0.1F, 0.1F, 0.1F).getRGB()).viscosity(1000)).bucket(ModItems.INK_BUCKET)
                                .block(ModBlocks.INK).explosionResistance(100.0F).levelDecreasePerBlock(2)
                                .slopeFindDistance(2).tickRate(15);
    }

}