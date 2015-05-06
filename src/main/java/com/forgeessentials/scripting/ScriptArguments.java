package com.forgeessentials.scripting;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.scripting.ScriptParser.MissingPlayerException;
import com.forgeessentials.scripting.ScriptParser.ScriptArgument;
import com.forgeessentials.scripting.ScriptParser.SyntaxException;
import com.forgeessentials.util.FunctionHelper;

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

    public static final Pattern ARGUMENT_PATTERN = Pattern.compile("@\\{?(\\w+)\\}?");

    public static String process(String text)
    {
        return process(text, null, null);
    }

    public static String process(String text, ICommandSender sender)
    {
        return process(text, sender, null);
    }

    public static String process(String text, ICommandSender sender, List<?> args)
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

    public static ScriptArgument player = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            return sender.getCommandSenderName();
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
    };

    public static ScriptArgument y = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Integer.toString((int) ((EntityPlayerMP) sender).posY);
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
    };

    public static ScriptArgument xd = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Double.toString(((EntityPlayerMP) sender).posX);
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
    };

    public static ScriptArgument zd = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Double.toString(((EntityPlayerMP) sender).posZ);
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
    };

    public static ScriptArgument health = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Float.toString(((EntityPlayerMP) sender).getHealth());
        }
    };

    public static ScriptArgument food = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            return Integer.toString(((EntityPlayerMP) sender).getFoodStats().getFoodLevel());
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
    };

    public static ScriptArgument tps = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            return new DecimalFormat("#").format(FunctionHelper.getTPS());
        }
    };

    public static ScriptArgument realTime = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            return FunctionHelper.getCurrentTimeString();
        }
    };

    public static ScriptArgument realDate = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            return FunctionHelper.getCurrentDateString();
        }
    };

    public static ScriptArgument worldTime = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            return new DecimalFormat("#").format(MinecraftServer.getServer().getEntityWorld().getWorldTime());
        }
    };

    public static ScriptArgument noOfPlayersOnline = new ScriptArgument() {
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
            }
            return "" + online;
        }
    };

    public static ScriptArgument serverUptime = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
            int secsIn = (int) (rb.getUptime() / 1000);
            return FunctionHelper.parseTime(secsIn);
        }
    };

    public static ScriptArgument uniquePlayers = new ScriptArgument() {
        @Override
        public String process(ICommandSender sender)
        {
            return Integer.toString(APIRegistry.perms.getServerZone().getKnownPlayers().size());
        }
    };

    static
    {
        registerAll();
        add("p", player);
    }

}
