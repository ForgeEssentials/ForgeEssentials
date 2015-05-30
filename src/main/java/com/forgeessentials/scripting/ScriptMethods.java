package com.forgeessentials.scripting;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.scripting.ScriptParser.MissingPermissionException;
import com.forgeessentials.scripting.ScriptParser.MissingPlayerException;
import com.forgeessentials.scripting.ScriptParser.ScriptException;
import com.forgeessentials.scripting.ScriptParser.ScriptMethod;
import com.forgeessentials.scripting.ScriptParser.SyntaxException;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.ServerUtil;
import com.google.common.collect.ImmutableMap;

public final class ScriptMethods
{

    public static Map<String, ScriptMethod> scriptMethods = new HashMap<>();

    public static void add(String name, ScriptMethod argument)
    {
        if (scriptMethods.containsKey(name))
            throw new RuntimeException(String.format("Script method name @%s already registered", name));
        scriptMethods.put(name, argument);
    }

    public static ScriptMethod get(String name)
    {
        return scriptMethods.get(name);
    }

    public static Map<String, ScriptMethod> getAll()
    {
        return ImmutableMap.copyOf(scriptMethods);
    }

    private static void registerAll()
    {
        try
        {
            for (Field field : ScriptMethods.class.getDeclaredFields())
                if (ScriptMethod.class.isAssignableFrom(field.getType()) && Modifier.isStatic(field.getModifiers()))
                    add(field.getName().toLowerCase(), (ScriptMethod) field.get(null));
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static final ScriptMethod confirm = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            OutputHandler.chatConfirmation(sender, StringUtils.join(args, " "));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send confirmation message to the player";
        }
    };

