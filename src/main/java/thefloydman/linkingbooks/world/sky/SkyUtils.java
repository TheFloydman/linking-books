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

package thefloydman.linkingbooks.world.sky;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.Mth;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import thefloydman.linkingbooks.Reference;
import thefloydman.linkingbooks.client.renderer.world.SkyObjectDetails;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class SkyUtils {

    public static @Nonnull List<SkyObjectDetails> generateSkyObjectDetails(@Nonnull SkyObject skyObject, long time) {
        List<SkyObjectDetails> allSkyObjectDetails = new ArrayList<>();
        Map<SkyObject, Vector3f> systemState = setupSystemState(skyObject, time);
        Pair<SkyObject, Vector3f> self = getSelf(systemState);
        if (self != null) {
            systemState.remove(self.getFirst());
            // Determine rotation of observer.
            float observerRotationAngle = self.getFirst().rotationPeriod() <= 0L ? 0.0F : (float) time / self.getFirst().rotationPeriod() % 1.0F * Mth.TWO_PI;
            Vector3f observerRotAxis = new Vector3f(0.0F, 1.0F, 0.0F);
            Quaternionf observerAxialTiltQuaternion = new Quaternionf(new AxisAngle4f(self.getFirst().axisTilt(), new Vector3f(0.0F, 0.0F, 1.0F)));
            observerAxialTiltQuaternion.transform(observerRotAxis);
            Quaternionf observerRotationQuaternion = new Quaternionf(new AxisAngle4f(observerRotationAngle, observerRotAxis));
            Quaternionf observerRotQuat = new Quaternionf(observerAxialTiltQuaternion);
            observerRotQuat.mul(observerRotationQuaternion);
            observerRotQuat.mul(new Quaternionf(new AxisAngle4f(Mth.HALF_PI, new Vector3f(0.0F, 1.0F, 0.0F))));

            for (Map.Entry<SkyObject, Vector3f> entry : systemState.entrySet()) {
                // Calculate vector pointing from observer to SkyObject.
                Vector3f diffVec = entry.getValue().sub(self.getSecond());
                Vector3f vecToObject = diffVec.normalize(new Vector3f());
                // Generate final quaternion.
                Quaternionf undoRot = new Quaternionf(observerRotQuat);
                undoRot.conjugate();
                Quaternionf quat = new Quaternionf().rotationTo(new Vector3f(0.0F, 1.0F, 0.0F), vecToObject);
                undoRot.mul(quat, quat);
                allSkyObjectDetails.add(new SkyObjectDetails(entry.getKey(), quat, diffVec.length()));
            }
            allSkyObjectDetails.sort(Comparator.comparingDouble(SkyObjectDetails::getDistance).reversed());
        }
        return allSkyObjectDetails;
    }

    public static List<HorizonInfo> generateHorizonInfos(@Nonnull SkyObject skyObject, long time) {

        List<SkyObjectDetails> foobar = generateSkyObjectDetails(skyObject, time).stream().filter(skyObjectDetails -> skyObjectDetails.getSkyObject().providedLight() > 0).toList();
        List<HorizonInfo> allHorizonInfos = new ArrayList<>();
        for (SkyObjectDetails skyObjectDetails : foobar) {
            Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
            Vector3f vecToObject = skyObjectDetails.getQuaternion().transform(up);
            Vector3f vecToHorizon = new Vector3f(vecToObject.x(), 0.0F, vecToObject.z()).normalize(new Vector3f());
            Vector3f vecNorth = new Vector3f(1.0F, 0.0F, 0.0F);
            float horizontalAngle = vecNorth.angle(vecToHorizon);
            float verticalAngle = vecToObject.angle(vecToHorizon) * (vecToObject.y() > vecToHorizon.y() ? 1 : -1);
            allHorizonInfos.add(new HorizonInfo(skyObjectDetails.getSkyObject(), horizontalAngle, verticalAngle));
        }

        return allHorizonInfos;
    }

    public static @Nonnull Map<SkyObject, Vector3f> setupSystemState(@Nonnull SkyObject skyObject, long time) {
        Map<SkyObject, Vector3f> systemState = new HashMap<>();

        // Determine position in tilted circular orbit.
        float positionAngle = skyObject.orbitPeriod() <= 0L ? 0.0F : (float) time / (float) skyObject.orbitPeriod() % 1.0F * Mth.TWO_PI;
        Vector3f orbitAxis = new Vector3f(-1.0F, 0.0F, 0.0F);
        Quaternionf orbitTiltQuaternion = new Quaternionf(new AxisAngle4f(skyObject.orbitTilt(), new Vector3f(0.0F, 0.0F, 1.0F)));
        orbitTiltQuaternion.transform(orbitAxis);
        Quaternionf positionQuaternion = new Quaternionf(new AxisAngle4f(positionAngle, orbitAxis));
        Vector3f currentPosition = new Vector3f(0.0F, 0.0F, skyObject.orbitRadius());
        Quaternionf posQuat = new Quaternionf(orbitTiltQuaternion);
        positionQuaternion.mul(posQuat);
        orbitTiltQuaternion.transform(currentPosition);
        positionQuaternion.transform(currentPosition);

        systemState.put(skyObject, currentPosition);

        for (SkyObject child : skyObject.children()) {
            systemState.putAll(
                    setupSystemState(child, time).entrySet().stream().map(entry ->
                            Map.entry(entry.getKey(), entry.getValue().add(currentPosition))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            );
        }

        return systemState;
    }

    public static @Nullable Pair<SkyObject, Vector3f> getSelf(@Nonnull Map<SkyObject, Vector3f> systemState) {
        Optional<Pair<SkyObject, Vector3f>> optional = systemState
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().name().equals(Reference.getAsResourceLocation("self")))
                .findFirst()
                .map(entry -> Pair.of(entry.getKey(), entry.getValue()));
        return optional.orElse(null);
    }

}