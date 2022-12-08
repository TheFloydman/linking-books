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

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import thefloydman.linkingbooks.network.ModNetworkHandler;
import thefloydman.linkingbooks.util.ImageUtils;

public class TakeScreenshotForLinkingBookMessage implements IMessage {

    private UUID uuid;

    public TakeScreenshotForLinkingBookMessage(UUID uuid) {
        this.uuid = uuid;
    }

    public TakeScreenshotForLinkingBookMessage() {
        this(UUID.randomUUID());
    }

    @Override
    public FriendlyByteBuf toData(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.uuid);
        return buffer;
    }

    @Override
    public void fromData(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
    }

    @Override
    public void handle(Context ctx) {
        ctx.enqueueWork(() -> {

            if (RenderSystem.isOnRenderThread()) {
                this.getScreenshot();
            } else {
                RenderSystem.recordRenderCall(() -> {
                    this.getScreenshot();
                });
            }

            ctx.setPacketHandled(true);
        });
    }

    private void getScreenshot() {

        Minecraft mc = Minecraft.getInstance();
        RenderTarget buffer = mc.getMainRenderTarget();
        float largeWidth = buffer.viewWidth;
        float largeHeight = buffer.viewHeight;
        float smallWidth = 64.0F;
        float smallHeight = 42.0F;
        if (largeWidth / largeHeight > smallWidth / smallHeight) {
            while (largeHeight % 42 != 0) {
                largeHeight--;
            }
            while (largeWidth / largeHeight != smallWidth / smallHeight) {
                largeWidth--;
            }
        } else if (largeWidth / largeHeight < smallWidth / smallHeight) {
            while (largeWidth % 64 != 0) {
                largeWidth--;
            }
            while (largeWidth / largeHeight != smallWidth / smallHeight) {
                largeHeight--;
            }
        }

        NativeImage fullImage = new NativeImage(buffer.width, buffer.height, false);
        mc.getMainRenderTarget().bindWrite(true);
        boolean hide = mc.options.hideGui;
        mc.options.hideGui = true;
        mc.gameRenderer.renderLevel(mc.getFrameTime(), Util.getNanos(), new PoseStack());
        mc.options.hideGui = hide;
        buffer.bindRead();
        fullImage.downloadTexture(0, true);
        mc.getMainRenderTarget().unbindWrite();
        fullImage.flipY();
        NativeImage largeImage = new NativeImage((int) largeWidth, (int) largeHeight, false);
        int initialX = (int) ((buffer.viewWidth - largeWidth) / 2);
        int initialY = (int) ((buffer.viewHeight - largeHeight) / 2);
        for (int largeY = 0, fullY = initialY; largeY < largeHeight; largeY++, fullY++) {
            for (int largeX = 0, fullX = initialX; largeX < largeWidth; largeX++, fullX++) {
                largeImage.setPixelRGBA(largeX, largeY, fullImage.getPixelRGBA(fullX, fullY));
            }
        }
        NativeImage smallImage = new NativeImage((int) smallWidth, (int) smallHeight, false);
        largeImage.resizeSubRectTo(0, 0, (int) largeWidth, (int) largeHeight, smallImage);
        largeImage.close();
        fullImage.close();

        ModNetworkHandler.sendToServer(new SaveLinkingPanelImageMessage(ImageUtils.imageToNBT(smallImage), this.uuid));
    }

}
