package com.forgeessentials.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.FEApi;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.TranslatedCommandException;
import com.forgeessentials.util.Translator;
import com.forgeessentials.util.Utils;
import com.sun.tools.corba.se.idl.Util;

import cpw.mods.fml.common.registry.GameData;

public class CommandParserArgs
{

    public final ICommand command;
    public final LinkedList<String> args;
    public final ICommandSender sender;
    public final EntityPlayerMP senderPlayer;
    public final UserIdent ident;
    public final boolean isTabCompletion;

    public List<String> tabCompletion;

    public CommandParserArgs(ICommand command, String[] args, ICommandSender sender, boolean isTabCompletion)
    {
        this.command = command;
        this.args = new LinkedList<>(Arrays.asList(args));
        this.sender = sender;
        this.senderPlayer = (sender instanceof EntityPlayerMP) ? (EntityPlayerMP) sender : null;
        this.ident = (senderPlayer == null) ? (sender instanceof DoAsCommandSender ? ((DoAsCommandSender) sender).getUserIdent() : null) : UserIdent
                .get(senderPlayer);
        this.isTabCompletion = isTabCompletion;
        if (isTabCompletion)
            tabCompletion = new ArrayList<>();
    }

    public CommandParserArgs(ICommand command, String[] args, ICommandSender sender)
    {
        this(command, args, sender, false);
    }

    public void sendMessage(String message)
    {
        if (!isTabCompletion)
            ChatUtil.sendMessage(sender, message);
    }

    public void sendMessage(IChatComponent message)
    {
        if (!isTabCompletion)
            ChatUtil.sendMessage(sender, message);
    }

    public void confirm(String message, Object... args)
    {
        if (!isTabCompletion)
            ChatUtil.chatConfirmation(sender, Translator.format(message, args));
    }

    public void notify(String message, Object... args)
    {
        if (!isTabCompletion)
            ChatUtil.chatNotification(sender, Translator.format(message, args));
    }

    public void warn(String message, Object... args)
    {
        if (!isTabCompletion)
            ChatUtil.chatWarning(sender, Translator.format(message, args));
    }

    public void error(String message, Object... args)
    {
        if (!isTabCompletion)
            ChatUtil.chatError(sender, Translator.format(message, args));
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

    public String get(int index)
    {
        return args.get(index);
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
                throw new CommandException(MessageConstants.MSG_NOT_ENOUGH_ARGUMENTS);
        }
        else
        {
            String name = remove();
            if (name.equalsIgnoreCase("_ME_"))
            {
                if (senderPlayer == null)
                    throw new CommandException("_ME_ cannot be used in console.");
                return ident;
            }
            else
            {
                UserIdent ident = UserIdent.get(name, sender, mustExist);
                if (mustExist && (ident == null || !ident.hasUuid()))
                    throw new CommandException("Player %s not found", name);
                else if (mustBeOnline && !ident.hasPlayer())
                    throw new CommandException("Player %s is not online", name);
                return ident;
            }
        }
    }

    public WorldServer parseWorld()
    {
        if (isTabCompletion && size() == 1)
        {
            tabCompletion = Utils.getListOfStringsMatchingLastWord(args.peek(), FEApi.namedWorldHandler.getWorldNames());
            throw new CancelParsingException();
        }
        if (isEmpty())
        {
            if (senderPlayer != null)
                return (WorldServer) senderPlayer.worldObj;
            else
                throw new TranslatedCommandException(MessageConstants.MSG_NOT_ENOUGH_ARGUMENTS);
        }
        else
        {
            String name = remove();
            if (name.equalsIgnoreCase("here"))
            {
                if (senderPlayer == null)
                    throw new TranslatedCommandException("\"here\" cannot be used in console.");
                return (WorldServer) senderPlayer.worldObj;
            }
            else
            {
                return FEApi.namedWorldHandler.getWorld(name);
            }
        }
    }

    public List<String> completePlayer(String arg)
    {
        Set<String> result = new TreeSet<>();
        for (EntityPlayerMP player : Utils.getPlayerList())
        {
            if (CommandBase.doesStringStartWith(arg, player.getCommandSenderName()))
                result.add(player.getCommandSenderName());
        }
        return new ArrayList<>(result);
    }

