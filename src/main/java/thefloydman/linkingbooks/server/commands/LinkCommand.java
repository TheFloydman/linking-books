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

package thefloydman.linkingbooks.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.LinkingUtils;
import thefloydman.linkingbooks.util.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                                    ResourceLocation dimension = context.getSource().getPlayerOrException().getCommandSenderWorld().dimension().location();
                                    BlockPos blockPos = BlockPosArgument.getSpawnablePos(context, POSITION);
                                    float rotation = context.getSource().getPlayerOrException().getYRot();
                                    List<ResourceLocation> linkEffects = List.of(Reference.getAsResourceLocation("intraage_linking"));
                                    LinkData linkData = new LinkData(dimension, blockPos, rotation, UUID.randomUUID(), linkEffects);
                                    return LinkingUtils.linkEntities(
                                            new ArrayList<>(EntityArgument.getEntities(context, ENTITIES)), linkData, false);
                                })))

                        .then(Commands.argument(ENTITIES, EntityArgument.entities())
                                .then(Commands.argument(DIMENSION, DimensionArgument.dimension())
                                        .then(Commands.argument(POSITION, BlockPosArgument.blockPos()).executes((context) -> {
                                            ResourceLocation dimension = DimensionArgument.getDimension(context, DIMENSION).dimension().location();
                                            BlockPos blockPos = BlockPosArgument.getSpawnablePos(context, POSITION);
                                            float rotation = context.getSource().getPlayerOrException().getYRot();
                                            List<ResourceLocation> linkEffects = List.of(Reference.getAsResourceLocation("intraage_linking"));
                                            LinkData linkData = new LinkData(dimension, blockPos, rotation, UUID.randomUUID(), linkEffects);
                                            return LinkingUtils.linkEntities(
                                                    new ArrayList<>(EntityArgument.getEntities(context, ENTITIES)), linkData,
                                                    false);
                                        }))))

                        .then(Commands.argument(DIMENSION, DimensionArgument.dimension())
                                .then(Commands.argument(POSITION, BlockPosArgument.blockPos()).executes((context) -> {
                                    ResourceLocation dimension = DimensionArgument.getDimension(context, DIMENSION).dimension().location();
                                    BlockPos blockPos = BlockPosArgument.getSpawnablePos(context, POSITION);
                                    float rotation = context.getSource().getPlayerOrException().getYRot();
                                    List<ResourceLocation> linkEffects = List.of(Reference.getAsResourceLocation("intraage_linking"));
                                    LinkData linkData = new LinkData(dimension, blockPos, rotation, UUID.randomUUID(), linkEffects);
                                    return LinkingUtils.linkEntities(
                                            new ArrayList<>(Lists.newArrayList(context.getSource().getPlayerOrException())),
                                            linkData, false);
                                })))

                        .then(Commands.argument(POSITION, BlockPosArgument.blockPos()).executes((context) -> {
                            ResourceLocation dimension = context.getSource().getPlayerOrException().getCommandSenderWorld().dimension().location();
                            BlockPos blockPos = BlockPosArgument.getSpawnablePos(context, POSITION);
                            float rotation = context.getSource().getPlayerOrException().getYRot();
                            List<ResourceLocation> linkEffects = List.of(Reference.getAsResourceLocation("intraage_linking"));
                            LinkData linkData = new LinkData(dimension, blockPos, rotation, UUID.randomUUID(), linkEffects);
                            return LinkingUtils.linkEntities(
                                    new ArrayList<>(Lists.newArrayList(context.getSource().getPlayerOrException())), linkData,
                                    false);
                        }))

                        .then(Commands.argument(ENTITIES, EntityArgument.entities())
                                .then(Commands.argument(ENTITY, EntityArgument.entity()).executes((context) -> {
                                    ResourceLocation dimension = EntityArgument.getEntity(context, ENTITY).getCommandSenderWorld()
                                            .dimension().location();
                                    BlockPos blockPos = EntityArgument.getEntity(context, ENTITY).blockPosition();
                                    List<ResourceLocation> linkEffects = List.of(Reference.getAsResourceLocation("intraage_linking"));
                                    int i = 0;
                                    for (Entity entity : EntityArgument.getEntities(context, ENTITIES)) {
                                        float rotation = entity.getYRot();
                                        LinkData linkData = new LinkData(dimension, blockPos, rotation, UUID.randomUUID(), linkEffects);
                                        i += LinkingUtils.linkEntities(new ArrayList<>(Lists.newArrayList(entity)), linkData,
                                                false);
                                    }
                                    return i;
                                })))

        );
    }
}