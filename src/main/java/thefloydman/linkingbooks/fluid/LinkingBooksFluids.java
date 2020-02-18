package thefloydman.linkingbooks.fluid;

import java.awt.Color;
import java.util.function.Supplier;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import thefloydman.linkingbooks.block.LinkingBooksBlocks;
import thefloydman.linkingbooks.item.LinkingBooksItems;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.FluidNames;

@EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class LinkingBooksFluids {

    @ObjectHolder(FluidNames.PLAIN_INK)
    public static final ForgeFlowingFluid PLAIN_INK = null;

    @ObjectHolder(FluidNames.FLOWING_PLAIN_INK)
    public static final ForgeFlowingFluid FLOWING_PLAIN_INK = null;

    @SubscribeEvent
    public static void registerFluids(RegistryEvent.Register<Fluid> event) {

        registerSourceAndFlowing(event.getRegistry(), new Color(0.1F, 0.1F, 0.1F, 1.0F),
                () -> LinkingBooksItems.PLAIN_INK_BUCKET, () -> LinkingBooksBlocks.INK, () -> PLAIN_INK,
                () -> FLOWING_PLAIN_INK, Reference.getAsResourceLocation(FluidNames.PLAIN_INK),
                Reference.getAsResourceLocation(FluidNames.FLOWING_PLAIN_INK));

    }

    public static void registerSourceAndFlowing(IForgeRegistry<Fluid> registry, Color color,
            Supplier<? extends Item> bucket, Supplier<? extends FlowingFluidBlock> block,
            Supplier<? extends Fluid> fluidSource, Supplier<? extends Fluid> fluidFlowing,
            ResourceLocation registryNameSource, ResourceLocation registryNameFlowing) {

        registry.register(new InkFluid.Source(new InkFluid.Properties(fluidSource, fluidFlowing,
                FluidAttributes.builder(Reference.Resources.INK_TEXTURE, Reference.Resources.FLOWING_INK_TEXTURE)
                        .viscosity(3000).color(color.getRGB())).bucket(bucket).block(block))
                                .setRegistryName(registryNameSource));

        registry.register(new InkFluid.Flowing(new InkFluid.Properties(fluidSource, fluidFlowing,
                FluidAttributes.builder(Reference.Resources.INK_TEXTURE, Reference.Resources.FLOWING_INK_TEXTURE)
                        .viscosity(3000).color(color.getRGB())).bucket(bucket).block(block))
                                .setRegistryName(registryNameFlowing));

    }

    /*
     * @SubscribeEvent public static void
     * registerFluids(RegistryEvent.Register<Fluid> event) {
     * 
     * event.getRegistry().register(new InkFluid.Source(new InkFluid.Properties(()
     * -> PLAIN_INK, () -> FLOWING_PLAIN_INK,
     * FluidAttributes.builder(Reference.Resources.INK_TEXTURE,
     * Reference.Resources.FLOWING_INK_TEXTURE) .color(new Color(0.1F, 0.1F, 0.1F,
     * 1.0F).getRGB())) .bucket(() -> LinkingBooksItems.PLAIN_INK_BUCKET).block(()
     * -> LinkingBooksBlocks.INK)) .setRegistryName(Reference.MOD_ID,
     * Reference.FluidNames.PLAIN_INK)); event.getRegistry().register(new
     * InkFluid.Flowing(new InkFluid.Properties(() -> PLAIN_INK, () ->
     * FLOWING_PLAIN_INK, FluidAttributes.builder(Reference.Resources.INK_TEXTURE,
     * Reference.Resources.FLOWING_INK_TEXTURE) .color(new Color(0.1F, 0.1F, 0.1F,
     * 1.0F).getRGB())) .bucket(() -> LinkingBooksItems.PLAIN_INK_BUCKET).block(()
     * -> LinkingBooksBlocks.INK)) .setRegistryName(Reference.MOD_ID,
     * Reference.FluidNames.FLOWING_PLAIN_INK));
     * 
     * }
     */

}
