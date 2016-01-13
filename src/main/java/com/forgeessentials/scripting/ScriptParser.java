package com.forgeessentials.scripting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.util.DoAsCommandSender;
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

    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("@([\\w*]+)(.*)");

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
            else if (modifier.endsWith("*"))
            {
                try
                {
                    int idx = 0;
                    if (modifier.length() > 1)
                        idx = Integer.parseInt(modifier.substring(0, modifier.length() - 1));
                    if (args == null || idx >= args.size())
                        throw new SyntaxException("Missing argument @%d", idx);
                    List<String> newArgs = new ArrayList<>(Arrays.asList(actionArgs));
                    newArgs.remove(i);
                    for (int j = idx; j < args.size(); j++)
                        newArgs.add(i + j - idx, args.get(j));
                    actionArgs = newArgs.toArray(new String[newArgs.size()]);
                    actionArgs[actionArgs.length - 1] += rest;
                }
                catch (NumberFormatException e)
                {
                    throw new SyntaxException("Unknown argument modifier \"%s\"", modifier);
                }
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
        if (cmd.isEmpty())
            throw new SyntaxException("Could not handle script action \"%s\"", action);

        char c = cmd.charAt(0);
        switch (c)
        {
        case '/':
        case '$':
        case '?':
        case '*':
        {
            ICommandSender cmdSender = sender;
            if (cmd.equals("p") || cmd.equals("feperm"))
                cmdSender = MinecraftServer.getServer();

            boolean ignoreErrors = false;
            modifierLoop: while (true)
            {
                cmd = cmd.substring(1);
                switch (c)
                {
                case '$':
                    if (!(cmdSender instanceof DoAsCommandSender))
                        cmdSender = new DoAsCommandSender(APIRegistry.IDENT_SERVER, sender);
                    ((DoAsCommandSender) cmdSender).setIdent(APIRegistry.IDENT_SERVER);
                    break;
                case '?':
                    ignoreErrors = true;
                    break;
                case '*':
                    if (sender instanceof EntityPlayer)
                    {
                        if (!(cmdSender instanceof DoAsCommandSender))
                            cmdSender = new DoAsCommandSender(UserIdent.get((EntityPlayer) sender), sender);
                        ((DoAsCommandSender) cmdSender).setHideChatMessages(true);
                    }
                    else if (sender == null || sender instanceof MinecraftServer)
                    {
                        if (!(cmdSender instanceof DoAsCommandSender))
                            cmdSender = new DoAsCommandSender(APIRegistry.IDENT_SERVER, sender);
                        ((DoAsCommandSender) cmdSender).setHideChatMessages(true);
                    }
                    break;
                case '/':
                    break modifierLoop;
                default:
                    throw new SyntaxException("Could not handle script action \"%s\"", action);
                }
                c = cmd.charAt(0);
            }
            ICommand mcCommand = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(cmd);
            try
            {
                mcCommand.processCommand(cmdSender, args);
            }
            catch (CommandException e)
            {
                if (!ignoreErrors)
                    throw e;
                LoggingHandler.felog.info(String.format("Silent script command /%s %s failed: %s", cmd, StringUtils.join(args, " "), e.getMessage()));
            }
            return true;
        }
        default:
            boolean canFail = false;
            if (cmd.length() > 1 && cmd.charAt(0) == '?')
            {
                canFail = true;
                cmd = cmd.substring(1);
            }
            ScriptMethod method = ScriptMethods.get(cmd);
            if (method == null)
                throw new SyntaxException("Unknown script method \"%s\"", cmd);
            try
            {
                return method.process(sender, args) | canFail;
            }
            catch (NumberFormatException e)
            {
                throw new CommandException("Invalid number format: " + e.getMessage());
            }
        }
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

    public static class ScriptErrorException extends ScriptException
    {

        public ScriptErrorException()
        {
            super();
        }

        public ScriptErrorException(String message)
        {
            super(message);
        }

        public ScriptErrorException(String message, Object... args)
        {
            super(message, args);
        }

    }

    public static class MissingPlayerException extends ScriptErrorException
    {

        public MissingPlayerException()
        {
            super("Missing player for @player argument");
        }

    }

    public static class MissingPermissionException extends ScriptErrorException
    {

        public final String permission;

        public MissingPermissionException(String permission)
        {
            super();
            this.permission = permission;
        }

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
