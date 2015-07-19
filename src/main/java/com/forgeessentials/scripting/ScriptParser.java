package com.forgeessentials.scripting;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.util.output.LoggingHandler;

public class ScriptParser
{

    public static interface ScriptMethod
    {

        public boolean process(ICommandSender sender, String[] args) throws CommandException;

        public String getHelp();

    }

    public static interface ScriptArgument
    {

        public String process(ICommandSender sender) throws ScriptException;

        public String getHelp();

    }

    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("@(\\w+)(.*)");

    public static String[] processArguments(ICommandSender sender, String[] actionArgs, List<String> args)
    {
        for (int i = 0; i < actionArgs.length; i++)
        {
            Matcher matcher = ARGUMENT_PATTERN.matcher(actionArgs[i]);
            if (!matcher.matches())
                continue;
            String modifier = matcher.group(1).toLowerCase();
            String rest = matcher.group(2);

            ScriptArgument argument = ScriptArguments.get(modifier);
            if (argument != null)
            {
                actionArgs[i] = argument.process(sender) + rest;
            }
            else
            {
                try
                {
                    int idx = Integer.parseInt(modifier);
                    if (args == null || idx >= args.size())
                        throw new SyntaxException("Missing argument @%d", idx);
                    actionArgs[i] = args.get(idx) + rest;
                }
                catch (NumberFormatException e)
                {
                    throw new SyntaxException("Unknown argument modifier \"%s\"", modifier);
                }
            }
        }
        return actionArgs;
    }

    public static void run(List<String> script) throws CommandException
    {
        run(script, null);
    }

    public static void run(List<String> script, ICommandSender sender) throws CommandException
    {
        run(script, sender, null);
    }

    public static boolean run(List<String> script, ICommandSender sender, List<String> args) throws CommandException
    {
        for (String action : script)
            if (!run(action, sender, args))
                return false;
        return true;
    }

    public static boolean run(String action, ICommandSender sender, List<String> argumentValues) throws CommandException
    {
        String[] args = action.split(" ", 2);
        String cmd = args[0].toLowerCase();
        args = args.length > 1 ? args[1].split(" ") : new String[0];
        args = processArguments(sender, args, argumentValues);

        if (cmd.length() > 1 && cmd.charAt(0) == '/')
        {
            // Run command
            cmd = cmd.substring(1);
            boolean ignorePermissions = cmd.equals("p") || cmd.equals("feperm");
            ICommand mcCommand = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(cmd);
            mcCommand.processCommand(ignorePermissions ? MinecraftServer.getServer() : sender, args);
        }
        else if (cmd.length() > 2 && cmd.charAt(0) == '?' && cmd.charAt(1) == '/')
        {
            // Run command silently (execution won't fail if command fails)
            cmd = cmd.substring(2);
            boolean ignorePermissions = cmd.equals("p") || cmd.equals("feperm");
            ICommand mcCommand = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(cmd);
            try
            {
                mcCommand.processCommand(ignorePermissions ? MinecraftServer.getServer() : sender, args);
            }
            catch (CommandException e)
            {
                LoggingHandler.felog.info(String.format("Silent script command /%s %s failed: %s", cmd, StringUtils.join(args, " "), e.getMessage()));
            }
        }
        else if (cmd.length() > 2 && cmd.charAt(0) == '$' && cmd.charAt(1) == '/')
        {
            // Run command as server
            cmd = cmd.substring(2);
            ICommand mcCommand = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(cmd);
            try
            {
                mcCommand.processCommand(MinecraftServer.getServer(), args);
            }
            catch (CommandException e)
            {
                LoggingHandler.felog.info(String.format("Silent script command /%s %s failed: %s", cmd, StringUtils.join(args, " "), e.getMessage()));
            }
        }
        else
        {
            boolean canFail = false;
            if (cmd.length() > 1 && cmd.charAt(0) == '?')
            {
                canFail = true;
                cmd = cmd.substring(1);
            }
            ScriptMethod method = ScriptMethods.get(cmd);
            if (method == null)
                throw new SyntaxException("Unknown script method \"%s\"", cmd);
            return method.process(sender, args) | canFail;
        }
        return true;
    }

    public static class ScriptException extends RuntimeException
    {

        public ScriptException()
        {
            super();
        }

        public ScriptException(String message)
        {
            super(message);
        }

        public ScriptException(String message, Object... args)
        {
            super(String.format(message, args));
        }

    }

    public static class SyntaxException extends ScriptException
    {

        public SyntaxException(String message, Object... args)
        {
            super(message, args);
        }

    }

    public static class MissingPlayerException extends ScriptException
    {

        public MissingPlayerException()
        {
            super("Missing player for @player argument");
        }

    }

    public static class MissingPermissionException extends ScriptException
    {

        public final String permission;

        public MissingPermissionException(String permission, String message)
        {
            super(message);
            this.permission = permission;
        }

        public MissingPermissionException(String permission, String message, Object... args)
        {
            super(message, args);
            this.permission = permission;
        }

    }

}
