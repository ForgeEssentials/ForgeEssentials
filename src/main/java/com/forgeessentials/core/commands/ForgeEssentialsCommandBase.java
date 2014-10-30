package com.forgeessentials.core.commands;

import java.util.List;

import com.forgeessentials.permissions.core.PermissionEventHandler;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;
import net.minecraftforge.server.CommandHandlerForge;

public abstract class ForgeEssentialsCommandBase extends CommandBase {

    // ---------------------------
    // processing command
    // ---------------------------

    @Override
    public void processCommand(ICommandSender var1, String[] var2)
    {
        if (var1 instanceof EntityPlayer)
        {
            processCommandPlayer((EntityPlayer) var1, var2);
        }
        else if (var1 instanceof TileEntityCommandBlock)
        {
            processCommandBlock((CommandBlockLogic) var1, var2);
        }
        else
        {
            processCommandConsole(var1, var2);
        }
    }

    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
    }

    /**
     * Override is optional.
     */
    public void processCommandBlock(CommandBlockLogic block, String[] args)
    {
        processCommandConsole(block, args);
    }

    // ---------------------------
    // command usage
    // ---------------------------

    public void processCommandConsole(ICommandSender sender, String[] args)
    {
    }

    // ---------------------------
    // permissions
    // ---------------------------

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
        {
            return canPlayerUseCommand((EntityPlayer) sender);
        }
        else if (sender instanceof TileEntityCommandBlock)
        {
            return canCommandBlockUseCommand((TileEntityCommandBlock) sender);
        }
        else
        {
            return canConsoleUseCommand();
        }
    }

    public abstract boolean canConsoleUseCommand();

    /**
     * returns canConsoleUseCommand() by default. Override if you want to change that.
     */
    public boolean canCommandBlockUseCommand(TileEntityCommandBlock block)
    {
        return canConsoleUseCommand();
    }

    public boolean canPlayerUseCommand(EntityPlayer player)
    {
        return checkCommandPermission(player);
    }

    /**
     * Simply prints a usage message to the sender of the command.
     *
     * @param sender
     *            Object that issued the command
     */
    public void error(ICommandSender sender)
    {
        this.error(getCommandUsage(sender));
    }

    /**
     * Prints an error message to the sender of the command.
     *
     * @param sender
     *            Object that issued the command
     * @param message
     *            Error message
     */
    public void error(String message)
    {
        throw new CommandException(message);
    }

    /**
     * Registers this command
     * 
     * @param permLevel
     */
    public void register(RegisteredPermValue permLevel)
    {
        CommandHandlerForge.registerCommand(this, getPermissionNode(), permLevel);
    }

    public boolean checkCommandPermission(EntityPlayer player)
    {
        String perm = getPermissionNode();
        if (perm != null && !PermissionsManager.checkPermission(player, perm))
        {
            //PermissionEventHandler.permissionDeniedMessage(player);
            return false;
        }
        return true;
    }

    // permissions

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return null;
    }

    /**
     * Get the permission node
     */
    public abstract String getPermissionNode();

    public abstract RegisteredPermValue getDefaultPermission();

    @Override
    public int compareTo(Object o)
    {
        if (o instanceof ICommand)
        {
            return this.compareTo((ICommand) o);
        }
        else
        {
            return 0;
        }
    }

    // ---------------------------
    // utility
    // ---------------------------

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
