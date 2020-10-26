package com.forgeessentials.util;

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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.server.permission.context.IContext;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

/**
 *
 */
public class CommandParserArgs
{

    public final ICommand command;
    public final LinkedList<String> args;
    public final ICommandSender sender;
    public final EntityPlayerMP senderPlayer;
    public final UserIdent ident;
    public final boolean isTabCompletion;
    public final MinecraftServer server;
    public IContext context;

    public List<String> tabCompletion;

    public CommandParserArgs(ICommand command, String[] args, ICommandSender sender, boolean isTabCompletion, MinecraftServer server)
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
        this.context = null;
        this.server = server;
    }

    public CommandParserArgs(ICommand command, String[] args, ICommandSender sender, MinecraftServer server)
    {
        this(command, args, sender, false, server);
    }

    public void sendMessage(String message)
    {
        if (!isTabCompletion)
            ChatOutputHandler.sendMessage(sender, message);
    }

    public void sendMessage(ITextComponent message)
    {
        if (!isTabCompletion)
            ChatOutputHandler.sendMessage(sender, message);
    }

    public void confirm(String message, Object... args)
    {
        if (!isTabCompletion)
            ChatOutputHandler.chatConfirmation(sender, Translator.format(message, args));
    }

    public void notify(String message, Object... args)
    {
        if (!isTabCompletion)
            ChatOutputHandler.chatNotification(sender, Translator.format(message, args));
    }

    public void warn(String message, Object... args)
    {
        if (!isTabCompletion)
            ChatOutputHandler.chatWarning(sender, Translator.format(message, args));
    }

    public void error(String message, Object... args)
    {
        if (!isTabCompletion)
            ChatOutputHandler.chatError(sender, Translator.format(message, args));
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
    public UserIdent parsePlayer() throws CommandException
    {
        return parsePlayer(true, false);
    }

    @Deprecated
    public UserIdent parsePlayer(boolean mustExist) throws CommandException
    {
        return parsePlayer(mustExist, false);
    }

    public UserIdent parsePlayer(boolean mustExist, boolean mustBeOnline) throws CommandException
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
        Set<String> result = new TreeSet<>();
        for (UserIdent knownPlayerIdent : APIRegistry.perms.getServerZone().getKnownPlayers())
        {
            if (CommandBase.doesStringStartWith(arg, knownPlayerIdent.getUsernameOrUuid()))
                result.add(knownPlayerIdent.getUsernameOrUuid());
        }
        for (EntityPlayerMP player : ServerUtil.getPlayerList())
        {
            if (CommandBase.doesStringStartWith(arg, player.getName()))
                result.add(player.getName());
        }
        return new ArrayList<>(result);
    }

    public Item parseItem() throws CommandException
    {
        if (isTabCompletion && size() == 1)
        {
            for (Object item : Item.REGISTRY.getKeys())
                if (item.toString().startsWith(peek()))
                    tabCompletion.add(item.toString());
            for (Object item : Item.REGISTRY.getKeys())
                if (item.toString().startsWith("minecraft:" + peek()))
                    tabCompletion.add(item.toString().substring(10));
            throw new CancelParsingException();
        }
        String itemName = remove();
        Item item = CommandBase.getItemByText(sender, itemName);
        if (item == null)
            throw new TranslatedCommandException("Item %s not found", itemName);
        return item;
    }

    public Block parseBlock() throws CommandException
    {
        if (isTabCompletion && size() == 1)
        {
            for (Object block : Block.REGISTRY.getKeys())
                if (block.toString().startsWith(peek()))
                    tabCompletion.add(block.toString());
            for (Object block : Block.REGISTRY.getKeys())
                if (block.toString().startsWith("minecraft:" + peek()))
                    tabCompletion.add(block.toString().substring(10));
            throw new CancelParsingException();
        }
        String itemName = remove();
        return CommandBase.getBlockByText(sender, itemName);
    }

    public String parsePermission() throws CommandException
    {
        if (isTabCompletion && size() == 1)
        {
            String permission = peek();
            Set<String> permissionSet = APIRegistry.perms.getServerZone().getRootZone().enumRegisteredPermissions();
            Set<String> result = new TreeSet<>();
            for (String perm : permissionSet)
            {
                int nodeIndex = perm.indexOf('.', permission.length());
                if (nodeIndex >= 0)
                    perm = perm.substring(0, nodeIndex);
                if (CommandBase.doesStringStartWith(permission, perm))
                    result.add(perm);
            }
            tabCompletion = new ArrayList<>(result);
            throw new CancelParsingException();
        }
        return remove();
    }

    public void checkPermission(String perm) throws CommandException
    {
        if (!isTabCompletion && sender != null && !hasPermission(perm))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
    }

    public boolean hasPermission(String perm)
    {
        if (sender instanceof EntityPlayer)
        return APIRegistry.perms.checkPermission(senderPlayer, perm);
        else return true;
    }

    public void tabComplete(String... completionList) throws CancelParsingException
    {
        if (!isTabCompletion || args.size() != 1)
            return;
        tabCompletion.addAll(ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), completionList));
        throw new CancelParsingException();
    }

    public void tabComplete(Collection<String> completionList) throws CancelParsingException
    {
        if (!isTabCompletion || args.size() != 1)
            return;
        tabCompletion.addAll(ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), completionList));
        throw new CancelParsingException();
    }

    public void tabCompleteWord(String completion)
    {
        if (!isTabCompletion || args.size() != 1 || completion == null || completion.isEmpty())
            return;
        if (completion.startsWith(args.peek()))
            tabCompletion.add(completion);
    }

    public WorldServer parseWorld() throws CommandException
    {
        if (isTabCompletion && size() == 1)
        {
            tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), APIRegistry.namedWorldHandler.getWorldNames());
            throw new CancelParsingException();
        }
        if (isEmpty())
        {
            if (senderPlayer != null)
                return (WorldServer) senderPlayer.world;
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
                return (WorldServer) senderPlayer.world;
            }
            else
            {
                return APIRegistry.namedWorldHandler.getWorld(name);
            }
        }
    }

    public int parseInt() throws CommandException
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
            throw new TranslatedCommandException("Invalid number: %s", strValue);
        }
    }

    public long parseLong() throws CommandException
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

    public double parseDouble() throws CommandException
    {
        checkTabCompletion();
        return CommandBase.parseDouble(remove());
    }

    public boolean parseBoolean() throws CommandException
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

    public static final Pattern timeFormatPattern = Pattern.compile("(\\d+)(\\D+)?");

    private static double mcHour = 1000;
    private static double mcMinute = 1000.0/60;
    private static double mcSecond = 1000.0/60/60;

    /**
     * Parses a Time string in Minecraft time format.
     * @return
     * @throws CommandException
     */
    public Long mcParseTimeReadable() throws CommandException
    {
        if (isEmpty()) {
            checkTabCompletion();
            return null;
        }
        String timeStr = remove();

        Matcher m = timeFormatPattern.matcher(timeStr);
        if (!m.find())
        {
            throw new TranslatedCommandException("Invalid time format: %s", timeStr);
        }

        double resultPart = Double.parseDouble(m.group(1));

        String unit = m.group(2);
        if (unit != null)
        {
            switch (unit)
            {
            case "s":
            case "second":
            case "seconds":
                resultPart *= mcSecond;
                break;
            case "m":
            case "minute":
            case "minutes":
                resultPart *= mcMinute;
                break;
            case "h":
            case "hour":
            case "hours":
                resultPart *= mcHour;
                break;
            default:
                throw new TranslatedCommandException("Invalid time format: %s", timeStr);
            }
        }
        return Math.round(resultPart);
    }
    public long parseTimeReadable() throws CommandException
    {
        checkTabCompletion();
        String value = remove();
        Matcher m = timeFormatPattern.matcher(value);
        if (!m.find())
        {
            throw new TranslatedCommandException("Invalid time format: %s", value);
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
                    resultPart *= (long) 1000 * 60 * 60 * 24 * 30;
                    break;
                default:
                    throw new TranslatedCommandException("Invalid time format: %s", value);
                }
            }

            result += resultPart;
        }
        while (m.find());

        return result;
    }

    public void checkTabCompletion() throws CommandException
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

    public void requirePlayer() throws CommandException
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
        ICommandSender s = sender != null ? sender : server;
        return new WorldPoint(s.getEntityWorld(), s.getPosition());
    }

    public WorldZone getWorldZone() throws CommandException
    {
        if (senderPlayer == null)
            throw new TranslatedCommandException("Player needed");
        return APIRegistry.perms.getServerZone().getWorldZone(senderPlayer.dimension);
    }

    public void needsPlayer() throws CommandException
    {
        if (senderPlayer == null)
            throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
    }

}
