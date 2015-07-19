package com.forgeessentials.chat.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;

public class CommandIrc extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "irc";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/irc <message...>: Send a message to a client on IRC.";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat.irc";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (!IrcHandler.getInstance().isConnected())
            throw new TranslatedCommandException("Not connected to IRC!");
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.message.usage");
        }
        else
        {
            IChatComponent message = getChatComponentFromNthArg(sender, args, 0, !(sender instanceof EntityPlayer));
            IrcHandler.getInstance().sendPlayerMessage(sender, message);
        }
    }

}
