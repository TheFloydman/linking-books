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
package thefloydman.linkingbooks.linking;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.api.linking.LinkEffect;

/**
 * Applies a MobEffect to the linking entity when the link has completed.
 */
public class MobEffectLinkEffect extends LinkEffect {

    public static final String TAG_EFFECT = "effect";
    public static final String TAG_TICKS = "ticks";
    private MobEffect effect;
    private int ticks;

    public MobEffectLinkEffect(MobEffect effect, int ticks) {
        this.effect = effect;
        this.ticks = ticks;
    }

    @Override
    public void onLinkEnd(Entity entity, ILinkData linkData) {
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addEffect(new MobEffectInstance(this.effect, ticks));
        }
    }

    public static class Type extends LinkEffect.Type {

        @Override
        public LinkEffect fromJson(JsonObject json) {
            if (json.has(TAG_EFFECT) && json.has(TAG_TICKS)) {
                MobEffect effect = ForgeRegistries.MOB_EFFECTS
                        .getValue(new ResourceLocation(json.get(TAG_EFFECT).getAsString()));
                int ticks = json.get(TAG_TICKS).getAsInt();
                return new MobEffectLinkEffect(effect, ticks);
            }
            return null;
        }
    }

}