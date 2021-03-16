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
package thefloydman.linkingbooks.network.packets;

import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

public class SaveLinkingPanelImageMessage implements IMessage {

    private CompoundNBT image = new CompoundNBT();
    private UUID uuid;

    public SaveLinkingPanelImageMessage(CompoundNBT image, UUID uuid) {
        this.image = image;
        this.uuid = uuid;
    }

    public SaveLinkingPanelImageMessage() {
        this(null, UUID.randomUUID());
    }

    @Override
    public PacketBuffer toData(PacketBuffer buffer) {
        buffer.writeUUID(this.uuid);
        buffer.writeNbt(this.image);
        return buffer;
    }

    @Override
    public void fromData(PacketBuffer buffer) {
        this.uuid = buffer.readUUID();
        this.image = buffer.readNbt();
    }

    @Override
    public void handle(Context ctx) {
        ctx.enqueueWork(() -> {
            LinkingBooksSavedData worldData = ctx.getSender().getServer().getLevel(World.OVERWORLD).getDataStorage()
                    .computeIfAbsent(LinkingBooksSavedData::new, Reference.MOD_ID);
            worldData.addLinkingPanelImage(this.uuid, this.image);
            ctx.setPacketHandled(true);
        });
    }

}
