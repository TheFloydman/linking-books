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

import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thefloydman.linkingbooks.event.LevelUpdateSkyBrightnessEvent;

@Mixin(Level.class)
public class LevelMixin {

    @Shadow
    private int skyDarken;

    @Inject(method = "updateSkyBrightness", at = @At("TAIL"))
    public void onUpdateBrightness(CallbackInfo callbackInfo) {
        if ((Object) this instanceof Level level) {
            LevelUpdateSkyBrightnessEvent event = NeoForge.EVENT_BUS.post(new LevelUpdateSkyBrightnessEvent(level));
            if (event.getSkyDarken() != null) {
                this.skyDarken = event.getSkyDarken();
            }
        }
    }

}