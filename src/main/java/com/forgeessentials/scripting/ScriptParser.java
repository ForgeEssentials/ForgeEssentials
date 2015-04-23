package com.forgeessentials.scripting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

public class ScriptParser {

    public static interface ScriptMethod {
        public boolean process(ICommandSender sender, String[] args);
    }

    public static interface ScriptArgument {
        public String process(ICommandSender sender);
    }

    public static Map<String, ScriptMethod> scriptMethods = new HashMap<>();

    public static Map<String, ScriptArgument> scriptArguments = new HashMap<>();

    static
    {
        ScriptMethod function = new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                OutputHandler.chatConfirmation(sender, StringUtils.join(args, " "));
                return true;
            }
        };
        scriptMethods.put("echo", function);
        scriptMethods.put("confirm", function);

        scriptMethods.put("notify", new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                OutputHandler.chatNotification(sender, StringUtils.join(args, " "));
                return true;
            }
        });
        scriptMethods.put("warn", new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                OutputHandler.chatWarning(sender, StringUtils.join(args, " "));
                return true;
            }
        });
        scriptMethods.put("error", new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                OutputHandler.chatError(sender, StringUtils.join(args, " "));
                return true;
            }
        });
        scriptMethods.put("fail", new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                OutputHandler.chatError(sender, StringUtils.join(args, " "));
                return false;
            }
        });
        scriptMethods.put("confirmall", new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                OutputHandler.broadcast(OutputHandler.confirmation(StringUtils.join(args, " ")));
                return true;
            }
        });
        scriptMethods.put("notifyall", new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                OutputHandler.broadcast(OutputHandler.notification(StringUtils.join(args, " ")));
                return true;
            }
        });
        scriptMethods.put("warnall", new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                OutputHandler.broadcast(OutputHandler.warning(StringUtils.join(args, " ")));
                return true;
            }
        });
        scriptMethods.put("errorall", new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                OutputHandler.broadcast(OutputHandler.error(StringUtils.join(args, " ")));
                return true;
            }
        });
        scriptMethods.put("failall", new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                OutputHandler.broadcast(OutputHandler.error(StringUtils.join(args, " ")));
                return false;
            }
        });
        scriptMethods.put("permcheck", new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                if (sender == null || !(sender instanceof EntityPlayerMP))
                    throw new MissingPlayerException();
                if (args.length < 1)
                    throw new ScriptException("Missing argument for permcheck");
                if (!APIRegistry.perms.checkUserPermission(new UserIdent((EntityPlayerMP) sender), args[0]))
                    throw new MissingPermissionException(args[0], args.length > 1 ? StringUtils.join(Arrays.copyOfRange(args, 1, args.length)) : "");
                return true;
            }
        });
        scriptMethods.put("permchecksilent", new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                if (sender == null || !(sender instanceof EntityPlayerMP))
                    return false;
                if (args.length < 1)
                    throw new ScriptException("Invalid argument count for permcheck");
                if (!APIRegistry.perms.checkUserPermission(new UserIdent((EntityPlayerMP) sender), args[0]))
                    return false;
                return true;
            }
        });
        scriptMethods.put("!permcheck", new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                if (sender == null || !(sender instanceof EntityPlayerMP))
                    throw new MissingPlayerException();
                if (args.length < 1)
                    throw new ScriptException("Missing argument for permcheck");
                if (APIRegistry.perms.checkUserPermission(new UserIdent((EntityPlayerMP) sender), args[0]))
                    throw new MissingPermissionException(args[0], args.length > 1 ? StringUtils.join(Arrays.copyOfRange(args, 1, args.length)) : "");
                return true;
            }
        });
        scriptMethods.put("!permchecksilent", new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                if (sender == null || !(sender instanceof EntityPlayerMP))
                    return false;
                if (args.length != 1)
                    throw new ScriptException("Invalid argument count for permcheck");
                if (APIRegistry.perms.checkUserPermission(new UserIdent((EntityPlayerMP) sender), args[0]))
                    return false;
                return true;
            }
        });

        // Script arguments

        scriptArguments.put("player", new ScriptArgument() {
            @Override
            public String process(ICommandSender sender)
            {
                return sender.getCommandSenderName();
            }
        });
        scriptArguments.put("x", new ScriptArgument() {
            @Override
            public String process(ICommandSender sender)
            {
                if (!(sender instanceof EntityPlayerMP))
                    throw new MissingPlayerException();
                return Integer.toString((int) ((EntityPlayerMP) sender).posX);
            }
        });
        scriptArguments.put("y", new ScriptArgument() {
            @Override
            public String process(ICommandSender sender)
            {
                if (!(sender instanceof EntityPlayerMP))
                    throw new MissingPlayerException();
                return Integer.toString((int) ((EntityPlayerMP) sender).posY);
            }
        });
        scriptArguments.put("z", new ScriptArgument() {
            @Override
            public String process(ICommandSender sender)
            {
                if (!(sender instanceof EntityPlayerMP))
                    throw new MissingPlayerException();
                return Integer.toString((int) ((EntityPlayerMP) sender).posZ);
            }
        });
        scriptArguments.put("xd", new ScriptArgument() {
            @Override
            public String process(ICommandSender sender)
            {
                if (!(sender instanceof EntityPlayerMP))
                    throw new MissingPlayerException();
                return Double.toString(((EntityPlayerMP) sender).posX);
            }
        });
        scriptArguments.put("yd", new ScriptArgument() {
            @Override
            public String process(ICommandSender sender)
            {
                if (!(sender instanceof EntityPlayerMP))
                    throw new MissingPlayerException();
                return Double.toString(((EntityPlayerMP) sender).posY);
            }
        });
        scriptArguments.put("zd", new ScriptArgument() {
            @Override
            public String process(ICommandSender sender)
            {
                if (!(sender instanceof EntityPlayerMP))
                    throw new MissingPlayerException();
                return Double.toString(((EntityPlayerMP) sender).posZ);
            }
        });
        scriptArguments.put("dim", new ScriptArgument() {
            @Override
            public String process(ICommandSender sender)
            {
                if (!(sender instanceof EntityPlayerMP))
                    throw new MissingPlayerException();
                return Integer.toString(((EntityPlayerMP) sender).dimension);
            }
        });
        scriptArguments.put("health", new ScriptArgument() {
            @Override
            public String process(ICommandSender sender)
            {
                if (!(sender instanceof EntityPlayerMP))
                    throw new MissingPlayerException();
                return Float.toString(((EntityPlayerMP) sender).getHealth());
            }
        });
        scriptArguments.put("food", new ScriptArgument() {
            @Override
            public String process(ICommandSender sender)
            {
                if (!(sender instanceof EntityPlayerMP))
                    throw new MissingPlayerException();
                return Integer.toString(((EntityPlayerMP) sender).getFoodStats().getFoodLevel());
            }
        });
        scriptArguments.put("saturation", new ScriptArgument() {
            @Override
            public String process(ICommandSender sender)
            {
                if (!(sender instanceof EntityPlayerMP))
                    throw new MissingPlayerException();
                return Float.toString(((EntityPlayerMP) sender).getFoodStats().getSaturationLevel());
            }
        });
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

            ScriptArgument argument = scriptArguments.get(modifier);
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
                        throw new MissingArgumentException("Missing argument @%d", idx);
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

    public static void run(List<String> script)
    {
        run(script, null);
    }

    public static void run(List<String> script, ICommandSender sender)
    {
        run(script, sender, null);
    }

    public static boolean run(List<String> script, ICommandSender sender, List<String> args)
    {
        for (String action : script)
            if (!run(action, sender, args))
                return false;
        return true;
    }

    public static boolean run(String action, ICommandSender sender, List<String> argumentValues)
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
                OutputHandler.felog.info(String.format("Silent script command /%s %s failed: %s", cmd, StringUtils.join(args, " "), e.getMessage()));
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
                OutputHandler.felog.info(String.format("Silent script command /%s %s failed: %s", cmd, StringUtils.join(args, " "), e.getMessage()));
            }
        }
        else
        {
            ScriptMethod method = scriptMethods.get(cmd);
            if (method == null)
                throw new ScriptException("Unknown script method \"%s\"", cmd);
            return method.process(sender, args);
        }
        return true;
    }

    public static class ScriptException extends RuntimeException {

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

    public static class MissingArgumentException extends ScriptException {

        public MissingArgumentException(String message, Object... args)
        {
            super(message, args);
        }

    }

    public static class SyntaxException extends ScriptException {

        public SyntaxException(String message, Object... args)
        {
            super(message, args);
        }

    }

    public static class MissingPlayerException extends ScriptException {

        public MissingPlayerException()
        {
            super("Missing player for @player argument");
        }

    }

    public static class MissingPermissionException extends ScriptException {

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
