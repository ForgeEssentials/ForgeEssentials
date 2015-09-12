package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.permission.PermissionContext;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.registry.GameData;

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
        this.ident = (senderPlayer == null) ? (sender instanceof DoAsCommandSender ? ((DoAsCommandSender) sender).getUserIdent() : null) : UserIdent
                .get(senderPlayer);
        this.isTabCompletion = isTabCompletion;
        if (isTabCompletion)
            tabCompletion = new ArrayList<>();
        this.permissionContext = new PermissionContext(sender, command);
    }

    public CommandParserArgs(ICommand command, String[] args, ICommandSender sender)
    {
        this(command, args, sender, false);
    }

    public void confirm(String message)
    {
        if (!isTabCompletion)
            ChatOutputHandler.chatConfirmation(sender, message);
    }

    public void notify(String message)
    {
        if (!isTabCompletion)
            ChatOutputHandler.chatNotification(sender, message);
    }

    public void warn(String message)
    {
        if (!isTabCompletion)
            ChatOutputHandler.chatWarning(sender, message);
    }

    public void error(String message)
    {
        if (!isTabCompletion)
            ChatOutputHandler.chatError(sender, message);
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
        return parsePlayer(true, false);
    }

    @Deprecated
    public UserIdent parsePlayer(boolean mustExist)
    {
        return parsePlayer(mustExist, false);
    }

    public UserIdent parsePlayer(boolean mustExist, boolean mustBeOnline)
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
                return ident;
            }
            else
            {
                UserIdent ident = UserIdent.get(name, sender, mustExist);
                if (mustExist && (ident == null || !ident.hasUuid()))
                    throw new TranslatedCommandException("Player %s not found", name);
                else if (mustBeOnline && !ident.hasPlayer())
                    throw new TranslatedCommandException("Player %s is not online", name);
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

    public Item parseItem()
    {
        if (isTabCompletion && size() == 1)
        {
            for (Object item : GameData.getItemRegistry().getKeys())
                if (item.toString().startsWith(peek()))
                    tabCompletion.add(item.toString());
            for (Object item : GameData.getItemRegistry().getKeys())
                if (item.toString().startsWith("minecraft:" + peek()))
                    tabCompletion.add(item.toString().substring(10));
            throw new CancelParsingException();
        }
        String itemName = remove();
        Item item = CommandBase.getItemByText(sender, itemName);
        if (item == null)
            error(Translator.format("Item %s not found", itemName));
        return item;
    }

    public Block parseBlock()
    {
        if (isTabCompletion && size() == 1)
        {
            for (Object block : GameData.getBlockRegistry().getKeys())
                if (block.toString().startsWith(peek()))
                    tabCompletion.add(block.toString());
            for (Object block : GameData.getBlockRegistry().getKeys())
                if (block.toString().startsWith("minecraft:" + peek()))
                    tabCompletion.add(block.toString().substring(10));
            throw new CancelParsingException();
        }
        String itemName = remove();
        return CommandBase.getBlockByText(sender, itemName);
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

    public World parseWorld()
    {
        if (isTabCompletion && size() == 1)
        {
            tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), APIRegistry.namedWorldHandler.listWorldNames());
            throw new CancelParsingException();
        }
        if (isEmpty())
        {
            if (senderPlayer != null)
                return senderPlayer.worldObj;
            else
                throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
        }
        else
        {
            String name = remove();
            if (name.equalsIgnoreCase("here"))
            {
                if (senderPlayer == null)
                    throw new TranslatedCommandException("\"here\" cannot be used in console.");
                return senderPlayer.worldObj;
            }
            else
            {
                return APIRegistry.namedWorldHandler.getWorld(name);
            }
        }
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

    public double parseDouble()
    {
        checkTabCompletion();
        return CommandBase.parseDouble(sender, remove());
    }

    public boolean parseBoolean()
    {
        checkTabCompletion();
        String value = remove().toLowerCase();
        switch (value)
        {
        case "off":
        case "false":
        case "disable":
        case "disabled":
            return false;
        case "on":
        case "true":
        case "enable":
        case "enabled":
            return true;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_INVALID_ARGUMENT, value);
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

    public WorldPoint getSenderPoint()
    {
        ICommandSender s = sender != null ? sender : MinecraftServer.getServer();
        return new WorldPoint(s.getEntityWorld(), s.getPlayerCoordinates());
    }
}
