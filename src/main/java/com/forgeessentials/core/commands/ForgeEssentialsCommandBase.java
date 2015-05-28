package com.forgeessentials.core.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fe.server.CommandHandlerForge;
import net.minecraftforge.permissions.PermissionContext;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.misc.TranslatedCommandException;

public abstract class ForgeEssentialsCommandBase extends CommandBase
{

    public List<String> aliases = new ArrayList<String>();

    // ------------------------------------------------------------
    // Command alias

    @Override
    public List<String> getCommandAliases()
    {
        return aliases;
    }

    /**
     * Returns a list of default aliases, that will be added to the configuration on first run
     */
    public String[] getDefaultAliases()
    {
        return new String[] {};
    }

    public void setAliases(String[] aliases)
    {
        setAliases(Arrays.asList(aliases));
    }

    public void setAliases(List<String> aliases)
    {
        this.aliases = aliases;
    }

    // ------------------------------------------------------------
    // Command processing

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (sender instanceof EntityPlayerMP)
        {
            processCommandPlayer((EntityPlayerMP) sender, args);
        }
        else if (sender instanceof CommandBlockLogic)
        {
            processCommandBlock((CommandBlockLogic) sender, args);
        }
        else
        {
            processCommandConsole(sender, args);
        }
    }

    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        throw new TranslatedCommandException("Command %s is not implemented for players", getCommandName());
    }

    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        throw new TranslatedCommandException("Command %s is not implemented for console", getCommandName());
    }

    public void processCommandBlock(CommandBlockLogic block, String[] args)
    {
        processCommandConsole(block, args);
    }

    // ------------------------------------------------------------
    // Command usage

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        if (!(sender instanceof EntityPlayer) && !canConsoleUseCommand())
            return canCommandSenderUseCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
        if (!checkCommandPermission(sender))
            return false;
        return true;
    }

    protected static boolean canCommandSenderUseCommandException(String msg)
    {
        // Find out if, if canCommandSenderUseCommand was called from within executeCommand method of CommandHandler.
        // Only if it's called from there, it's safe to throw an exception.
        final String className = CommandHandler.class.getName();
        final String methodName = "executeCommand";
        for (StackTraceElement s : Thread.currentThread().getStackTrace())
            if (s.getClassName().equals(className))
            {
                if (s.getClassName().equals(methodName))
                    throw new TranslatedCommandException(msg);
                break;
            }
        // Just return false instead of an exception
        return false;
    }

    public abstract boolean canConsoleUseCommand();

    // ------------------------------------------------------------
    // Permissions

    /**
     * Registers this command and it's permission node
     */
    public void register()
    {
        if (getPermissionNode() != null && getDefaultPermission() != null)
        {
            CommandHandlerForge.registerCommand(this, getPermissionNode(), getDefaultPermission());
        }
        else
        {
            ((CommandHandler) MinecraftServer.getServer().getCommandManager()).registerCommand(this);
        }
        registerExtraPermissions();
    }

    /**
     * Registers additional permissions
     */
    public void registerExtraPermissions()
    {
        /* do nothing */
    }

    /**
     * Get the permission node
     */
    public abstract String getPermissionNode();

    /**
     * Check, if the sender has permissions to use this command
     */
    public boolean checkCommandPermission(ICommandSender sender)
    {
        if (getPermissionNode() == null || getPermissionNode().isEmpty())
            return true;
        return PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender).setCommand(this), getPermissionNode());
    }

    /**
     * Get the default permission level needed to use this command
     */
    public abstract RegisteredPermValue getDefaultPermission();

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

    public static List<String> getListOfStringsMatchingLastWord(String[] args, Collection<String> possibleMatches)
    {
        return getListOfStringsMatchingLastWord(args[args.length - 1], possibleMatches);
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

    public static List<String> getListOfStringsMatchingLastWord(String[] args, String... possibleMatches)
    {
        return getListOfStringsMatchingLastWord(args[args.length - 1], possibleMatches);
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

    @Override
    public int compareTo(Object o)
    {
        if (o instanceof ICommand)
            return this.compareTo((ICommand) o);
        return 0;
    }

    /**
     * Parse int with support for relative int.
     *
     * @param sender
     * @param string
     * @param relativeStart
     * @return
     */
    public static int parseInt(ICommandSender sender, String string, int relativeStart)
    {
        if (string.startsWith("~"))
        {
            string = string.substring(1);
            return relativeStart + parseInt(sender, string);
        }
        else
        {
            return parseInt(sender, string);
        }
    }

    /**
     * Parse double with support for relative values.
     *
     * @param sender
     * @param string
     * @param relativeStart
     * @return
     */
    public static double parseDouble(ICommandSender sender, String string, double relativeStart)
    {
        if (string.startsWith("~"))
        {
            string = string.substring(1);
            return relativeStart + parseInt(sender, string);
        }
        else
        {
            return parseInt(sender, string);
        }
    }

}
