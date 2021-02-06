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
package thefloydman.linkingbooks.integration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.qouteall.immersive_portals.ClientWorldLoader;
import com.qouteall.immersive_portals.chunk_loading.ChunkVisibilityManager;
import com.qouteall.immersive_portals.chunk_loading.ChunkVisibilityManager.ChunkLoader;
import com.qouteall.immersive_portals.chunk_loading.DimensionalChunkPos;
import com.qouteall.immersive_portals.chunk_loading.NewChunkTrackingGraph;
import com.qouteall.immersive_portals.portal.PortalManipulation;
import com.qouteall.immersive_portals.render.GuiPortalRendering;
import com.qouteall.immersive_portals.render.MyRenderHelper;
import com.qouteall.immersive_portals.render.PortalEntityRenderer;
import com.qouteall.immersive_portals.render.context_management.WorldRenderInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.config.ModConfig;
import thefloydman.linkingbooks.entity.LinkingPortalEntity;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.tileentity.LinkTranslatorTileEntity;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.EntityNames;

public class ImmersivePortalsIntegration {

    private static Map<UUID, ChunkLoader> chunkLoaders = new HashMap<UUID, ChunkLoader>();
    public static EntityType<LinkingPortalEntity> linkingPortalEntityType;

    public static void addChunkLoader(ILinkData linkData, ServerPlayerEntity player) {
        removeChunkLoader(linkData, player);
        ChunkLoader chunkLoader = new ChunkVisibilityManager.ChunkLoader(
                new DimensionalChunkPos(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, linkData.getDimension()),
                        new ChunkPos(linkData.getPosition())),
                ModConfig.COMMON.linkingPanelChunkLoadRadius.get());
        chunkLoaders.put(linkData.getUUID(), chunkLoader);
        NewChunkTrackingGraph.addPerPlayerAdditionalChunkLoader(player, chunkLoader);
    }

    public static void removeChunkLoader(ILinkData linkData, ServerPlayerEntity player) {
        ChunkLoader chunkLoader = chunkLoaders.remove(linkData.getUUID());
        if (chunkLoader != null) {
            NewChunkTrackingGraph.removePerPlayerAdditionalChunkLoader(player, chunkLoader);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderGuiPortal(ILinkData linkData, Framebuffer frameBuffer, Minecraft client,
            MatrixStack matrixStack, int x, int y, int width, int height) {
        Matrix4f cameraTransformation = new Matrix4f();
        cameraTransformation.setIdentity();
        cameraTransformation.mul(Vector3f.YP.rotationDegrees(linkData.getRotation() + 180.0F));
        WorldRenderInfo worldRenderInfo = new WorldRenderInfo(
                ClientWorldLoader.getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, linkData.getDimension())),
                new Vector3d(linkData.getPosition().getX() + 0.5D, linkData.getPosition().getY() + 1.5D,
                        linkData.getPosition().getZ() + 0.5D),
                cameraTransformation, null, ModConfig.COMMON.linkingPanelChunkRenderDistance.get(), true);
        GuiPortalRendering.submitNextFrameRendering(worldRenderInfo, frameBuffer);
        MyRenderHelper.drawFramebuffer(frameBuffer, false, false,
                x * (float) client.getMainWindow().getGuiScaleFactor(),
                (x + width) * (float) client.getMainWindow().getGuiScaleFactor(),
                y * (float) client.getMainWindow().getGuiScaleFactor(),
                (y + height) * (float) client.getMainWindow().getGuiScaleFactor());
    }

    public static UUID[] addImmersivePortal(World world, double[] pos, double width, double height, Axis axis,
            ILinkData linkData, LinkTranslatorTileEntity tileEntity) {
        if (axis == Axis.Y) {
            return null;
        }
        ItemStack stack = ModItems.WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        ILinkData itemData = stack.getCapability(LinkData.LINK_DATA).orElse(null);
        itemData.setDimension(linkData.getDimension());
        itemData.setLinkEffects(linkData.getLinkEffects());
        itemData.setPosition(linkData.getPosition());
        itemData.setRotation(linkData.getRotation());
        itemData.setUUID(linkData.getUUID());
        LinkingPortalEntity portal = new LinkingPortalEntity(linkingPortalEntityType, world, stack,
                tileEntity.getPos());
        portal.setPosition(pos[0], pos[1], pos[2]);
        portal.setSquareShape(axis == Axis.X ? new Vector3d(1, 0, 0) : new Vector3d(0, 0, 1), new Vector3d(0, 1, 0),
                width, height);
        PortalManipulation.setPortalTransformation(portal,
                RegistryKey.getOrCreateKey(Registry.WORLD_KEY, linkData.getDimension()),
                new Vector3d(linkData.getPosition().getX() + 0.5D, linkData.getPosition().getY() + (height / 2.0D),
                        linkData.getPosition().getZ() + 0.5D),
                Vector3f.YP.rotationDegrees(linkData.getRotation() + (axis == Axis.X ? 180.0F : -90.0F)), 1.0D);
        PortalManipulation.removeOverlappedPortals(world, portal.func_242274_V(), portal.getNormal(), (p) -> {
            return p instanceof LinkingPortalEntity;
        }, (p) -> {
        });
        world.addEntity(portal);
        LinkingPortalEntity reversePortal = PortalManipulation.createFlippedPortal(portal, linkingPortalEntityType);
        reversePortal.setRotationTransformation(
                Vector3f.YP.rotationDegrees(linkData.getRotation() + (axis == Axis.X ? 0.0F : 90.0F)));
        reversePortal.setTileEntityPos(portal.getTileEntityPos());
        PortalManipulation.removeOverlappedPortals(world, reversePortal.func_242274_V(), reversePortal.getNormal(),
                (p) -> {
                    return p instanceof LinkingPortalEntity;
                }, (p) -> {
                });
        world.addEntity(reversePortal);
        return new UUID[] { portal.getUniqueID(), reversePortal.getUniqueID() };
    }

    @SubscribeEvent
    public static void registerImmersivePortalsEntities(RegistryEvent.Register<EntityType<?>> event) {
        linkingPortalEntityType = EntityType.Builder
                .<LinkingPortalEntity>create(LinkingPortalEntity::new, EntityClassification.MISC).size(1.0F, 1.0F)
                .setTrackingRange(96).immuneToFire()
                .setCustomClientFactory((a, b) -> new LinkingPortalEntity(linkingPortalEntityType, b))
                .build(Reference.MOD_ID + ":" + EntityNames.LINKING_PORTAL);
        linkingPortalEntityType.setRegistryName(Reference.getAsResourceLocation(EntityNames.LINKING_PORTAL));
        event.getRegistry().register(linkingPortalEntityType);
    }

    public static void registerEntityRenderingHandlers() {
        RenderingRegistry.registerEntityRenderingHandler(linkingPortalEntityType, PortalEntityRenderer::new);
    }

    public static List<LinkingPortalEntity> getNearbyLinkingPortals(BlockPos pos, World world) {
        return world.getEntitiesWithinAABB(LinkingPortalEntity.class,
                new AxisAlignedBB(pos.down(64).south(64).west(64), pos.up(64).north(64).east(64)), null);
    }

    public static void deleteLinkingPortals(LinkTranslatorTileEntity blockEntity) {
        List<LinkingPortalEntity> nearbyPortals = getNearbyLinkingPortals(blockEntity.getPos(), blockEntity.getWorld());
        for (LinkingPortalEntity portal : nearbyPortals) {
            if (portal.getTileEntityPos().equals(blockEntity.getPos())) {
                portal.remove();
            }
        }
    }

}