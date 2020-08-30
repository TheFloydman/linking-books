package thefloydman.linkingbooks.fluid;

import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.FluidNames;

public class ModFluids {

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS,
            Reference.MOD_ID);

    public static final RegistryObject<FlowingFluid> INK = FLUIDS.register(FluidNames.INK, InkFluid.Source::new);
    public static final RegistryObject<FlowingFluid> FLOWING_INK = FLUIDS.register(FluidNames.FLOWING_INK,
            InkFluid.Flowing::new);

}