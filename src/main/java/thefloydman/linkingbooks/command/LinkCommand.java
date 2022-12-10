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
package thefloydman.linkingbooks.command;

import java.util.ArrayList;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.util.LinkingUtils;

public class LinkCommand {

    private static final String ENTITIES = "teleporting_entities";
    private static final String ENTITY = "destination_entity";
    private static final String POSITION = "position";
    private static final String DIMENSION = "dimension";

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {

        commandDispatcher.register(Commands.literal("link").requires((context) -> {
            return context.hasPermission(2);
        })

                .then(Commands.argument(ENTITIES, EntityArgument.entities())
                        .then(Commands.argument(POSITION, BlockPosArgument.blockPos()).executes((context) -> {
                            ILinkData linkData = new LinkData();
                            linkData.setDimension(context.getSource().getPlayerOrException().getCommandSenderWorld()
                                    .dimension().location());
                            linkData.setPosition(BlockPosArgument.getSpawnablePos(context, POSITION));
                            linkData.setRotation(context.getSource().getPlayerOrException().getYRot());
                            linkData.addLinkEffect(new ResourceLocation("linkingbooks:intraage_linking"));
                            return LinkingUtils.linkEntities(
                                    new ArrayList<>(EntityArgument.getEntities(context, ENTITIES)), linkData, false);
                        })))

                .then(Commands.argument(ENTITIES, EntityArgument.entities())
                        .then(Commands.argument(DIMENSION, DimensionArgument.dimension())
                                .then(Commands.argument(POSITION, BlockPosArgument.blockPos()).executes((context) -> {
                                    ILinkData linkData = new LinkData();
                                    linkData.setDimension(
                                            DimensionArgument.getDimension(context, DIMENSION).dimension().location());
                                    linkData.setPosition(BlockPosArgument.getSpawnablePos(context, POSITION));
                                    linkData.setRotation(context.getSource().getPlayerOrException().getYRot());
                                    linkData.addLinkEffect(new ResourceLocation("linkingbooks:intraage_linking"));
                                    return LinkingUtils.linkEntities(
                                            new ArrayList<>(EntityArgument.getEntities(context, ENTITIES)), linkData,
                                            false);
                                }))))

                .then(Commands.argument(DIMENSION, DimensionArgument.dimension())
                        .then(Commands.argument(POSITION, BlockPosArgument.blockPos()).executes((context) -> {
                            ILinkData linkData = new LinkData();
                            linkData.setDimension(
                                    DimensionArgument.getDimension(context, DIMENSION).dimension().location());
                            linkData.setPosition(BlockPosArgument.getSpawnablePos(context, POSITION));
                            linkData.setRotation(context.getSource().getPlayerOrException().getYRot());
                            linkData.addLinkEffect(new ResourceLocation("linkingbooks:intraage_linking"));
                            return LinkingUtils.linkEntities(
                                    new ArrayList<>(Lists.newArrayList(context.getSource().getPlayerOrException())),
                                    linkData, false);
                        })))

                .then(Commands.argument(POSITION, BlockPosArgument.blockPos()).executes((context) -> {
                    ILinkData linkData = new LinkData();
                    linkData.setDimension(
                            context.getSource().getPlayerOrException().getCommandSenderWorld().dimension().location());
                    linkData.setPosition(BlockPosArgument.getSpawnablePos(context, POSITION));
                    linkData.setRotation(context.getSource().getPlayerOrException().getYRot());
                    linkData.addLinkEffect(new ResourceLocation("linkingbooks:intraage_linking"));
                    return LinkingUtils.linkEntities(
                            new ArrayList<>(Lists.newArrayList(context.getSource().getPlayerOrException())), linkData,
                            false);
                }))

                .then(Commands.argument(ENTITIES, EntityArgument.entities())
                        .then(Commands.argument(ENTITY, EntityArgument.entity()).executes((context) -> {
                            ILinkData linkData = new LinkData();
                            linkData.setDimension(EntityArgument.getEntity(context, ENTITY).getCommandSenderWorld()
                                    .dimension().location());
                            linkData.setPosition(EntityArgument.getEntity(context, ENTITY).blockPosition());
                            linkData.addLinkEffect(new ResourceLocation("linkingbooks:intraage_linking"));
                            int i = 0;
                            for (Entity entity : EntityArgument.getEntities(context, ENTITIES)) {
                                linkData.setRotation(entity.getYRot());
                                i += LinkingUtils.linkEntities(new ArrayList<>(Lists.newArrayList(entity)), linkData,
                                        false);
                            }
                            return i;
                        })))

        );
    }
}
