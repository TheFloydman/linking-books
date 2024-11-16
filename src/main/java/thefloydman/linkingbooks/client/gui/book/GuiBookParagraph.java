/*
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 */
package thefloydman.linkingbooks.client.gui.book;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.compress.utils.Lists;
import thefloydman.linkingbooks.client.gui.widget.ParagraphWidget;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GuiBookParagraph extends GuiBookElement<ParagraphWidget> {

    private final int lineSpacing = 6;
    private final List<String> contents;

    public GuiBookParagraph(List<String> contents) {
        super("paragraph");
        this.contents = contents;
    }

    @Override
    public ParagraphWidget getAsWidget(String id, int x, int y, float z, int width, int height, Screen parentScreen,
                                       float scale, Font font) {

        List<Component> outputParagraph = Lists.newArrayList();
        for (String span : this.contents) {
            List<String> wordList = Stream.of(span.split(" ")).collect(Collectors.toList());
            boolean excessWords = true;
            boolean styled = false;
            String style = "";
            String lastStyle = "";
            while (excessWords) {
                List<String> lineList = Lists.newArrayList();
                int index = 0;
                excessWords = false;
                String postWord = "";
                for (String currentLine = ""; index < wordList.size(); index++) {
                    String preWord = wordList.get(index);
                    String temp = preWord;
                    while (temp.indexOf("�") >= 0) {
                        int currentIndex = temp.indexOf("�");
                        String sub = temp.substring(currentIndex, currentIndex + 2);
                        styled = !sub.equals("�r");
                        if (styled) {
                            style += sub;
                        } else {
                            lastStyle = style;
                            style = "";
                        }
                        temp = currentIndex + 2 < temp.length() ? temp.substring(currentIndex + 2) : "";
                    }
                    postWord = (index == 0 && styled ? style : "") + preWord;
                    lineList.add(postWord);
                    currentLine = lineList.stream().collect(Collectors.joining(" "));
                    excessWords = font.width(currentLine) > (width / scale) && lineList.size() > 1;
                    if (excessWords) {
                        if (preWord.contains("�r"))
                            postWord = lastStyle + preWord;
                        wordList.set(index, postWord);
                        lineList.remove(lineList.size() - 1);
                        break;
                    }
                }
                outputParagraph.add(Component.literal(lineList.stream().collect(Collectors.joining(" "))));
                if (excessWords) {
                    wordList = wordList.subList(index, wordList.size());
                }
            }
        }

        return new ParagraphWidget(id, x, y, z, width, (int) (outputParagraph.size() * this.lineSpacing / scale),
                Component.literal("Paragraph"), parentScreen, scale, outputParagraph, this.lineSpacing, font);

    }

}
