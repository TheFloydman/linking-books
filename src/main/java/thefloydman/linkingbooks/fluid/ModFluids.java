/*******************************************************************************
 * Linking Books
 * Copyright (C) 2021  TheFloydman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You can reach TheFloydman on Discord at Floydman#7171.
 *******************************************************************************/
package thefloydman.linkingbooks.fluid;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thefloydman.linkingbooks.block.ModBlocks;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.util.Reference;

public class ModFluids {

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS,
            Reference.MOD_ID);

    public static final RegistryObject<FlowingFluid> INK = FLUIDS.register(Reference.FluidNames.INK,
            () -> new InkFluid.Source(makeInkProperties()) {
            });

    public static final RegistryObject<FlowingFluid> FLOWING_INK = FLUIDS.register(Reference.FluidNames.FLOWING_INK,
            () -> new InkFluid.Flowing(makeInkProperties()) {
            });

    public static ForgeFlowingFluid.Properties makeInkProperties() {
        return new ForgeFlowingFluid.Properties(ModFluidTypes.INK, INK, FLOWING_INK).block(ModBlocks.INK)
                .bucket(ModItems.INK_BUCKET).explosionResistance(100.0F).levelDecreasePerBlock(2).slopeFindDistance(2)
                .tickRate(15);
    }

}