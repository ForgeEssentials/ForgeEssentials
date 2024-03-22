package com.forgeessentials.commands.world;

import java.util.List;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandRemove extends ForgeEssentialsCommandBuilder
{

    public CommandRemove(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "remove";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "clearGroundItems" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("radius", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                .then(Commands.argument("position", BlockPosArgument.blockPos())
                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                .executes(CommandContext -> execute(CommandContext, "dim")))
                        .executes(CommandContext -> execute(CommandContext, "blank"))));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        int radius = 10;
        double centerX;
        double centerY;
        double centerZ;

        radius = IntegerArgumentType.getInteger(ctx, "radius");
        centerX = BlockPosArgument.getLoadedBlockPos(ctx, "position").getX();
        centerY = BlockPosArgument.getLoadedBlockPos(ctx, "position").getY();
        centerZ = BlockPosArgument.getLoadedBlockPos(ctx, "position").getZ();

        List<ItemEntity> entityList = getServerPlayer(ctx.getSource()).getLevel().getEntitiesOfClass(ItemEntity.class,
                new AABB(centerX - radius, centerY - radius, centerZ - radius, centerX + radius + 1,
                        centerY + radius + 1, centerZ + radius + 1));

        int counter = 0;
        for (ItemEntity entity : entityList) {
            counter += entity.getItem().getCount();
            entity.kill();
        }
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("%d items removed.", counter));
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        int radius = 0;
        WorldPoint center = new WorldPoint("minecraft:overworld", 0, 0, 0);

        radius = IntegerArgumentType.getInteger(ctx, "radius");
        center.setX(BlockPosArgument.getLoadedBlockPos(ctx, "position").getX());
        center.setY(BlockPosArgument.getLoadedBlockPos(ctx, "position").getY());
        center.setZ(BlockPosArgument.getLoadedBlockPos(ctx, "position").getZ());

        if (params.equals("dim"))
        {
            center.setDimension(DimensionArgument.getDimension(ctx, "dimension").dimension().location().toString());
        }

        List<ItemEntity> entityList = ServerUtil.getWorldFromString(center.getDimension()).getEntitiesOfClass(
                ItemEntity.class,
                new AABB(center.getX() - radius, center.getY() - radius, center.getZ() - radius,
                        center.getX() + radius + 1, center.getY() + radius + 1, center.getZ() + radius + 1));

        int counter = 0;
        for (ItemEntity entity : entityList) {
            counter += entity.getItem().getCount();
            entity.kill();
        }
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("%d items removed.", counter));
        return Command.SINGLE_SUCCESS;
    }

}
