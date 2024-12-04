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

package thefloydman.linkingbooks.network.server;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thefloydman.linkingbooks.Reference;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

import javax.annotation.Nonnull;
import java.util.UUID;

public record SaveLinkingPanelImageMessage(CompoundTag image, UUID uuid) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SaveLinkingPanelImageMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "save_linking_book_screenshot"));

    public static final StreamCodec<ByteBuf, SaveLinkingPanelImageMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, SaveLinkingPanelImageMessage::image,
            UUIDUtil.STREAM_CODEC, SaveLinkingPanelImageMessage::uuid,
            SaveLinkingPanelImageMessage::new
    );

    public static void handle(final SaveLinkingPanelImageMessage data, final IPayloadContext context) {

        try {
            LinkingBooksSavedData worldData = context.player().getServer().overworld().getDataStorage()
                    .computeIfAbsent(LinkingBooksSavedData.factory(), Reference.MODID);
            worldData.addLinkingPanelImage(data.uuid(), data.image());
        } catch (NullPointerException exception) {
            LogUtils.getLogger().warn(exception.getMessage());
        }

    }

    @Override
    public @Nonnull CustomPacketPayload.Type<SaveLinkingPanelImageMessage> type() {
        return TYPE;
    }

}
