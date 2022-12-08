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
package thefloydman.linkingbooks.network.packets;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

public class SaveLinkingPanelImageMessage implements IMessage {

    private CompoundTag image = new CompoundTag();
    private UUID uuid;

    public SaveLinkingPanelImageMessage(CompoundTag image, UUID uuid) {
        this.image = image;
        this.uuid = uuid;
    }

    public SaveLinkingPanelImageMessage() {
        this(null, UUID.randomUUID());
    }

    @Override
    public FriendlyByteBuf toData(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.uuid);
        buffer.writeNbt(this.image);
        return buffer;
    }

    @Override
    public void fromData(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
        this.image = buffer.readNbt();
    }

    @Override
    public void handle(Context ctx) {

        LinkingBooksSavedData worldData = ctx.getSender().getServer().getLevel(Level.OVERWORLD).getDataStorage()
                .computeIfAbsent(LinkingBooksSavedData::load, LinkingBooksSavedData::new, Reference.MOD_ID);
        worldData.addLinkingPanelImage(this.uuid, this.image);
        ctx.setPacketHandled(true);

    }

}
