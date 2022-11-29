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
package thefloydman.linkingbooks.client.sound;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thefloydman.linkingbooks.util.Reference;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
            Reference.MOD_ID);

    public static final RegistryObject<SoundEvent> PAGEFLIP_FORWARD = SOUNDS.register(
            Reference.SoundNames.PAGEFLIP_FORWARD,
            () -> new SoundEvent(Reference.getAsResourceLocation(Reference.SoundNames.PAGEFLIP_FORWARD)));

    public static final RegistryObject<SoundEvent> PAGEFLIP_BACK = SOUNDS.register(Reference.SoundNames.PAGEFLIP_BACK,
            () -> new SoundEvent(Reference.getAsResourceLocation(Reference.SoundNames.PAGEFLIP_BACK)));

    public static final RegistryObject<SoundEvent> BOOK_CLOSE = SOUNDS.register(Reference.SoundNames.BOOK_CLOSE,
            () -> new SoundEvent(Reference.getAsResourceLocation(Reference.SoundNames.BOOK_CLOSE)));

}