    public static final ScriptMethod notify = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            OutputHandler.chatNotification(sender, StringUtils.join(args, " "));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send notification message to the player";
        }
    };

    public static final ScriptMethod warn = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            OutputHandler.chatWarning(sender, StringUtils.join(args, " "));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send warning message to the player";
        }
    };

    public static final ScriptMethod error = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            OutputHandler.chatError(sender, StringUtils.join(args, " "));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send error message to the player";
        }
    };

    public static final ScriptMethod fail = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            OutputHandler.chatError(sender, StringUtils.join(args, " "));
            return false;
        }

        @Override
        public String getHelp()
        {
            return "Send error message to the player and fail script execution";
        }
    };

    public static final ScriptMethod confirmall = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            OutputHandler.broadcast(OutputHandler.confirmation(StringUtils.join(args, " ")));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send confirmation message to all players";
        }
    };

    public static final ScriptMethod notifyall = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            OutputHandler.broadcast(OutputHandler.notification(StringUtils.join(args, " ")));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send notification message to all players";
        }
    };

    public static final ScriptMethod warnall = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            OutputHandler.broadcast(OutputHandler.warning(StringUtils.join(args, " ")));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send warning message to all players";
        }
    };

    public static final ScriptMethod errorall = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            OutputHandler.broadcast(OutputHandler.error(StringUtils.join(args, " ")));
            return true;
        }

        @Override
        public String getHelp()
        {
            return "Send error message to all players";
        }
    };

    public static final ScriptMethod failall = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            OutputHandler.broadcast(OutputHandler.error(StringUtils.join(args, " ")));
            return false;
        }

        @Override
        public String getHelp()
        {
            return "Send error message to all players and fail script execution";
        }
    };

    public static final ScriptMethod permcheck = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            if (args.length < 1)
                throw new SyntaxException("Missing argument for permcheck");
            if (!APIRegistry.perms.checkUserPermission(UserIdent.get((EntityPlayerMP) sender), args[0]))
                throw new MissingPermissionException(args[0], args.length > 1 ? StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ") : "");
            return true;
        }

        @Override
        public String getHelp()
        {
            return "`permcheck <perm> [error message...]`  \nPermission check (with error message)";
        }
    };

    public static final ScriptMethod permchecksilent = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (!(sender instanceof EntityPlayerMP))
                return false;
            if (args.length < 1)
                throw new SyntaxException("Invalid argument count for permcheck");
            if (!APIRegistry.perms.checkUserPermission(UserIdent.get((EntityPlayerMP) sender), args[0]))
                return false;
            return true;
        }

        @Override
        public String getHelp()
        {
            return "`permchecksilent <perm>`  \nPermission check (without error message)";
        }
    };

    public static final ScriptMethod teleport = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (args.length < 1)
                throw new SyntaxException("Missing player argument for teleport");
            if (args.length < 2)
                throw new SyntaxException("Missing target argument for teleport");
            UserIdent player = UserIdent.get(args[1], sender);
            if (!player.hasPlayer())
                return false;
            if (args.length == 2)
            {
                UserIdent target = UserIdent.get(args[2], sender);
                if (!target.hasPlayer())
                    return false;
                TeleportHelper.teleport(player.getPlayerMP(), new WarpPoint(target.getPlayerMP()));
            }
            else if (args.length == 4)
            {
                Integer x = ServerUtil.tryParseInt(args[1]);
                Integer y = ServerUtil.tryParseInt(args[2]);
                Integer z = ServerUtil.tryParseInt(args[3]);
                if (x == null || y == null || z == null)
                    return false;
                EntityPlayerMP p = player.getPlayerMP();
                TeleportHelper.teleport(p, new WarpPoint(p.dimension, x, y, z, p.cameraPitch, p.cameraYaw));
            }
            else if (args.length == 5)
            {
                Integer x = ServerUtil.tryParseInt(args[1]);
                Integer y = ServerUtil.tryParseInt(args[2]);
                Integer z = ServerUtil.tryParseInt(args[3]);
                Integer dim = ServerUtil.tryParseInt(args[4]);
                if (x == null || y == null || z == null || dim == 0 || !DimensionManager.isDimensionRegistered(dim))
                    return false;
                EntityPlayerMP p = player.getPlayerMP();
                TeleportHelper.teleport(p, new WarpPoint(dim, x, y, z, p.cameraPitch, p.cameraYaw));
            }
            else
                throw new SyntaxException("Incorrect number of arguments");
            return true;
        }

        @Override
        public String getHelp()
        {
            return "`teleport <player> <to-player>`  \n`teleport <player> <x> <y> <z> [dim]`";
        }
    };

    public static final ScriptMethod pay = new ScriptMethod() {
        @Override
        public boolean process(ICommandSender sender, String[] args)
        {
            if (!(sender instanceof EntityPlayerMP))
                throw new MissingPlayerException();
            if (args.length < 1)
                throw new SyntaxException("Missing amount for pay command");
            if (args.length > 2)
                throw new SyntaxException("Too many arguments");
            try
            {
                long amount = Long.parseLong(args[0]);
                Wallet src = APIRegistry.economy.getWallet((EntityPlayerMP) sender);
                Wallet dst = null;
                if (args.length == 2)
                {
                    UserIdent dstIdent = UserIdent.get(args[1], sender);
                    if (!dstIdent.hasUuid())
                        throw new ScriptException("Player %s not found", args[1]);
                    dst = APIRegistry.economy.getWallet(dstIdent);
                }
                if (!src.withdraw(amount))
                {
                    OutputHandler.chatError(sender, Translator.translate("You can't afford that!"));
                    return false;
                }
                if (dst != null)
                    dst.add(amount);
                return true;
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }

        @Override
        public String getHelp()
        {
            return "`pay <amount> [to-player]`  \nMake the player pay some amount of money and fail, if he can't afford it";
        }
    };

    static
    {
        registerAll();

        add("echo", confirm);

        add("!permcheck", new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                if (!(sender instanceof EntityPlayerMP))
                    throw new MissingPlayerException();
                if (args.length < 1)
                    throw new SyntaxException("Missing argument for permcheck");
                if (APIRegistry.perms.checkUserPermission(UserIdent.get((EntityPlayerMP) sender), args[0]))
                    throw new MissingPermissionException(args[0], args.length > 1 ? StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ") : "");
                return true;
            }

            @Override
            public String getHelp()
            {
                return "`!permcheck <perm> [error message...]`  \nNegated permission check (with error message)";
            }
        });

        add("!permchecksilent", new ScriptMethod() {
            @Override
            public boolean process(ICommandSender sender, String[] args)
            {
                if (!(sender instanceof EntityPlayerMP))
                    return false;
                if (args.length != 1)
                    throw new SyntaxException("Invalid argument count for permcheck");
                if (APIRegistry.perms.checkUserPermission(UserIdent.get((EntityPlayerMP) sender), args[0]))
                    return false;
                return true;
            }

            @Override
            public String getHelp()
            {
                return "`!permchecksilent <perm>`  \nNegated permission check (without error message)";
            }
        });
    }

}
