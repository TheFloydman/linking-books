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
package thefloydman.linkingbooks.client.resources.guidebook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.util.Reference;

@OnlyIn(Dist.CLIENT)
public class GuidebookManager implements ResourceManagerReloadListener {

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
        Collection<ResourceLocation> guidebookResources = resourceManager.listResources("lang/linkingbooks/guidebook",
                (resourceLocation) -> {
                    return resourceLocation.endsWith(String.format("%s.xml", currentLang));
                });
        for (ResourceLocation resourceLocation : guidebookResources) {
            Document document = null;
            try {
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = factory.newSchema(new StreamSource(resourceManager
                        .getResource(Reference.getAsResourceLocation("lang/linkingbooks/guidebook/schema.xsd"))
                        .getInputStream()));
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                builderFactory.setSchema(schema);
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                document = builder.parse(resourceManager.getResource(resourceLocation).getInputStream());
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (document != null) {
                NodeList pageNodes = document.getFirstChild().getChildNodes();
                for (int i = 0; i < pageNodes.getLength(); i++) {
                    if (!pageNodes.item(i).getNodeName().equals("page"))
                        continue;
                    List<Object> elementList = Lists.newArrayList();
                    NodeList elementNodes = pageNodes.item(i).getChildNodes();
                    for (int j = 0; j < elementNodes.getLength(); j++) {
                        Node elementNode = elementNodes.item(j);
                        if (elementNode.getNodeName().equals("p")) {
                            NodeList styleNodes = elementNode.getChildNodes();
                            for (int k = 0; k < styleNodes.getLength(); k++) {
                                Node styleNode = styleNodes.item(k);
                                String colorCode = "";
                                String formatCode = "";
                                if (styleNode.hasAttributes()) {
                                    NamedNodeMap styleNodeMap = styleNode.getAttributes();
                                    for (int l = 0; l < styleNodeMap.getLength(); l++) {
                                        Node attribute = styleNodeMap.item(l);
                                        if (attribute.getNodeName() != null && attribute.getNodeValue() != null) {
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
                                    styleNode.setTextContent(
                                            colorCode + formatCode + styleNode.getTextContent() + TAG_RESET);
                                }
                            }
                            GuidebookParagraph paragraph = new GuidebookParagraph(
                                    elementNodes.item(j).getTextContent());
                            elementList.add(paragraph);
                        } else if (elementNode.getNodeName().equals("img")) {
                            NamedNodeMap imageNodeMap = elementNode.getAttributes();
                            ResourceLocation location = null;
                            float scale = 1.0F;
                            int width = 256;
                            int height = 256;
                            for (int k = 0; k < imageNodeMap.getLength(); k++) {
                                Node attribute = imageNodeMap.item(k);
                                if (attribute.getNodeName().equals("src")) {
                                    location = new ResourceLocation(attribute.getNodeValue());
                                } else if (attribute.getNodeName().equals("scale")) {
                                    scale = Float.valueOf(attribute.getNodeValue());
                                } else if (attribute.getNodeName().equals("width")) {
                                    width = Integer.valueOf(attribute.getNodeValue());
                                } else if (attribute.getNodeName().equals("height")) {
                                    height = Integer.valueOf(attribute.getNodeValue());
                                }
                            }
                            if (location != null) {
                                elementList.add(new GuidebookImage(location, scale, width, height));
                            }
                        } else if (elementNode.getNodeName().equals("recipe")) {
                            List<GuidebookRecipe.Ingredient> recipeIngredients = Lists.newArrayList();
                            for (int k = 0; k < 10; k++) {
                                recipeIngredients.add(GuidebookRecipe.emptyIngredient());
                            }
                            NodeList ingredientsNodeList = elementNode.getChildNodes();
                            for (int k = 0, index = 0; k < ingredientsNodeList.getLength(); k++) {
                                Node ingredientNode = ingredientsNodeList.item(k);
                                if (ingredientNode.hasAttributes()) {
                                    GuidebookRecipe.Ingredient ingredient = GuidebookRecipe.emptyIngredient();
                                    String item = "";
                                    String tag = "";
                                    int quantity = 1;
                                    NamedNodeMap attributes = ingredientNode.getAttributes();
                                    for (int l = 0; l < attributes.getLength(); l++) {
                                        Node attribute = attributes.item(l);
                                        if (attribute.getNodeName().equals("item")) {
                                            item = attribute.getNodeValue();
                                        } else if (attribute.getNodeName().equals("tag")) {
                                            tag = attribute.getNodeValue();
                                        } else if (attribute.getNodeName().equals("quantity")) {
                                            quantity = Integer.valueOf(attribute.getNodeValue());
                                        }
                                    }
                                    if (!item.strip().isEmpty()) {
                                        ingredient = new GuidebookRecipe.Ingredient(
                                                GuidebookRecipe.Ingredient.IngredientType.ITEM,
                                                new ResourceLocation(item), quantity);
                                    } else if (!tag.strip().isEmpty()) {
                                        ingredient = new GuidebookRecipe.Ingredient(
                                                GuidebookRecipe.Ingredient.IngredientType.TAG,
                                                new ResourceLocation(tag), quantity);
                                    }
                                    recipeIngredients.set(index, ingredient);
                                    index++;
                                } else if (ingredientNode.getNodeName().equals("ingredient")) {
                                    index++;
                                }
                            }
                            elementList
                                    .add(new GuidebookRecipe(GuidebookRecipe.RecipeType.CRAFTING, recipeIngredients));
                        }
                    }
                    guidebookPages.put(guidebookPages.size(), elementList);
                }
            }
        }
        LOGGER.info(
                String.format("Successfully imported %d guidebook pages for Linking Books.", guidebookPages.size()));
    }

    public static Map<Integer, List<Object>> getPages() {
        return guidebookPages;
    }

    public static String trim(String input) {
        BufferedReader reader = new BufferedReader(new StringReader(input));
        StringBuffer result = new StringBuffer();
        try {
            String line;
            while ((line = reader.readLine()) != null)
                result.append(line.trim());
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}