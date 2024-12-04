/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package thefloydman.linkingbooks.client.sound;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.Reference;

import java.util.function.Supplier;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT,
            Reference.MODID);

    public static final Supplier<SoundEvent> PAGEFLIP_FORWARD = SOUNDS
            .register(Reference.SoundNames.PAGEFLIP_FORWARD, () -> SoundEvent
                    .createVariableRangeEvent(Reference.getAsResourceLocation(Reference.SoundNames.PAGEFLIP_FORWARD)));

    public static final Supplier<SoundEvent> PAGEFLIP_BACK = SOUNDS.register(Reference.SoundNames.PAGEFLIP_BACK,
            () -> SoundEvent
                    .createVariableRangeEvent(Reference.getAsResourceLocation(Reference.SoundNames.PAGEFLIP_BACK)));

    public static final Supplier<SoundEvent> BOOK_OPEN = SOUNDS.register(Reference.SoundNames.BOOK_OPEN,
            () -> SoundEvent
                    .createVariableRangeEvent(Reference.getAsResourceLocation(Reference.SoundNames.BOOK_OPEN)));

    public static final Supplier<SoundEvent> BOOK_CLOSE = SOUNDS.register(Reference.SoundNames.BOOK_CLOSE,
            () -> SoundEvent
                    .createVariableRangeEvent(Reference.getAsResourceLocation(Reference.SoundNames.BOOK_CLOSE)));

    public static final Supplier<SoundEvent> LINK = SOUNDS.register(Reference.SoundNames.LINK,
            () -> SoundEvent
                    .createVariableRangeEvent(Reference.getAsResourceLocation(Reference.SoundNames.LINK)));

}