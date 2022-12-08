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

import thefloydman.linkingbooks.api.linking.LinkEffect;

/**
 * A Link Effect only containing a name.
 *
 */
public class BasicLinkEffect extends LinkEffect {

    public static class Type extends LinkEffect.Type {

        @Override
        public LinkEffect fromJson(JsonObject json) {
            return new BasicLinkEffect();
        }
    }

}