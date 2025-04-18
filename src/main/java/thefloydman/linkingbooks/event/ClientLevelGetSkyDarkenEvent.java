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

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.Event;

import javax.annotation.Nullable;

/**
 * <p>This event is fired whenever the method {@link ClientLevel#getSkyDarken(float) Level.getSkyDarken} is called.</p>
 * <p>Setting skyDarken to {@code null} will use the vanilla value.
 * A value of 0.0F will maximize skylight, while a value of 1.0F will minimize skylight.
 * Values outside of this range will be clamped.</p>
 * <p>This event is not cancellable and does not have a result.
 * This event is fired on the {@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS NeoForge.EVENT_BUS}.</p>
 */
public class ClientLevelGetSkyDarkenEvent extends Event {

    protected final ClientLevel clientLevel;
    protected Float skyDarken;

    public ClientLevelGetSkyDarkenEvent(ClientLevel clientLevel) {
        this.clientLevel = clientLevel;
    }

    public ClientLevel getClientLevel() {
        return this.clientLevel;
    }

    public @Nullable Float getSkyDarken() {
        return skyDarken;
    }

    public void setSkyDarken(@Nullable Float skyDarken) {
        this.skyDarken = skyDarken == null ? null : 1.0F - Mth.clamp(skyDarken, 0.0F, 1.0F);
    }

}