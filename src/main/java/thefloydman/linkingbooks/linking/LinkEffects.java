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
package thefloydman.linkingbooks.linking;

import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.LinkEffectNames;

public class LinkEffects {

    public static final DeferredRegister<LinkEffect> LINK_EFFECTS = DeferredRegister.create(LinkEffect.class,
            Reference.MOD_ID);

    public static final RegistryObject<PotionLinkEffect> POISON_EFFECT = LINK_EFFECTS
            .register(LinkEffectNames.POISON_EFFECT, () -> new PotionLinkEffect(Effects.POISON, 20 * 10));

    public static final RegistryObject<IntraAgeLinkingLinkEffect> INTRAAGE_LINKING = LINK_EFFECTS
            .register(LinkEffectNames.INTRAAGE_LINKING, () -> new IntraAgeLinkingLinkEffect());

}
