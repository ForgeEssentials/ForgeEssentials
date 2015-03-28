package com.forgeessentials.chat.commands;

import com.forgeessentials.chat.irc.IRCHelper;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class CommandIRC extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "irc";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
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

        else
        {
            IRCHelper.status(sender);
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
        return "/irc [reconnect|disconnect|status] Connect or disconnect the IRC server bot.";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.OP;
    }

}
