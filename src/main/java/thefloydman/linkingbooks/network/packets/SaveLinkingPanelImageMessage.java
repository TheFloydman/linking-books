package thefloydman.linkingbooks.network.packets;

import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.world.storage.LinkingBooksGlobalSavedData;

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
        buffer.writeUniqueId(this.uuid);
        buffer.writeCompoundTag(this.image);
        return buffer;
    }

    @Override
    public void fromData(PacketBuffer buffer) {
        this.uuid = buffer.readUniqueId();
        this.image = buffer.readCompoundTag();
    }

    @Override
    public void handle(Context ctx) {
        ctx.enqueueWork(() -> {
            LinkingBooksGlobalSavedData worldData = ctx.getSender().getServer().getWorld(World.OVERWORLD).getSavedData()
                    .getOrCreate(LinkingBooksGlobalSavedData::new, Reference.MOD_ID);
            worldData.addLinkingPanelImage(this.uuid, this.image);
            ctx.setPacketHandled(true);
        });
    }

}
