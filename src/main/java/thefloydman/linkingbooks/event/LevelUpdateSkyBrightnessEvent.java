/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2025 Dan Floyd ("TheFloydman").
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

package thefloydman.linkingbooks.event;

import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

import javax.annotation.Nullable;

/**
 * <p>This event is fired whenever the method {@link Level#updateSkyBrightness() Level.updateSkyBrightness} is called on the client and server.</p>
 * <p>This event is not cancellable and does not have a result.
 * This event is fired on the {@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS NeoForge.EVENT_BUS}.</p>
 */
public class LevelUpdateSkyBrightnessEvent extends Event implements ICancellableEvent {

    private final Level level;
    protected Integer skyDarken;

    public LevelUpdateSkyBrightnessEvent(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return this.level;
    }

    public @Nullable Integer getSkyDarken() {
        return this.skyDarken;
    }

    public void setSkyDarken(@Nullable Integer skyDarken) {
        this.skyDarken = skyDarken == null ? null : Math.clamp(skyDarken, 0, 15);
    }

}