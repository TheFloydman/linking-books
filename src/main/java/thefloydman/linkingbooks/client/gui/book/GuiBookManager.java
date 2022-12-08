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
package thefloydman.linkingbooks.client.gui.book;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.util.Reference;

@OnlyIn(Dist.CLIENT)
public class GuiBookManager implements ResourceManagerReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static Map<Integer, List<Object>> guidebookPages = Maps.newHashMap();
    private static final String TAG_RESET = "§r";
    private static Map<String, String> formatMap = Maps.newHashMap();
    private static Map<String, String> colorMap = Maps.newHashMap();

    static {
        colorMap.put("black", "0");
        colorMap.put("dark_blue", "1");
        colorMap.put("dark_green", "2");
        colorMap.put("dark_aqua", "3");
        colorMap.put("dark_red", "4");
        colorMap.put("dark_purple", "5");
        colorMap.put("gold", "6");
        colorMap.put("gray", "7");
        colorMap.put("dark_gray", "8");
        colorMap.put("blue", "9");
        colorMap.put("green", "a");
        colorMap.put("aqua", "b");
        colorMap.put("red", "c");
        colorMap.put("light_purple", "d");
        colorMap.put("yellow", "e");
        colorMap.put("white", "f");
        formatMap.put("obfuscated", "k");
        formatMap.put("bold", "l");
        formatMap.put("strikethrough", "m");
        formatMap.put("underline", "n");
        formatMap.put("italic", "o");
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        guidebookPages = Maps.newHashMap();
        String currentLang = Minecraft.getInstance().getLanguageManager().getSelected().getCode();
        Collection<ResourceLocation> guidebookResources = resourceManager
                .listResources("lang/linkingbooks/guidebook", (resourceLocation) -> {
                    return resourceLocation.getPath().endsWith(String.format("%s.xml", currentLang));
                }).keySet();
        for (ResourceLocation resourceLocation : guidebookResources) {
            Document document = null;
            try {
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = factory.newSchema(new StreamSource(resourceManager
                        .getResource(Reference.getAsResourceLocation("lang/linkingbooks/guidebook/schema.xsd")).get()
                        .open()));
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                builderFactory.setSchema(schema);
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                document = builder.parse(resourceManager.open(resourceLocation));
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (document != null) {
                NodeList pageNodes = document.getDocumentElement().getChildNodes();
                for (int i = 0; i < pageNodes.getLength(); i++) {
                    Node pageNode = pageNodes.item(i);
                    if (pageNode.getNodeType() == Node.ELEMENT_NODE && pageNode.getNodeName().equals("page")) {
                        List<Object> elementList = Lists.newArrayList();
                        NodeList elementNodes = pageNode.getChildNodes();
                        for (int j = 0; j < elementNodes.getLength(); j++) {
                            Node elementNode = elementNodes.item(j);
                            if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
                                if (elementNode.getNodeName().equals("p")) {
                                    List<String> brokenLines = Lists.newArrayList();
                                    NodeList paragraphChildNodes = elementNode.getChildNodes();
                                    for (int k = 0; k < paragraphChildNodes.getLength(); k++) {
                                        Node paragraphChildNode = paragraphChildNodes.item(k);
                                        if (paragraphChildNode.getNodeName().equals("style")) {
                                            String colorCode = "";
                                            String formatCode = "";
                                            if (paragraphChildNode.hasAttributes()) {
                                                NamedNodeMap styleNodeMap = paragraphChildNode.getAttributes();
                                                for (int l = 0; l < styleNodeMap.getLength(); l++) {
                                                    Node attribute = styleNodeMap.item(l);
                                                    if (attribute.getNodeName() != null
                                                            && attribute.getNodeValue() != null) {
                                                        if (attribute.getNodeName().equals("color")) {
                                                            String color = colorMap.get(attribute.getNodeValue());
                                                            colorCode = color == null ? "" : "§" + color;
                                                        } else {
                                                            if (attribute.getNodeValue().equals("true")) {
                                                                String format = formatMap.get(attribute.getNodeName());
                                                                formatCode += format == null ? "" : "§" + format;
                                                            }
                                                        }
                                                    }
                                                }
                                                paragraphChildNode.setTextContent(colorCode + formatCode
                                                        + paragraphChildNode.getTextContent() + TAG_RESET);
                                            }
                                        } else if (paragraphChildNode.getNodeName().equals("br")) {
                                            paragraphChildNode.setTextContent("/u000a");
                                        }
                                    }
                                    brokenLines = Stream.of(elementNode.getTextContent().split("/u000a")).toList();
                                    GuiBookParagraph paragraph = new GuiBookParagraph(brokenLines);
                                    elementList.add(paragraph);
                                } else if (elementNode.getNodeName().equals("img")) {
                                    NamedNodeMap imageNodeMap = elementNode.getAttributes();
                                    ResourceLocation location = new ResourceLocation(
                                            imageNodeMap.getNamedItem("src").getNodeValue());
                                    float scale = imageNodeMap.getNamedItem("scale") == null ? 1.0F
                                            : Float.valueOf(imageNodeMap.getNamedItem("scale").getNodeValue());
                                    int width = imageNodeMap.getNamedItem("width") == null ? 256
                                            : Integer.valueOf(imageNodeMap.getNamedItem("width").getNodeValue());
                                    int height = imageNodeMap.getNamedItem("height") == null ? 256
                                            : Integer.valueOf(imageNodeMap.getNamedItem("height").getNodeValue());
                                    if (location != null) {
                                        elementList.add(new GuiBookImage(location, scale, width, height));
                                    }
                                } else if (elementNode.getNodeName().equals("recipe")) {
                                    if (elementNode.hasAttributes()) {
                                        NamedNodeMap attributes = elementNode.getAttributes();
                                        String source = attributes.getNamedItem("src") == null ? ""
                                                : attributes.getNamedItem("src").getNodeValue();
                                        elementList.add(new GuiBookRecipe(RecipeType.CRAFTING,
                                                ResourceLocation.tryParse(source)));
                                    }
                                } else if (elementNode.getNodeName().equals("recipes")) {
                                    if (elementNode.hasChildNodes()) {
                                        NodeList recipeNodeList = elementNode.getChildNodes();
                                        List<ResourceLocation> recipeLocationList = Lists.newArrayList();
                                        for (int k = 0; k < recipeNodeList.getLength(); k++) {
                                            Node recipeNode = recipeNodeList.item(k);
                                            if (recipeNode.getNodeType() == Node.ELEMENT_NODE
                                                    && recipeNode.getNodeName().equals("rec")) {
                                                if (recipeNode.hasAttributes()) {
                                                    NamedNodeMap attributes = recipeNode.getAttributes();
                                                    String source = attributes.getNamedItem("src") == null ? ""
                                                            : attributes.getNamedItem("src").getNodeValue();
                                                    recipeLocationList.add(ResourceLocation.tryParse(source));
                                                }
                                            }
                                        }
                                        elementList.add(
                                                new GuiBookRecipeCarousel(RecipeType.CRAFTING, recipeLocationList));
                                    }
                                }
                            }
                        }
                        guidebookPages.put(guidebookPages.size(), elementList);
                    }
                }
            }
        }
        LOGGER.info(
                String.format("Successfully imported %d guidebook pages for Linking Books.", guidebookPages.size()));
    }

    public static Map<Integer, List<Object>> getPages() {
        return guidebookPages;
    }
}