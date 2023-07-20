package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.FECommandParsingException;
import com.mojang.brigadier.context.ParsedCommandNode;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class CommandUtils
{
    public static ICommandSource GetSource(CommandSource source)
    {
        return ObfuscationReflectionHelper.getPrivateValue(CommandSource.class, source,
                "field_197041_c");
    }

    public static UserIdent parsePlayer(String name, CommandSource sender, boolean mustExist, boolean mustBeOnline)
            throws FECommandParsingException
    {
        UserIdent ident = UserIdent.get(name, sender, mustExist);
        if (mustExist && (ident == null || !ident.hasUuid()))
            throw new FECommandParsingException("Player %s not found", name);
        else if (mustBeOnline && !ident.hasPlayer())
            throw new FECommandParsingException("Player %s is not online", name);
        return ident;
    }

    public static class CommandInfo
    {

        protected String commandName;

        protected CommandSource source;

        protected List<String> commandRelativeArgs;

        protected String commandActualArgs;

        public String getCommandName()
        {
            return commandName;
        }

        public CommandSource getSource()
        {
            return source;
        }

        public List<String> getRelativeArgs()
        {
            return commandRelativeArgs;
        }

        public String getRelativeArgsString()
        {
            return commandRelativeArgs.isEmpty() ? "" : String.join(" ", commandRelativeArgs);
        }

        public String getActualArgsString()
        {
            return commandActualArgs;
        }

        public String getPermissionNode()
        {
            return commandName + (commandRelativeArgs.isEmpty() ? "" : "." + String.join(".", commandRelativeArgs));
        }
    }

    public static CommandInfo getCommandInfo(CommandEvent event)
    {
        CommandInfo info = new CommandInfo();
        info.source = event.getParseResults().getContext().getSource();
        try
        {
            info.commandName = event.getParseResults().getContext().getNodes().get(0).getNode().getName();
        }
        catch (IndexOutOfBoundsException e)
        {
            throw new RuntimeException(e);
        }
        info.commandRelativeArgs = new ArrayList<>();
        if (event.getParseResults().getContext().getNodes().size() > 1)
        {
            // System.out.println(event.getParseResults().getReader().getString());
            for (ParsedCommandNode<CommandSource> node : event.getParseResults().getContext().getNodes())
            {
                info.commandRelativeArgs.add(node.getNode().getName());
                // System.out.println(node.getNode().getName());
            }
            info.commandRelativeArgs.remove(0);
        }
        info.commandActualArgs = event.getParseResults().getReader().getString();
        while (info.commandActualArgs.startsWith("/"))
        {
            info.commandActualArgs = info.commandActualArgs.substring(1);
        }
        if (info.commandActualArgs.startsWith(info.commandName))
        {
            info.commandActualArgs = info.commandActualArgs.substring(info.commandName.length());
        }
        if (info.commandActualArgs.startsWith(" "))
        {
            info.commandActualArgs = info.commandActualArgs.substring(1);
        }
        return info;
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

            if (!Double.isFinite(d0))
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

    public static long parseLong(String input) throws FECommandParsingException
    {
        try
        {
            return Long.parseLong(input);
        }
        catch (NumberFormatException e)
        {
            throw new FECommandParsingException("Invalid number: %s", input);
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
            throw new NumberFormatException(var2.getMessage());
        }
    }

    public static int parseInt(String input, int min) throws NumberFormatException
    {
        return parseInt(input, min, Integer.MAX_VALUE);
    }

    /*
     * Argument to pass: int to test min value max value
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

    // ------------------------------------------------------------
    // Utilities

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

    public static boolean hasPermissionNOC(CommandSource sender, String perm)
    {
        if (sender.getEntity() != null && sender.getEntity() instanceof PlayerEntity)
            return APIRegistry.perms.checkPermission(getServerPlayer(sender), perm);
        ICommandSource source = GetSource(sender);
        return source instanceof MinecraftServer || source instanceof CommandBlockLogic;
    }

    @Deprecated
    public static TextComponent getChatComponentFromNthArg(String[] args, int index)
    {
        TextComponent itextcomponent = new StringTextComponent("");

        for (int i = index; i < args.length; ++i)
        {
            if (i > index)
            {
                itextcomponent.append(" ");
            }

            itextcomponent.append(net.minecraftforge.common.ForgeHooks.newChatWithLinks(args[i])); // Forge: links for
                                                                                                   // messages

        }

        return itextcomponent;
    }

    public static boolean doesStringStartWith(String original, String region)
    {
        return region.regionMatches(true, 0, original, 0, original.length());
    }

    public static ServerPlayerEntity getServerPlayer(CommandSource source)
    {
        return (source.getEntity() instanceof ServerPlayerEntity) ? (ServerPlayerEntity) source.getEntity() : null;
    }

    public static final Pattern timeFormatPattern = Pattern.compile("(\\d+)(\\D+)?");

    private static double mcHour = 1000;
    private static double mcMinute = 1000.0 / 60;
    private static double mcSecond = 1000.0 / 60 / 60;

    /**
     * Parses a Time string in Minecraft time format.
     * 
     * @return Long
     * @throws FECommandParsingException
     */
    public static Long mcParseTimeReadable(String mcTime) throws FECommandParsingException
    {

        Matcher m = timeFormatPattern.matcher(mcTime);
        if (!m.find())
        {
            throw new FECommandParsingException("Invalid time format: %s", mcTime);
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
                throw new FECommandParsingException("Invalid time format: %s", mcTime);
            }
        }
        return Math.round(resultPart);
    }

    public static long parseTimeReadable(String time) throws FECommandParsingException
    {
        Matcher m = timeFormatPattern.matcher(time);
        if (!m.find())
        {
            throw new FECommandParsingException("Invalid time format: %s", time);
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
                    throw new FECommandParsingException("Invalid time format: %s", time);
                }
            }

            result += resultPart;
        }
        while (m.find());

        return result;
    }

    public static UserIdent getIdent(ServerPlayerEntity senderPlayer)
    {
        return (senderPlayer == null) ? null : UserIdent.get(senderPlayer);
    }

    public static UserIdent getIdent(CommandSource sender)
    {
        ServerPlayerEntity senderPlayer = getServerPlayer(sender);
        return (senderPlayer == null) ? (CommandUtils.GetSource(sender) instanceof DoAsCommandSender
                ? ((DoAsCommandSender) CommandUtils.GetSource(sender)).getUserIdent()
                : null) : UserIdent.get(senderPlayer);
    }

    public WorldPoint getSenderPoint(CommandSource sender)
    {
        BlockPos pos = new BlockPos(sender.getPosition().x, sender.getPosition().y, sender.getPosition().z);
        return new WorldPoint(sender.getLevel(), pos);
    }
}
