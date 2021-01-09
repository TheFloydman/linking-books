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
package thefloydman.linkingbooks.command;

import java.util.ArrayList;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.linking.LinkEffects;
import thefloydman.linkingbooks.util.LinkingUtils;

public class LinkCommand {

    private static final String ENTITIES = "teleporting_entities";
    private static final String ENTITY = "destination_entity";
    private static final String POSITION = "position";
    private static final String DIMENSION = "dimension";

    public static void register(CommandDispatcher<CommandSource> dispatcher) {

        dispatcher.register(Commands.literal("link").requires((context) -> {
            return context.hasPermissionLevel(2);
        })

                .then(Commands.argument(ENTITIES, EntityArgument.entities())
                        .then(Commands.argument(POSITION, BlockPosArgument.blockPos()).executes((context) -> {
                            ILinkData linkData = LinkData.LINK_DATA.getDefaultInstance();
                            linkData.setDimension(
                                    context.getSource().asPlayer().getEntityWorld().getDimensionKey().getLocation());
                            linkData.setPosition(BlockPosArgument.getBlockPos(context, POSITION));
                            linkData.setRotation(context.getSource().asPlayer().rotationYaw);
                            linkData.addLinkEffect(LinkEffects.INTRAAGE_LINKING.get());
                            return LinkingUtils.linkEntities(
                                    new ArrayList<>(EntityArgument.getEntities(context, ENTITIES)), linkData, false);
                        })))

                .then(Commands.argument(ENTITIES, EntityArgument.entities())
                        .then(Commands.argument(DIMENSION, DimensionArgument.getDimension())
                                .then(Commands.argument(POSITION, BlockPosArgument.blockPos()).executes((context) -> {
                                    ILinkData linkData = LinkData.LINK_DATA.getDefaultInstance();
                                    linkData.setDimension(DimensionArgument.getDimensionArgument(context, DIMENSION)
                                            .getDimensionKey().getLocation());
                                    linkData.setPosition(BlockPosArgument.getBlockPos(context, POSITION));
                                    linkData.setRotation(context.getSource().asPlayer().rotationYaw);
                                    linkData.addLinkEffect(LinkEffects.INTRAAGE_LINKING.get());
                                    return LinkingUtils.linkEntities(
                                            new ArrayList<>(EntityArgument.getEntities(context, ENTITIES)), linkData,
                                            false);
                                }))))

                .then(Commands.argument(DIMENSION, DimensionArgument.getDimension())
                        .then(Commands.argument(POSITION, BlockPosArgument.blockPos()).executes((context) -> {
                            ILinkData linkData = LinkData.LINK_DATA.getDefaultInstance();
                            linkData.setDimension(DimensionArgument.getDimensionArgument(context, DIMENSION)
                                    .getDimensionKey().getLocation());
                            linkData.setPosition(BlockPosArgument.getBlockPos(context, POSITION));
                            linkData.setRotation(context.getSource().asPlayer().rotationYaw);
                            linkData.addLinkEffect(LinkEffects.INTRAAGE_LINKING.get());
                            return LinkingUtils.linkEntities(
                                    new ArrayList<>(Lists.newArrayList(context.getSource().asPlayer())), linkData,
                                    false);
                        })))

                .then(Commands.argument(POSITION, BlockPosArgument.blockPos()).executes((context) -> {
                    ILinkData linkData = LinkData.LINK_DATA.getDefaultInstance();
                    linkData.setDimension(
                            context.getSource().asPlayer().getEntityWorld().getDimensionKey().getLocation());
                    linkData.setPosition(BlockPosArgument.getBlockPos(context, POSITION));
                    linkData.setRotation(context.getSource().asPlayer().rotationYaw);
                    linkData.addLinkEffect(LinkEffects.INTRAAGE_LINKING.get());
                    return LinkingUtils.linkEntities(
                            new ArrayList<>(Lists.newArrayList(context.getSource().asPlayer())), linkData, false);
                }))

                .then(Commands.argument(ENTITIES, EntityArgument.entities())
                        .then(Commands.argument(ENTITY, EntityArgument.entity()).executes((context) -> {
                            ILinkData linkData = LinkData.LINK_DATA.getDefaultInstance();
                            linkData.setDimension(EntityArgument.getEntity(context, ENTITY).getEntityWorld()
                                    .getDimensionKey().getLocation());
                            linkData.setPosition(EntityArgument.getEntity(context, ENTITY).getPosition());
                            linkData.addLinkEffect(LinkEffects.INTRAAGE_LINKING.get());
                            int i = 0;
                            for (Entity entity : EntityArgument.getEntities(context, ENTITIES)) {
                                linkData.setRotation(entity.rotationYaw);
                                i += LinkingUtils.linkEntities(new ArrayList<>(Lists.newArrayList(entity)), linkData,
                                        false);
                            }
                            return i;
                        })))

        );
    }
}