    public String parsePermission()
    {
        if (isTabCompletion && size() == 1)
        {
            tabCompletion = new ArrayList<>();
            throw new CancelParsingException();
        }
        return remove();
    }

    public void checkPermission(String perm)
    {
        if (!isTabCompletion && sender != null && !hasPermission(perm))
            throw new TranslatedCommandException(MessageConstants.MSG_NO_COMMAND_PERM);
    }

    public boolean hasPermission(String perm)
    {
        Utils.felog.warn("This should never be called! The subclass FECommandParserArgs should overwrite this.");
        return false;
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
            throw new CommandException("Item %s not found", itemName);
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

    public void tabComplete(String... completionList)
    {
        if (!isTabCompletion || args.size() != 1)
            return;
        tabCompletion.addAll(Utils.getListOfStringsMatchingLastWord(args.peek(), completionList));
        throw new CancelParsingException();
    }

    public void tabComplete(Collection<String> completionList)
    {
        if (!isTabCompletion || args.size() != 1)
            return;
        tabCompletion.addAll(Utils.getListOfStringsMatchingLastWord(args.peek(), completionList));
        throw new CancelParsingException();
    }

    public void tabCompleteWord(String completion)
    {
        if (!isTabCompletion || args.size() != 1 || completion == null || completion.isEmpty())
            return;
        if (completion.startsWith(args.peek()))
            tabCompletion.add(completion);
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
            throw new CommandException("Invalid number: %s", value);
        }
    }

    public int parseInt(int min, int max) throws CommandException
    {
        checkTabCompletion();
        String strValue = remove();
        try
        {
            int value = Integer.parseInt(strValue);
            if (value < min)
                throw new NumberInvalidException("commands.generic.num.tooSmall", strValue, Integer.toString(min));
            if (value > max)
                throw new NumberInvalidException("commands.generic.num.tooBig", strValue, Integer.toString(max));
            return value;
        }
        catch (NumberFormatException e)
        {
            throw new CommandException("Invalid number: %s", strValue);
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
            throw new CommandException("Invalid number: %s", value);
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
            throw new CommandException(MessageConstants.MSG_INVALID_ARGUMENT, value);
        }
    }

    public static final Pattern timeFormatPattern = Pattern.compile("(\\d+)(\\D+)?");

    public long parseTimeReadable()
    {
        checkTabCompletion();
        String value = remove();
        Matcher m = timeFormatPattern.matcher(value);
        if (!m.find())
        {
            throw new CommandException("Invalid time format: %s", value);
        }

        long result = 0;

        do
        {
            long resultPart = Long.parseLong(m.group(1));

            String unit = m.group(2);
            if (unit != null)
            {
                switch (unit)
                {
                case "s":
                case "second":
                case "seconds":
                    resultPart *= 1000;
                    break;
                case "m":
                case "minute":
                case "minutes":
                    resultPart *= 1000 * 60;
                    break;
                case "h":
                case "hour":
                case "hours":
                    resultPart *= 1000 * 60 * 60;
                    break;
                case "d":
                case "day":
                case "days":
                    resultPart *= 1000 * 60 * 60 * 24;
                    break;
                case "w":
                case "week":
                case "weeks":
                    resultPart *= 1000 * 60 * 60 * 24 * 7;
                    break;
                case "month":
                case "months":
                    resultPart *= 1000 * 60 * 60 * 24 * 30;
                    break;
                default:
                    throw new CommandException("Invalid time format: %s", value);
                }
            }

            result += resultPart;
        }
        while (m.find());

        return result;
    }

    public void checkTabCompletion()
    {
        if (isTabCompletion && size() == 1)
            throw new CancelParsingException();
    }

    public void requirePlayer()
    {
        if (senderPlayer == null)
            throw new CommandException(MessageConstants.MSG_NO_CONSOLE_COMMAND);
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

    /**
     * Utility exception which tells a command parser to cancel parsing.
     * Used to break out of parsing for tab completion.
     */
    public static class CancelParsingException extends CommandException
    {

        public CancelParsingException()
        {
            super("");
        }

    }

}
