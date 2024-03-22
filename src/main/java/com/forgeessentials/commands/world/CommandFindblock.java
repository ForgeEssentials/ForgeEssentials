package com.forgeessentials.commands.world;

import com.forgeessentials.commands.util.TickTaskBlockFinder;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandFindblock extends ForgeEssentialsCommandBuilder
{

    public CommandFindblock(boolean enabled)
    {
        super(enabled);
    }

    public static final int defaultCount = 1;
    public static int defaultRange = 20 * 16;
    public static int defaultSpeed = 16 * 16;

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "findblock";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "fb" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("block", BlockStateArgument.block())
                .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        BlockState id = BlockStateArgument.getBlock(ctx, "block").getState();
        // int range = (args.length < 3) ? defaultRange : parseInt(args[2], 1,
        // Integer.MAX_VALUE);
        // int amount = (args.length < 4) ? defaultCount : parseInt(args[3], 1,
        // Integer.MAX_VALUE);
        // int speed = (args.length < 5) ? defaultSpeed : parseInt(args[4], 1,
        // Integer.MAX_VALUE);
        // TODO add custom ranges
        // new TickTaskBlockFinder(getServerPlayer(ctx.getSource()), id, range, amount,
        // speed);
        new TickTaskBlockFinder(getServerPlayer(ctx.getSource()), id, defaultRange, defaultCount, defaultSpeed);
        return Command.SINGLE_SUCCESS;
    }
}