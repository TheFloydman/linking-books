package thefloydman.linkingbooks.command;

import java.util.ArrayList;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityArgument;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.linking.LinkEffects;
import thefloydman.linkingbooks.util.LinkingUtils;

public class LinkCommand {

    private static final String ENTITIES = "entities";
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

        );
    }
}
