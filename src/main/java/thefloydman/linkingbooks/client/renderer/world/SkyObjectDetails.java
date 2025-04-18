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

package thefloydman.linkingbooks.client.renderer.world;

import org.joml.Quaternionf;
import thefloydman.linkingbooks.world.sky.SkyObject;

public class SkyObjectDetails {

    private final SkyObject skyObject;
    private final Quaternionf quaternion;
    private final float distance;

    public SkyObjectDetails(SkyObject skyObject, Quaternionf quaternion, float distance) {
        this.skyObject = skyObject;
        this.quaternion = quaternion;
        this.distance = distance;
    }

    public SkyObject getSkyObject() {
        return skyObject;
    }

    public Quaternionf getQuaternion() {
        return quaternion;
    }

    public float getDistance() {
        return distance;
    }

}