/*******************************************************************************
 * Copyright 2019-2022 Dan Floyd ("TheFloydman")
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package thefloydman.linkingbooks.fluid;

import java.awt.Color;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
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