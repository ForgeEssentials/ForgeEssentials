package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class CommandUtils 
{
	public static ICommandSource GetSource(CommandSource source) {
		ICommandSource isource = ObfuscationReflectionHelper.getPrivateValue(CommandSource.class, source, "source");
		return isource;
	}
	
    public static UserIdent parsePlayer(String name, CommandSource sender, boolean mustExist, boolean mustBeOnline) throws CommandException
    {
        UserIdent ident = UserIdent.get(name, sender, mustExist);
        if (mustExist && (ident == null || !ident.hasUuid()))
            throw new TranslatedCommandException("Player %s not found", name);
        else if (mustBeOnline && !ident.hasPlayer())
            throw new TranslatedCommandException("Player %s is not online", name);
        return ident;
    }
    public static List<String> getAllPlayernames()
    {
        List<String> arraylist = new ArrayList<>();
        for (UserIdent s2 : APIRegistry.perms.getServerZone().getKnownPlayers())
        {
            arraylist.add(s2.getUsernameOrUuid());
        }
        return arraylist;
    }
	public static double parseDouble(String input) throws NumberFormatException
    {
        try
        {
            double d0 = Double.parseDouble(input);

            if (!Doubles.isFinite(d0))
            {
                throw new NumberFormatException();
            }
            else
            {
                return d0;
            }
        }
        catch (NumberFormatException var3)
        {
            throw new NumberFormatException();
        }
    }
    public static long parseLong(String input) throws CommandException
    {
        try
        {
            return Long.parseLong(input);
        }
        catch (NumberFormatException e)
        {
            throw new TranslatedCommandException("Invalid number: %s", input);
        }
    }
    public static double parseDouble(String input, double min) throws NumberFormatException
    {
        return parseDouble(input, min, Double.MAX_VALUE);
    }

    public static double parseDouble(String input, double min, double max) throws NumberFormatException
    {
        double d0 = parseDouble(input);

        if (d0 < min)
        {
            throw new NumberFormatException();
        }
        else if (d0 > max)
        {
            throw new NumberFormatException();
        }
        else
        {
            return d0;
        }
    }

    public static int parseInt(String input) throws NumberFormatException
    {
        try
        {
            return Integer.parseInt(input);
        }
        catch (NumberFormatException var2)
        {
            throw new NumberFormatException();
        }
    }

    public static int parseInt(String input, int min) throws NumberFormatException
    {
        return parseInt(input, min, Integer.MAX_VALUE);
    }
    /*
     * Argument to pass: 
     * int to test
     * min value
     * max value
     */
    public static int parseInt(int input, int min, int max) throws NumberFormatException
    {
        return parseIntT(input, min, max);
    }

    public static int parseInt(String input, int min, int max) throws NumberFormatException
    {
        return parseIntT(parseInt(input), min, max);
    }

    private static int parseIntT(int input, int min, int max) throws NumberFormatException
    {
        if (input < min)
        {
            throw new NumberFormatException();
        }
        else if (input > max)
        {
            throw new NumberFormatException();
        }
        else
        {
            return input;
        }
    }

    /**
     * Parse int with support for relative int.
     *
     * @param string
     * @param relativeStart
     * @return
     * @throws NumberInvalidException
     */
    public static int parseRelativeInt(String string, int relativeStart) throws NumberFormatException
    {
        if (string.startsWith("~"))
        {
            string = string.substring(1);
            return relativeStart + parseInt(string);
        }
        else
        {
            return parseInt(string);
        }
    }

    /**
     * Parse double with support for relative values.
     *
     * @param string
     * @param relativeStart
     * @return
     */
    public static double parseRelativeDouble(String string, double relativeStart) throws NumberFormatException
    {
        if (string.startsWith("~"))
        {
            string = string.substring(1);
            return relativeStart + parseInt(string);
        }
        else
        {
            return parseInt(string);
        }
    }

    // ------------------------------------------------------------
    // Utilities

    public static List<String> getListOfStringsMatchingLastWord(String arg, Collection<String> possibleMatches)
    {
        List<String> arraylist = new ArrayList<>();
        for (String s2 : possibleMatches)
        {
            if (doesStringStartWith(arg, s2))
            {
                arraylist.add(s2);
            }
        }
        return arraylist;
    }

    public static List<String> getListOfStringsMatchingLastWord(String arg, String... possibleMatches)
    {
        List<String> arraylist = new ArrayList<>();
        int i = possibleMatches.length;
        for (int j = 0; j < i; ++j)
        {
            String s2 = possibleMatches[j];
            if (doesStringStartWith(arg, s2))
            {
                arraylist.add(s2);
            }
        }
        return arraylist;
    }

    public static List<String> completePlayername(String arg)
    {
        List<String> arraylist = new ArrayList<>();
        for (UserIdent s2 : APIRegistry.perms.getServerZone().getKnownPlayers())
        {
            if (doesStringStartWith(arg, s2.getUsernameOrUuid()))
            {
                arraylist.add(s2.getUsernameOrUuid());
            }
        }
        return arraylist;
    }

    public static List<String> getListOfStringsMatchingLastWord(String[] inputArgs, Collection<?> possibleCompletions)
    {
        String s = inputArgs[inputArgs.length - 1];
        List<String> list = Lists.<String>newArrayList();

        if (!possibleCompletions.isEmpty())
        {
            for (String s1 : Iterables.transform(possibleCompletions, Functions.toStringFunction()))
            {
                if (doesStringStartWith(s, s1))
                {
                    list.add(s1);
                }
            }

            if (list.isEmpty())
            {
                for (Object object : possibleCompletions)
                {
                    if (object instanceof ResourceLocation && doesStringStartWith(s, ((ResourceLocation)object).getNamespace()))
                    {
                        list.add(String.valueOf(object));
                    }
                }
            }
        }

        return list;
    }

    public static List<String> matchToPlayers(String[] args)
    {
        return getListOfStringsMatchingLastWord(args, ServerLifecycleHooks.getCurrentServer().getPlayerNames());
    }

    public static List<String> getListOfStringsMatchingLastWord(String[] args, String... possibilities)
    {
        return getListOfStringsMatchingLastWord(args, Arrays.asList(possibilities));
    }
    
    public static TextComponent getChatComponentFromNthArg(String[] args, int index)
    {
        TextComponent itextcomponent = new StringTextComponent("");

        for (int i = index; i < args.length; ++i)
        {
            if (i > index)
            {
                itextcomponent.append(" ");
            }

            itextcomponent.append(net.minecraftforge.common.ForgeHooks.newChatWithLinks(args[i])); // Forge: links for messages

        }

        return itextcomponent;
    }

    public static boolean doesStringStartWith(String original, String region)
    {
        return region.regionMatches(true, 0, original, 0, original.length());
    }
    public static ServerPlayerEntity getServerPlayer(CommandSource source) {
        return (source.getEntity() instanceof ServerPlayerEntity) ? (ServerPlayerEntity) source.getEntity() : null;
    }
    
    public static final Pattern timeFormatPattern = Pattern.compile("(\\d+)(\\D+)?");

    private static double mcHour = 1000;
    private static double mcMinute = 1000.0 / 60;
    private static double mcSecond = 1000.0 / 60 / 60;

    /**
     * Parses a Time string in Minecraft time format.
     * 
     * @return
     * @throws CommandException
     */
    public static Long mcParseTimeReadable(String mcTime) throws CommandException
    {
        String timeStr = mcTime;

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

    public static long parseTimeReadable(String time) throws CommandException
    {
        String value = time;
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
    public static UserIdent getIdent(ServerPlayerEntity senderPlayer) {
        return getIdent(senderPlayer.createCommandSourceStack());
    }

    public static UserIdent getIdent(CommandSource sender) {
        ServerPlayerEntity senderPlayer = getServerPlayer(sender);
        UserIdent ident = (senderPlayer == null) ? (CommandUtils.GetSource(sender) instanceof DoAsCommandSender ? ((DoAsCommandSender) CommandUtils.GetSource(sender)).getUserIdent() : null) : UserIdent.get(senderPlayer);
        return ident;
    }

    public static boolean hasPermission(CommandSource sender, String perm)
    {
        try
        {
            if (sender.getEntityOrException() instanceof PlayerEntity)
                return APIRegistry.perms.checkPermission(getServerPlayer(sender), perm);
            else
                return true;
        }
        catch (CommandSyntaxException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    public static void checkPermission(CommandSource sender,String perm) throws CommandException
    {
        if (sender != null && !hasPermission(sender, perm))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
    }
    
    public WorldPoint getSenderPoint(CommandSource sender)
    {
        CommandSource s = sender != null ? sender : null;//server;
        BlockPos pos = new BlockPos(s.getPosition().x, s.getPosition().y, s.getPosition().z);
        return new WorldPoint(s.getLevel(), pos);
    }
}
