package com.forgeessentials.scripting;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.scripting.ScriptParser.MissingPlayerException;
import com.forgeessentials.scripting.ScriptParser.ScriptArgument;

public final class ScriptArguments {

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

    static
    {
        registerAll();
        add("p", player);
    }

}
