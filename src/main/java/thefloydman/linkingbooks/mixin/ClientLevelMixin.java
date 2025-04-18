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

package thefloydman.linkingbooks.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thefloydman.linkingbooks.event.ClientLevelGetSkyDarkenEvent;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @Inject(method = "getSkyDarken", at = @At("HEAD"), cancellable = true)
    public void onGetSunAngle(float partialTicks, CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof ClientLevel level) {
            ClientLevelGetSkyDarkenEvent event = NeoForge.EVENT_BUS.post(new ClientLevelGetSkyDarkenEvent(level));
            if (event.getSkyDarken() != null) {
                cir.setReturnValue(event.getSkyDarken());
                cir.cancel();
            }
        }
    }

}