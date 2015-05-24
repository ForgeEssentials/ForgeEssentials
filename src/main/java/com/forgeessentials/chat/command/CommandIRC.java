package com.forgeessentials.chat.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;

public class CommandIRC extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "irc";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/irc [reconnect|disconnect|status] Connect or disconnect the IRC server bot.";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat.irc";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("reconnect"))
            {
                IrcHandler.instance.connect();
            }
            else if (args[0].equalsIgnoreCase("disconnect"))
            {
                IrcHandler.instance.disconnect();
            }
        }
        else
        {
            OutputHandler.sendMessage(sender, "IRC bot is " + (IrcHandler.instance.isConnected() ? "online" : "offline"));
        }
    }

}
