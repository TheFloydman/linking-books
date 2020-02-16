package thefloydman.linkingbooks.init;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Items;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;
import thefloydman.linkingbooks.fluid.InkFluid;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.FluidNames;

@EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class LinkingBooksFluids {

    @ObjectHolder(FluidNames.INK)
    public static final InkFluid INK = null;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Fluid> event) {
        event.getRegistry().registerAll(
                new InkFluid(new ForgeFlowingFluid.Properties(() -> new ForgeFlowingFluid.Source(null), () -> new ForgeFlowingFluid.Flowing(null),
                        FluidAttributes.builder(Reference.Resources.INK_TEXTURE,
                                Reference.Resources.FLOWING_INK_TEXTURE)).bucket(() -> Items.BUCKET)
                                        .block(() -> LinkingBooksBlocks.INK)).setRegistryName(Reference.MOD_ID,
                                                Reference.FluidNames.INK));
    }

}
