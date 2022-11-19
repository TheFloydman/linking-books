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

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.LinkEffectTypeNames;

public class LinkEffectTypes {

    public static final DeferredRegister<LinkEffect.Type> LINK_EFFECT_TYPES = DeferredRegister
            .create(Reference.getAsResourceLocation("linkeffecttypes"), Reference.MOD_ID);

    public static final RegistryObject<BasicLinkEffect.Type> BASIC = LINK_EFFECT_TYPES
            .register(LinkEffectTypeNames.BASIC, BasicLinkEffect.Type::new);

    public static final RegistryObject<MobEffectLinkEffect.Type> MOB_EFFECT = LINK_EFFECT_TYPES
            .register(LinkEffectTypeNames.MOB_EFFECT, MobEffectLinkEffect.Type::new);

}
