package com.forgeessentials.commands;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.LoginMessage;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class CommandMotd extends FEcmdModuleCommands {

    @Override
    public String getCommandName()
    {
        return "motd";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload"))
        {
            LoginMessage.loadFile();
        }
        LoginMessage.sendLoginMessage(sender);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload"))
        {
            LoginMessage.loadFile();
        }
        LoginMessage.sendLoginMessage(sender);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "reload");
        }
        else
        {
            return null;
        }
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.GUESTS;
    }

    @Override
    public int compareTo(Object o)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        // TODO Auto-generated method stub
        return "/motd Get the server message of the day.";
    }
}
