package com.forgeessentials.chat.command;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;

public class CommandIrc extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "irc";
    }


    @Override
    public String getPermissionNode()
    {
        return "fe.chat.irc";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (!IrcHandler.getInstance().isConnected())
            throw new TranslatedCommandException("Not connected to IRC!");
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.message.usage");
        }
        else
        {
            ITextComponent message = getChatComponentFromNthArg(sender, args, 0, !(sender instanceof PlayerEntity));
            IrcHandler.getInstance().sendPlayerMessage(sender, message);
        }
    }

}
