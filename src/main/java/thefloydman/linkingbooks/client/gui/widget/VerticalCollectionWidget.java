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
package thefloydman.linkingbooks.client.gui.widget;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.client.resources.guidebook.GuidebookImage;
import thefloydman.linkingbooks.client.resources.guidebook.GuidebookParagraph;
import thefloydman.linkingbooks.client.resources.guidebook.GuidebookRecipe;

@OnlyIn(Dist.CLIENT)
public class VerticalCollectionWidget extends NestedWidget {

    private Font font;
    private List<Object> preparedElements;
    private List<Object> rawElements;
    public long creationTime;
    public long changeTime = 2000L;

    public VerticalCollectionWidget(String id, int x, int y, float z, int width, int height, Component narration,
            Screen parentScreen, Float scale, Font font, List<Object> rawElements) {
        super(id, x, y, z, width, height, narration, parentScreen, scale);
        this.font = font;
        this.creationTime = System.currentTimeMillis();
        this.rawElements = rawElements;
        this.preparedElements = this.prepareElements(this.rawElements, Float.valueOf(this.width));
        int lineSpacing = 6;
        int currentY = (int) (this.y / this.scale);
        for (int i = 0; i < preparedElements.size(); i++) {
            Object object = preparedElements.get(i);
            if (object instanceof GuidebookParagraph) {
                GuidebookParagraph paragraph = (GuidebookParagraph) object;
                this.addChild(new ParagraphWidget(this.id + "paragraph" + i, this.x, currentY, z + 1, this.width,
                        paragraph.renderable.size() * lineSpacing, new TextComponent("Paragraph"), parentScreen, 0.5F,
                        paragraph.renderable, lineSpacing, font));
                currentY += paragraph.renderable.size() * lineSpacing + lineSpacing;
            } else if (object instanceof GuidebookImage) {
                GuidebookImage image = (GuidebookImage) object;
                float localScale = (float) this.width / (float) image.sourceWidth;
                float scaledHeight = image.sourceHeight * localScale;
                this.addChild(new ImageWidget(this.id + "image" + i, this.x, currentY, z + 1, this.width,
                        (int) scaledHeight, new TextComponent("Image"), parentScreen, 1.0F, image.resourceLocation,
                        image.sourceWidth, image.sourceHeight, 0, 0));
                currentY += scaledHeight + lineSpacing;
            } else if (object instanceof GuidebookRecipe) {
                GuidebookRecipe recipe = (GuidebookRecipe) object;
                int gridWidth = 107;
                int gridHeight = 62;
                this.addChild(new RecipeWidget(this.id + "recipe" + i,
                        (int) (this.x + ((this.width - (gridWidth * 0.5F)) / 2.0F)), currentY, z + 1, this.width,
                        gridHeight, new TextComponent("Recipe"), parentScreen, 0.5F, recipe.renderable));
                currentY += gridHeight * 0.5F + lineSpacing;
            }
        }
    }

    public List<Object> prepareElements(List<Object> source, float width) {
        List<Object> output = Lists.newArrayList();
        for (Object object : source) {
            if (object instanceof GuidebookParagraph) {
                GuidebookParagraph paragraph = (GuidebookParagraph) object;
                paragraph.makeRenderable(this.font, width / 0.5F);
                output.add(paragraph);
            } else if (object instanceof GuidebookImage) {
                output.add(object);
            } else if (object instanceof GuidebookRecipe) {
                GuidebookRecipe recipe = (GuidebookRecipe) object;
                recipe.makeRenderable();
                output.add(recipe);
            }
        }
        return output;
    }

    @Override
    public void restore(NestedWidget backup) {
        VerticalCollectionWidget old = VerticalCollectionWidget.class.cast(backup);
        if (old != null) {
            this.creationTime = old.creationTime;
        }
    }

}
