package thefloydman.linkingbooks.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public interface IMessage {

    public PacketBuffer toData(PacketBuffer buffer);

    public void fromData(PacketBuffer buffer);

    public void handle(Context ctx);

}
