package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionContext;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;

/**
 *
 */
public class CommandParserArgs
{

    public final ICommand command;
    public final Queue<String> args;
    public final ICommandSender sender;
    public final EntityPlayerMP senderPlayer;
    public final UserIdent ident;
    public final boolean isTabCompletion;
    private final PermissionContext permissionContext;

    public List<String> tabCompletion;

    public CommandParserArgs(ICommand command, String[] args, ICommandSender sender, boolean isTabCompletion)
    {
        this.command = command;
        this.args = new LinkedList<String>(Arrays.asList(args));
        this.sender = sender;
        this.senderPlayer = (sender instanceof EntityPlayerMP) ? (EntityPlayerMP) sender : null;
        this.ident = (senderPlayer == null) ? null : UserIdent.get(senderPlayer);
        this.isTabCompletion = isTabCompletion;
        if (isTabCompletion)
            tabCompletion = new ArrayList<>();
        this.permissionContext = new PermissionContext().setCommandSender(sender).setCommand(command);
    }

    public CommandParserArgs(ICommand command, String[] args, ICommandSender sender)
    {
        this(command, args, sender, false);
    }

    public void confirm(String message)
    {
        if (!isTabCompletion)
            OutputHandler.chatConfirmation(sender, message);
    }

    public void notify(String message)
    {
        if (!isTabCompletion)
            OutputHandler.chatNotification(sender, message);
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

    @Deprecated
    public UserIdent parsePlayer()
    {
        return parsePlayer(true);
    }

    public UserIdent parsePlayer(boolean mustExist)
    {
        if (isTabCompletion && size() == 1)
        {
            tabCompletion = completePlayer(peek());
            throw new CancelParsingException();
        }
        if (isEmpty())
        {
            if (ident != null)
                return ident;
            else
                throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
        }
        else
        {
            String name = remove();
            if (name.equalsIgnoreCase("_ME_"))
            {
                if (senderPlayer == null)
                    throw new TranslatedCommandException("_ME_ cannot be used in console.");
                return UserIdent.get(senderPlayer);
            }
            else
            {
                UserIdent ident = UserIdent.get(name, sender, mustExist);
                if (mustExist && (ident == null || !ident.hasUuid()))
                    throw new TranslatedCommandException("Player %s not found", name);
                return ident;
            }
        }
    }

    public static List<String> completePlayer(String arg)
    {
        Set<String> result = new TreeSet<String>();
        for (UserIdent knownPlayerIdent : APIRegistry.perms.getServerZone().getKnownPlayers())
        {
            if (CommandBase.doesStringStartWith(arg, knownPlayerIdent.getUsernameOrUuid()))
                result.add(knownPlayerIdent.getUsernameOrUuid());
        }
        for (EntityPlayerMP player : ServerUtil.getPlayerList())
        {
            if (CommandBase.doesStringStartWith(arg, player.getCommandSenderName()))
                result.add(player.getCommandSenderName());
        }
        return new ArrayList<String>(result);
    }

    public void checkPermission(String perm)
    {
        if (!isTabCompletion && sender != null && !hasPermission(perm))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
    }

    public boolean hasPermission(String perm)
    {
        return APIRegistry.perms.checkPermission(permissionContext, perm);
    }

    public void tabComplete(String... completionList)
    {
        if (!isTabCompletion || args.size() != 1)
            return;
        tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), completionList);
        throw new CancelParsingException();
    }

    public void tabComplete(Collection<String> completionList)
    {
        if (!isTabCompletion || args.size() != 1)
            return;
        tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), completionList);
        throw new CancelParsingException();
    }

    public int parseInt()
    {
        checkTabCompletion();
        String value = remove();
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            throw new TranslatedCommandException("Invalid number: %s", value);
        }
    }

    public long parseLong()
    {
        checkTabCompletion();
        String value = remove();
        try
        {
            return Long.parseLong(value);
        }
        catch (NumberFormatException e)
        {
            throw new TranslatedCommandException("Invalid number: %s", value);
        }
    }

    public Float parseFloat()
    {
        checkTabCompletion();
        String value = remove();
        try
        {
            return Float.parseFloat(value);
        }
        catch (NumberFormatException e)
        {
            throw new TranslatedCommandException("Invalid number: %s", value);
        }
    }

    public Double parseDouble()
    {
        checkTabCompletion();
        String value = remove();
        try
        {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e)
        {
            throw new TranslatedCommandException("Invalid number: %s", value);
        }
    }

    public void checkTabCompletion()
    {
        if (isTabCompletion && size() == 1)
            throw new CancelParsingException();
    }

    public static class CancelParsingException extends CommandException
    {

        public CancelParsingException()
        {
            super("");
        }

    }

    public void requirePlayer()
    {
        if (senderPlayer == null)
            throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
    }

    public String[] toArray()
    {
        return args.toArray(new String[args.size()]);
    }

    @Override
    public String toString()
    {
        return StringUtils.join(args.toArray(), " ");
    }

}
