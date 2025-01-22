package com.forgeessentials.scripting;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.chat.ChatConfig;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.scripting.ScriptParser.MissingPlayerException;
import com.forgeessentials.scripting.ScriptParser.ScriptArgument;
import com.forgeessentials.scripting.ScriptParser.ScriptException;
import com.forgeessentials.scripting.ScriptParser.SyntaxException;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.google.common.collect.ImmutableMap;

public final class ScriptArguments
{

    private static Map<String, ScriptArgument> scriptArguments = new HashMap<>();

    public static void add(String name, ScriptArgument argument)
    {
        if (scriptArguments.containsKey(name))
            throw new RuntimeException(String.format("Script argument name @%s already registered", name));
        scriptArguments.put(name, argument);
    }

    public static ScriptArgument get(String name)
    {
        return scriptArguments.get(name);
    }

    public static Map<String, ScriptArgument> getAll()
    {
        return ImmutableMap.copyOf(scriptArguments);
    }

    public static final Pattern ARGUMENT_PATTERN = Pattern.compile("@\\{?(\\w+)\\}?");

    public static String process(String text, ICommandSender sender) throws ScriptException
    {
        return process(text, sender, null);
    }

    public static String process(String text, ICommandSender sender, List<?> args) throws ScriptException
    {
        Matcher m = ARGUMENT_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find())
        {
            String modifier = m.group(1).toLowerCase();
            ScriptArgument argument = get(modifier);
            if (argument != null)
                m.appendReplacement(sb, argument.process(sender));
            else if (args == null)
                m.appendReplacement(sb, m.group());
            else
                try
                {
                    int idx = Integer.parseInt(modifier);
                    if (args == null || idx < 1 || idx > args.size())
                        throw new SyntaxException("Missing argument @%d", idx);
                    m.appendReplacement(sb, args.get(idx - 1).toString());
                }
                catch (NumberFormatException e)
                {
                    m.appendReplacement(sb, m.group());
                }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static String processSafe(String text, ICommandSender sender)
    {
        return processSafe(text, sender, null);
    }

    public static String processSafe(String text, ICommandSender sender, List<?> args)
    {
        Matcher m = ARGUMENT_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find())
        {
            String modifier = m.group(1).toLowerCase();
            ScriptArgument argument = get(modifier);
            if (argument != null)
            {
                try
                {
                    m.appendReplacement(sb, argument.process(sender));
                }
                catch (ScriptException e)
                {
                    m.appendReplacement(sb, m.group());
                }
            }
            else if (args == null)
                m.appendReplacement(sb, m.group());
            else
                try
                {
                    int idx = Integer.parseInt(modifier);
                    if (args == null || idx >= args.size())
                        throw new SyntaxException("Missing argument @%d", idx);
                    m.appendReplacement(sb, args.get(idx).toString());
                }
                catch (NumberFormatException e)
                {
                    m.appendReplacement(sb, m.group());
                }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static void registerAll()
    {
        try
        {
            for (Field field : ScriptArguments.class.getDeclaredFields())
                if (ScriptArgument.class.isAssignableFrom(field.getType()) && Modifier.isStatic(field.getModifiers()))
                    add(field.getName().toLowerCase(), (ScriptArgument) field.get(null));
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static ScriptArgument sender = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (sender == null)
                throw new MissingPlayerException();
            return sender.getName();
        }

        @Override
        public String getHelp()
        {
            return "Command sender name";
        }
    };

    public static ScriptArgument player = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (sender == null)
                throw new MissingPlayerException();
            return sender.getName();
        }

        @Override
        public String getHelp()
        {
            return "Player name";
        }
    };

