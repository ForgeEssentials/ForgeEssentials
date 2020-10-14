package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandSeen extends ParserCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "seen";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/seen <player>: Check when a player has been last seen online";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".seen";
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);

        UserIdent player = arguments.parsePlayer(false, false);

        if (player.hasPlayer())
        {
            arguments.confirm("Player %s is currently online", player.getUsernameOrUuid());
            return;
        }

        if (!player.hasUuid() || !PlayerInfo.exists(player.getUuid()))
            throw new PlayerNotFoundException("commands.generic.player.notFound");

        PlayerInfo pi = PlayerInfo.get(player.getUuid());
        long t = (System.currentTimeMillis() - pi.getLastLogout().getTime()) / 1000;
        arguments.confirm(Translator.format("Player %s was last seen %s ago", player.getUsernameOrUuid(),
                ChatOutputHandler.formatTimeDurationReadable(t, false)));
        PlayerInfo.discard(pi.ident.getUuid());
    }

}
