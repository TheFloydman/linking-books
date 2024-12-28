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
package thefloydman.linkingbooks.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.Reference;
import thefloydman.linkingbooks.client.gui.widget.NestedWidget;
import thefloydman.linkingbooks.client.gui.widget.ReltoBookWidget;
import thefloydman.linkingbooks.client.sound.ModSounds;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.menutype.ReltoBookMenuType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class ReltoBookScreen extends AbstractContainerScreen<ReltoBookMenuType> {

    public ReltoBookScreen(ReltoBookMenuType container, Inventory inventory, Component narration) {
        super(container, inventory, narration);
        this.imageWidth = 192;
        this.imageHeight = 180;
    }

    @Override
    protected void init() {
        super.init();
        LinkData linkData = new LinkData(Reference.getAsResourceLocation(String.format("relto_%s", this.getMenu().owner)), new BlockPos(-11, 200, 23), -180.0F, UUID.randomUUID(), List.of(Reference.getAsResourceLocation("intraage_linking")));
        String ownerUsername = Reference.PLAYER_DISPLAY_NAMES.get(this.getMenu().owner);
        Component ageName = ownerUsername == null ? Component.translatable("age.linkingbooks.name.relto") : Component.translatable("age.linkingbooks.name.relto", ownerUsername);
        NestedWidget linkingBook = this.addRenderableWidget(
                new ReltoBookWidget(
                        "relto book",
                        this.leftPos, this.topPos, 100.0F,
                        this.imageWidth, this.imageHeight,
                        Component.literal("Relto Book"),
                        this,
                        1.0F,
                        linkData,
                        ageName
                )
        );
        linkingBook.addListener(this);
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.playSound(ModSounds.BOOK_OPEN.get(), 0.5F, 1.0F);
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.playSound(ModSounds.BOOK_CLOSE.get());
        }
        super.onClose();
    }


    @Override
    protected void renderLabels(@Nonnull GuiGraphics guiGraphics, int p_97809_, int p_97810_) {
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float p_97788_, int p_97789_, int p_97790_) {
    }

}