    public static ScriptArgument uuid = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return ((EntityPlayerMP) sender).getPersistentID().toString();
        }

        @Override
        public String getHelp()
        {
            return "Player UUID";
        }
    };

    public static ScriptArgument x = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Integer.toString((int) ((EntityPlayerMP) sender).posX);
        }

        @Override
        public String getHelp()
        {
            return "Player X position (as integer)";
        }
    };

    public static ScriptArgument y = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Integer.toString((int) ((EntityPlayerMP) sender).posY);
        }

        @Override
        public String getHelp()
        {
            return "Player Y position (as integer)";
        }
    };

    public static ScriptArgument z = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Integer.toString((int) ((EntityPlayerMP) sender).posZ);
        }

        @Override
        public String getHelp()
        {
            return "Player Z position (as integer)";
        }
    };

    public static ScriptArgument xd = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Double.toString(((EntityPlayerMP) sender).posX);
        }

        @Override
        public String getHelp()
        {
            return "Player X position (as floating point number)";
        }
    };

    public static ScriptArgument yd = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Double.toString(((EntityPlayerMP) sender).posY);
        }

        @Override
        public String getHelp()
        {
            return "Player Y position (as floating point number)";
        }
    };

    public static ScriptArgument zd = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Double.toString(((EntityPlayerMP) sender).posZ);
        }

        @Override
        public String getHelp()
        {
            return "Player Z position (as floating point number)";
        }
    };

    public static ScriptArgument dim = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Integer.toString(((EntityPlayerMP) sender).dimension);
        }

        @Override
        public String getHelp()
        {
            return "Player dimension";
        }
    };

    public static ScriptArgument gm = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            if (((EntityPlayerMP) sender).theItemInWorldManager.getGameType().isCreative())
                return ChatConfig.gamemodeCreative;
            if (((EntityPlayerMP) sender).theItemInWorldManager.getGameType().isAdventure())
                return ChatConfig.gamemodeAdventure;
            return ChatConfig.gamemodeSurvival;
        }

        @Override
        public String getHelp()
        {
            return "Player gamemode";
        }
    };

    public static ScriptArgument health = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Float.toString(((EntityPlayerMP) sender).getHealth());
        }

        @Override
        public String getHelp()
        {
            return "Player health";
        }
    };

    public static ScriptArgument healthcolor = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            float health = ((EntityPlayerMP) sender).getHealth();
            if (health <= 6)
                return EnumChatFormatting.RED.toString();
            if (health < 16)
                return EnumChatFormatting.YELLOW.toString();
            return EnumChatFormatting.GREEN.toString();
        }

        @Override
        public String getHelp()
        {
            return "Insert color code based on player health";
        }
    };

    public static ScriptArgument hunger = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Integer.toString(((EntityPlayerMP) sender).getFoodStats().getFoodLevel());
        }

        @Override
        public String getHelp()
        {
            return "Player hunger level";
        }
    };

    public static ScriptArgument hungercolor = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            float hunger = ((EntityPlayerMP) sender).getFoodStats().getFoodLevel();
            if (hunger <= 6)
                return EnumChatFormatting.RED.toString();
            if (hunger < 12)
                return EnumChatFormatting.YELLOW.toString();
            return EnumChatFormatting.GREEN.toString();
        }

        @Override
        public String getHelp()
        {
            return "Insert color code based on player hunger level";
        }
    };

    public static ScriptArgument saturation = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Float.toString(((EntityPlayerMP) sender).getFoodStats().getSaturationLevel());
        }

        @Override
        public String getHelp()
        {
            return "Player (food) saturation level";
        }
    };

    public static ScriptArgument saturationcolor = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            float hunger = ((EntityPlayerMP) sender).getFoodStats().getSaturationLevel();
            if (hunger <= 0)
                return EnumChatFormatting.RED.toString();
            if (hunger <= 1.5)
                return EnumChatFormatting.YELLOW.toString();
            return EnumChatFormatting.GREEN.toString();
        }

        @Override
        public String getHelp()
        {
            return "Insert color code based on player saturation level";
        }
    };

    public static ScriptArgument zone = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return APIRegistry.perms.getServerZone().getZoneAt(new WorldPoint(((EntityPlayerMP) sender))).getName();
        }

        @Override
        public String getHelp()
        {
            return "Get name of the zone the player is in";
        }
    };

    public static ScriptArgument zoneId = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Integer.toString(APIRegistry.perms.getServerZone().getZoneAt(new WorldPoint(((EntityPlayerMP) sender))).getId());
        }

        @Override
        public String getHelp()
        {
            return "Get ID of the zone the player is in";
        }
    };

    public static ScriptArgument group = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            EntityPlayerMP _player = ((EntityPlayerMP) sender);
            return APIRegistry.perms.getServerZone().getPlayerGroups(UserIdent.get(_player)).first().getGroup();
        }

        @Override
        public String getHelp()
        {
            return "Get name of the zone the player is in";
        }
    };

    public static ScriptArgument timePlayed = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            EntityPlayerMP _player = ((EntityPlayerMP) sender);
            return ChatOutputHandler.formatTimeDurationReadable(PlayerInfo.get(_player).getTimePlayed() / 1000, true);
        }

        @Override
        public String getHelp()
        {
            return "Get total time a player played on the server" + "";
        }
    };

    public static ScriptArgument lastLogout = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            EntityPlayerMP _player = ((EntityPlayerMP) sender);
            return FEConfig.FORMAT_DATE_TIME.format(PlayerInfo.get(_player).getLastLogout());
        }

        @Override
        public String getHelp()
        {
            return "Get the time a player logged out last time";
        }
    };

    public static ScriptArgument lastLogin = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            EntityPlayerMP _player = ((EntityPlayerMP) sender);
            return FEConfig.FORMAT_DATE_TIME.format(PlayerInfo.get(_player).getLastLogin());
        }

        @Override
        public String getHelp()
        {
            return "Get the time a player logged in last time";
        }
    };

    public static ScriptArgument sinceLastLogout = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            EntityPlayerMP _player = ((EntityPlayerMP) sender);
            return ChatOutputHandler.formatTimeDurationReadable((new Date().getTime() - PlayerInfo.get(_player).getLastLogout().getTime()) / 1000, true);
        }

        @Override
        public String getHelp()
        {
            return "Get the time since a player logged out last time";
        }
    };

    public static ScriptArgument sinceLastLogin = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            EntityPlayerMP _player = ((EntityPlayerMP) sender);
            return ChatOutputHandler.formatTimeDurationReadable((new Date().getTime() - PlayerInfo.get(_player).getLastLogin().getTime()) / 1000, true);
        }

        @Override
        public String getHelp()
        {
            return "Get the time since a player logged in last time";
        }
    };

    public static ScriptArgument tps = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            return new DecimalFormat("#").format(Math.min(20, ServerUtil.getTPS()));
        }

        @Override
        public String getHelp()
        {
            return "Ticks per second";
        }
    };

    public static ScriptArgument realTime = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            return FEConfig.FORMAT_TIME.format(new Date());
        }

        @Override
        public String getHelp()
        {
            return "Current real time";
        }
    };

    public static ScriptArgument realDate = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            return FEConfig.FORMAT_DATE.format(new Date());
        }

        @Override
        public String getHelp()
        {
            return "Current real date";
        }
    };

    public static ScriptArgument worldTime = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            return new DecimalFormat("#").format(MinecraftServer.getServer().getEntityWorld().getWorldTime());
        }

        @Override
        public String getHelp()
        {
            return "Current MC world time";
        }
    };

    public static ScriptArgument worldTimeClock = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            try
            {
                FEConfig.FORMAT_TIME.setTimeZone(TimeZone.getTimeZone("US"));
                long ticks = MinecraftServer.getServer().getEntityWorld().getWorldTime();
                Date time = new Date(ticks * 1000 * 60 * 60 * 24 / 24000 + 1000 * 60 * 60 * 6);
                return FEConfig.FORMAT_TIME.format(time);
            }
            finally
            {
                FEConfig.FORMAT_TIME.setTimeZone(TimeZone.getDefault());
            }
        }

        @Override
        public String getHelp()
        {
            return "Current MC world time formatted as H:MM";
        }
    };

    public static ScriptArgument totalWorldTime = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            return new DecimalFormat("#").format(MinecraftServer.getServer().getEntityWorld().getTotalWorldTime());
        }

        @Override
        public String getHelp()
        {
            return "MC time passed since map creation";
        }
    };

    public static ScriptArgument serverUptime = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
            return ChatOutputHandler.formatTimeDurationReadable(rb.getUptime() / 1000, true);
        }

        @Override
        public String getHelp()
        {
            return "Time since server start";
        }
    };

    public static ScriptArgument onlinePlayers = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            int online = 0;
            try
            {
                online = MinecraftServer.getServer().getCurrentPlayerCount();
            }
            catch (Exception e)
            {
                /* do nothing */
            }
            return Integer.toString(online);
        }

        @Override
        public String getHelp()
        {
            return "Number of players that are online right now";
        }
    };

    public static ScriptArgument uniquePlayers = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            return Integer.toString(APIRegistry.perms.getServerZone().getKnownPlayers().size());
        }

        @Override
        public String getHelp()
        {
            return "Number of unique players on the server at all time";
        }
    };
	
	public static ScriptArgument exp = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Integer.toString(((EntityPlayerMP) sender).experienceLevel);
        }

        @Override
        public String getHelp()
        {
            return "Returns the expLevel of the command sender";
        }
    };
	
    //Should be removed (randi function replaces it better)
    @Deprecated
	public static ScriptArgument random = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            return Integer.toString(ForgeEssentials.rnd.nextInt(33554432) - 16777216);
        }

        @Override
        public String getHelp()
        {
            return "Returns a random integer between -16777216 and 16777215 inclusive. (Deprecated, may be removed in a later update.  Use expression functions instead.) ";
        }
    };

    static
    {
        registerAll();
        add("p", player);
    }

}
