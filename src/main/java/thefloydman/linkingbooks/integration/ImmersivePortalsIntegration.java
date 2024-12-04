/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
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

package thefloydman.linkingbooks.integration;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.joml.Matrix4f;
import qouteall.imm_ptl.core.ClientWorldLoader;
import qouteall.imm_ptl.core.api.PortalAPI;
import qouteall.imm_ptl.core.chunk_loading.ChunkLoader;
import qouteall.imm_ptl.core.chunk_loading.DimensionalChunkPos;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.imm_ptl.core.portal.nether_portal.BlockPortalShape;
import qouteall.imm_ptl.core.render.GuiPortalRendering;
import qouteall.imm_ptl.core.render.PortalEntityRenderer;
import qouteall.imm_ptl.core.render.context_management.WorldRenderInfo;
import thefloydman.linkingbooks.LinkingBooksConfig;
import thefloydman.linkingbooks.component.ModDataComponents;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.client.ImageUtils;
import thefloydman.linkingbooks.Reference;
import thefloydman.linkingbooks.entity.LinkingPortalEntity;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.blockentity.LinkTranslatorBlockEntity;

import java.util.*;

public class ImmersivePortalsIntegration {

    private static final WeakHashMap<ServerPlayer, ChunkLoader> CHUNK_LOADERS = new WeakHashMap<>();
    public static final EntityType<LinkingPortalEntity> LINKING_PORTAL_ENTITY_TYPE = EntityType.Builder.<LinkingPortalEntity>of(LinkingPortalEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).setTrackingRange(96).fireImmune().build(Reference.MODID + ":" + Reference.EntityNames.LINKING_PORTAL);
    ;

    public static void addChunkLoader(LinkData linkData, ServerPlayer player) {
        removeChunkLoader(player);
        ChunkLoader chunkLoader = new ChunkLoader(new DimensionalChunkPos(ResourceKey.create(Registries.DIMENSION, linkData.dimension()), new ChunkPos(linkData.blockPos())), LinkingBooksConfig.LINKING_PANEL_CHUNK_LOAD_RADIUS.get());
        PortalAPI.addChunkLoaderForPlayer(player, chunkLoader);
        CHUNK_LOADERS.put(player, chunkLoader);
    }

