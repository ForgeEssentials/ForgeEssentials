package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;

/**
 *
 */
public class CommandParserArgs {

    public final String command;
    public final Queue<String> args;
    public final ICommandSender sender;
    public final EntityPlayerMP senderPlayer;
    public final UserIdent userIdent;
    public final boolean isTabCompletion;

    public List<String> tabCompletion = null;

    public CommandParserArgs(String command, String[] args, ICommandSender sender, boolean isTabCompletion)
    {
        this.command = command;
        this.args = new LinkedList<String>(Arrays.asList(args));
        this.sender = sender;
        this.senderPlayer = (sender instanceof EntityPlayerMP) ? (EntityPlayerMP) sender : null;
        this.userIdent = (senderPlayer == null) ? null : new UserIdent(senderPlayer);
        this.isTabCompletion = isTabCompletion;
    }

    public CommandParserArgs(String commandName, String[] args, ICommandSender sender)
    {
        this(commandName, args, sender, false);
    }

    public void info(String message)
    {
        if (!isTabCompletion)
            OutputHandler.chatConfirmation(sender, message);
    }

    public void warn(String message)
    {
        if (!isTabCompletion)
            OutputHandler.chatWarning(sender, message);
    }

    public void error(String message)
    {
        if (!isTabCompletion)
            OutputHandler.chatError(sender, message);
    }

    public int size()
    {
        return args.size();
    }

    public String remove()
    {
        return args.remove();
    }

    public String peek()
    {
        return args.peek();
    }

    public boolean isEmpty()
    {
        return args.isEmpty();
    }

    public boolean hasPlayer()
    {
        return senderPlayer != null;
    }

    public UserIdent parsePlayer()
    {
        if (isTabCompletion && size() == 1)
        {
            tabCompletion = completePlayer(peek());
            return null;
        }
        if (isEmpty())
        {
            if (userIdent != null)
                return userIdent;
            else
                throw new CommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
        }
        else
        {
            String name = remove();
            if (name.equalsIgnoreCase("_ME_"))
            {
                if (senderPlayer == null)
                    throw new CommandException("_ME_ cannot be used in console.");
                return new UserIdent(senderPlayer);
            }
            else
            {
                return new UserIdent(name, sender);
            }
        }
    }

    public static List<String> completePlayer(String arg)
    {
        Set<String> result = new TreeSet<String>();
        for (UserIdent knownPlayerIdent : APIRegistry.perms.getServerZone().getKnownPlayers())
        {
            if (CommandBase.doesStringStartWith(arg, knownPlayerIdent.getUsernameOrUUID()))
                result.add(knownPlayerIdent.getUsernameOrUUID());
        }
        for (Object player : MinecraftServer.getServer().getConfigurationManager().playerEntityList)
        {
            if (CommandBase.doesStringStartWith(arg, ((EntityPlayerMP) player).getGameProfile().getName()))
                result.add(((EntityPlayerMP) player).getGameProfile().getName());
        }
        return new ArrayList<String>(result);
    }

    public void checkPermission(String perm)
    {
        if (userIdent != null && !APIRegistry.perms.checkUserPermission(userIdent, perm))
            throw new CommandException(FEPermissions.MSG_NO_COMMAND_PERM);
    }

}
