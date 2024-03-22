package com.forgeessentials.commands.player;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandLocate extends ForgeEssentialsCommandBuilder
{

    public CommandLocate(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "locate";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "gps", "loc", "playerinfo" };
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
        return baseBuilder.then(Commands.argument("player", EntityArgument.player())
                .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        if (player == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Player does not exist, or is not online.");
        }

        WorldPoint point = new WorldPoint(player);
        ChatOutputHandler.chatConfirmation(ctx.getSource(),
                Translator.format("%s is at %d, %d, %d in dim %s with gamemode %s", //
                        player.getDisplayName().getString(), point.getX(), point.getY(), point.getZ(),
                        point.getDimension(), //
                        player.gameMode.getGameModeForPlayer().getName()));
        return Command.SINGLE_SUCCESS;
    }
}
