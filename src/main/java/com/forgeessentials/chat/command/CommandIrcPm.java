package com.forgeessentials.chat.command;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;

public class CommandIrcPm extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "ircpm";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/ircpm <name> <message...>: Send a message to a client on IRC.";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat.ircpm";
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
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.message.usage");
        }
        else
        {
            ICommandSender target = IrcHandler.getInstance().getIrcUser(args[0]);
            if (target == null)
            {
                throw new PlayerNotFoundException("commands.generic.player.notFound");
            }
            else if (target == sender)
            {
                throw new PlayerNotFoundException("commands.message.sameTarget");
            }
            else
            {
                ITextComponent message = getChatComponentFromNthArg(sender, args, 1, !(sender instanceof EntityPlayer));
                ModuleChat.tell(sender, message, target);
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length != 1)
            return null;
        return getListOfStringsMatchingLastWord(args, IrcHandler.getInstance().getIrcUserNames());
    }

}
