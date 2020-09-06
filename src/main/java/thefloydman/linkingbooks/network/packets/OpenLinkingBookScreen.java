package thefloydman.linkingbooks.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import thefloydman.linkingbooks.client.gui.screen.LinkingBookScreen;

public class OpenLinkingBookScreen implements IMessage {

    @Override
    public PacketBuffer toData(PacketBuffer buffer) {
        return null;
    }

    @Override
    public void fromData(PacketBuffer buffer) {
    }

    @Override
    public void handle(Context ctx) {
        Minecraft.getInstance().deferTask(() -> {
            Minecraft.getInstance().displayGuiScreen(new LinkingBookScreen(new StringTextComponent("Linking Book")));
        });
    }

}