    public static void removeChunkLoader(ServerPlayer player) {
        ChunkLoader chunkLoader = CHUNK_LOADERS.remove(player);
        if (chunkLoader != null) {
            PortalAPI.removeChunkLoaderForPlayer(player, chunkLoader);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderGuiPortal(LinkData linkData, RenderTarget renderTarget, Minecraft client, int x, int y, int width, int height) {
        Matrix4f cameraTransformation = new Matrix4f();
        cameraTransformation.identity();
        cameraTransformation.mul(Axis.YP.rotationDegrees(linkData.rotation() + 180.0F).get(new Matrix4f()));
        WorldRenderInfo.Builder worldRenderInfoBuilder = new WorldRenderInfo.Builder();
        worldRenderInfoBuilder.setWorld(ClientWorldLoader.getWorld(ResourceKey.create(Registries.DIMENSION, linkData.dimension())));
        worldRenderInfoBuilder.setCameraPos(new Vec3(linkData.blockPos().getX() + 0.5D, linkData.blockPos().getY() + 1.5D, linkData.blockPos().getZ() + 0.5D));
        worldRenderInfoBuilder.setCameraTransformation(cameraTransformation);
        worldRenderInfoBuilder.setRenderDistance(LinkingBooksConfig.LINKING_PANEL_CHUNK_LOAD_RADIUS.get());
        WorldRenderInfo worldRenderInfo = worldRenderInfoBuilder.build();
        GuiPortalRendering.submitNextFrameRendering(worldRenderInfo, renderTarget);

        TextureTarget smallerRenderTarget = new TextureTarget(width, height, true, false);
        ImageUtils.cropShrinkCenterRenderTarget(renderTarget, smallerRenderTarget, width, height, true, false, false, true);
        ImageUtils.drawRenderTarget(smallerRenderTarget, true, false, (int) (x * client.getWindow().getGuiScale()), (int) (y * client.getWindow().getGuiScale()), (int) (width * client.getWindow().getGuiScale()), (int) (height * client.getWindow().getGuiScale()));
        smallerRenderTarget.clear(true);
    }

    public static UUID[] addImmersivePortal(Level world, double[] pos, double width, double height, Set<BlockPos> coveredBlocks, Direction.Axis axis, LinkData linkData, LinkTranslatorBlockEntity tileEntity) {
        if (axis == Direction.Axis.X) {
            axis = Direction.Axis.Z;
        } else if (axis == Direction.Axis.Z) {
            axis = Direction.Axis.X;
        }
        LinkData itemData = new LinkData(linkData.dimension(), linkData.blockPos(), linkData.rotation(), linkData.uuid(), linkData.linkEffects());
        ItemStack stack = ModItems.WRITTEN_LINKING_BOOK.toStack();
        stack.set(ModDataComponents.LINK_DATA, itemData);
        LinkingPortalEntity portal = new LinkingPortalEntity(LINKING_PORTAL_ENTITY_TYPE, world, stack, tileEntity.getBlockPos());
        portal.setPos(pos[0], pos[1], pos[2]);
        BlockPortalShape shape = new BlockPortalShape(coveredBlocks, axis);
        shape.initPortalPosAxisShape(portal, Direction.AxisDirection.POSITIVE);
        PortalManipulation.setPortalTransformation(portal, ResourceKey.create(Registries.DIMENSION, linkData.dimension()), new Vec3(linkData.blockPos().getX() + 0.5D, linkData.blockPos().getY() + (height / 2.0D) + (axis == Direction.Axis.Y ? 2.0D : 0.0D) + 0.5D, linkData.blockPos().getZ() + 0.5D), null, 1.0D);
        PortalManipulation.removeOverlappedPortals(world, portal.position(), portal.getNormal(), p -> p instanceof LinkingPortalEntity, p -> {
        });
        world.addFreshEntity(portal);
        LinkingPortalEntity reversePortal = PortalManipulation.createFlippedPortal(portal, LINKING_PORTAL_ENTITY_TYPE);
        reversePortal.setTileEntityPos(portal.getTileEntityPos());
        PortalManipulation.removeOverlappedPortals(world, reversePortal.position(), reversePortal.getNormal(), p -> p instanceof LinkingPortalEntity, p -> {
        });
        world.addFreshEntity(reversePortal);
        return new UUID[]{portal.getUUID(), reversePortal.getUUID()};
    }

    public static void registerImmersivePortalsEntities(RegisterEvent event) {
        event.register(
                Registries.ENTITY_TYPE,
                registry -> registry.register(Reference.getAsResourceLocation(Reference.EntityNames.LINKING_PORTAL), LINKING_PORTAL_ENTITY_TYPE)
        );
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(LINKING_PORTAL_ENTITY_TYPE, PortalEntityRenderer::new);
    }

    public static List<LinkingPortalEntity> getNearbyLinkingPortals(BlockPos pos, Level level) {
        return level.getEntitiesOfClass(LinkingPortalEntity.class, new AABB(pos.below(64).south(64).west(64).getCenter(), pos.above(64).north(64).east(64).getCenter()), Objects::nonNull);
    }

    public static void deleteLinkingPortals(LinkTranslatorBlockEntity blockEntity) {
        List<LinkingPortalEntity> nearbyPortals = getNearbyLinkingPortals(blockEntity.getBlockPos(), blockEntity.getLevel());
        for (LinkingPortalEntity portal : nearbyPortals) {
            if (portal.getTileEntityPos().equals(blockEntity.getBlockPos())) {
                portal.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }

}