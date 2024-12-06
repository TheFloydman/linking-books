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

package thefloydman.linkingbooks.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.FileUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.locating.IModFile;
import org.slf4j.Logger;
import thefloydman.linkingbooks.Reference;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.linking.LinkingUtils;
import thefloydman.linkingbooks.world.generation.AgeUtils;
import thefloydman.linkingbooks.world.generation.LinkingBooksDimensionFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class ReltoCommand {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(Commands.literal("relto").requires(commandSourceStack -> {
            return commandSourceStack.hasPermission(2);
        }).executes(commandContext -> {
            if (!commandContext.getSource().isPlayer()) {
                LOGGER.info("Only players can use the /relto command.");
                return 0;
            }
            MinecraftServer server = commandContext.getSource().getServer();
            ServerPlayer player = commandContext.getSource().getPlayer();
            if (player == null) {
                return 0;
            }
            UUID uuid = player.getUUID();
            ResourceLocation ageResourceLocation = Reference.getAsResourceLocation(String.format("relto_%s", uuid));
            ResourceKey<Level> levelKey = ResourceKey.create(Registries.DIMENSION, ageResourceLocation);
            Component name = Component.translatable("age.linkingbooks.name.relto");
            AgeUtils.getOrCreateLevel(server, levelKey, name, uuid, LinkingBooksDimensionFactory::createRelto);
            doThing(ageResourceLocation);
            LinkData linkData = new LinkData(
                    ageResourceLocation,
                    new BlockPos(0, 103, 0),
                    commandContext.getSource().getPlayerOrException().getYRot(),
                    UUID.randomUUID(),
                    List.of()
            );
            return LinkingUtils.linkEntities(Lists.newArrayList(player), linkData, false);
        }));
    }

    private static void doThing(ResourceLocation ageResourceLocation) {
        Path reltoRegionPath = Reference.server.getWorldPath(new LevelResource("dimensions/linkingbooks/" + ageResourceLocation.getPath() + "/region"));
        try {
            FileUtil.createDirectoriesSafe(reltoRegionPath);
            IModFile modFile = ModList.get().getModFileById(Reference.MODID).getFile();
            Path examples = modFile.findResource("data/linkingbooks/linkingbooks/agetemplate/relto");
            Stream<Path> paths = Files.list(examples);
            List<Path> pathList = paths.filter(fromPath -> fromPath.toString().endsWith(".mca")).toList();
            for (Path fromPath : pathList) {
                Path toPath = reltoRegionPath.resolve(fromPath.getFileName().toString());
                Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            LOGGER.error("Could not prefill dimension {}", ageResourceLocation.getPath());
        }
    }

}