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

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.util.LinkingUtils;

public class LinkMessage implements IMessage {

    public boolean holdingBook = false;
    public ILinkData linkData = new LinkData();

    public LinkMessage(boolean holdingBook, ILinkData linkData) {
        this.holdingBook = holdingBook;
        this.linkData = linkData;
    }

    public LinkMessage() {
        this(false, new LinkData());
    }

    @Override
    public FriendlyByteBuf toData(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.holdingBook);
        this.linkData.write(buffer);
        return buffer;
    }

    @Override
    public void fromData(FriendlyByteBuf buffer) {
        this.holdingBook = buffer.readBoolean();
        this.linkData.read(buffer);
    }

    @Override
    public void handle(Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            LinkingUtils.linkEntity(player, this.linkData, this.holdingBook);
            ctx.setPacketHandled(true);
        });
    }

}
