package thefloydman.linkingbooks.network.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.util.LinkingUtils;

public class LinkMessage implements IMessage {

    public boolean holdingBook = false;
    public ILinkData linkData = LinkData.LINK_DATA.getDefaultInstance();

    public LinkMessage(boolean holdingBook, ILinkData linkData) {
        this.holdingBook = holdingBook;
        this.linkData = linkData;
    }

    public LinkMessage() {
        this(false, LinkData.LINK_DATA.getDefaultInstance());
    }

    @Override
    public PacketBuffer toData(PacketBuffer buffer) {
        buffer.writeBoolean(this.holdingBook);
        this.linkData.write(buffer);
        return buffer;
    }

    @Override
    public void fromData(PacketBuffer buffer) {
        this.holdingBook = buffer.readBoolean();
        this.linkData.read(buffer);
    }

    @Override
    public void handle(Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayerEntity player = ctx.getSender();
            LinkingUtils.linkEntity(player, this.linkData, this.holdingBook);
        });
    }

}
