package com.forgeessentials.chat.commands;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.chat.irc.IRCHelper;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import net.minecraft.command.ICommandSender;

public class CommandIRC extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "irc";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args[0].equalsIgnoreCase("reconnect"))
        {
            IRCHelper.reconnect(sender);
        }
        else if (args[0].equalsIgnoreCase("disconnect"))
        {
            IRCHelper.shutdown();
        }

    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat.irc";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/irc [reconnect|disconnect] Connect or disconnect the IRC server bot.";
    }

    @Override
    public RegGroup getReggroup()
    {

        return RegGroup.ZONE_ADMINS;
    }

}
