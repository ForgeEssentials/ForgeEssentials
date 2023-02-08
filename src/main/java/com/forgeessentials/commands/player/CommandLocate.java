package com.forgeessentials.commands.player;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandLocate extends BaseCommand
{

    public CommandLocate(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "locate";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
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
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".locate";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(CommandContext -> execute(CommandContext)
                                )
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
        if (player == null)
            throw new TranslatedCommandException("Player does not exist, or is not online.");

        WorldPoint point = new WorldPoint(player);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("%s is at %d, %d, %d in dim %d with gamemode %s", //
                player.getName(), point.getX(), point.getY(), point.getZ(), point.getDimension(), //
                player.gameMode.getGameModeForPlayer().getName()));
        return Command.SINGLE_SUCCESS;
    }
}
