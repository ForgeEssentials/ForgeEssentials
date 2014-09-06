package com.forgeessentials.core.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;

import java.util.List;

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
     * returns canConsoleUseCommand() by default. Override if you want to change
     * that.
     */
    public boolean canCommandBlockUseCommand(TileEntityCommandBlock block)
    {
        return canConsoleUseCommand();
    }

    public boolean canPlayerUseCommand(EntityPlayer player)
    {
        // This will make the /help menu only display allowed commands.
        return checkCommandPerm(player);
    }

    /**
     * Simply prints a usage message to the sender of the command.
     *
     * @param sender Object that issued the command
     */
    public void error(ICommandSender sender)
    {
        this.error(sender, getCommandUsage(sender));
    }

    /**
     * Prints an error message to the sender of the command.
     *
     * @param sender  Object that issued the command
     * @param message Error message
     */
    public void error(ICommandSender sender, String message)
    {
        if (sender instanceof EntityPlayer)
        {
            OutputHandler.chatError(sender, message);
        }
        else
        {
            ChatUtils.sendMessage(sender, message);
        }
    }

    public boolean checkCommandPerm(EntityPlayer player)
    {
        String perm = getPermissionNode();
        if (perm == null)
        {
            return true;
        }
        else
        {
            return APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(player, perm));
        }
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

    public abstract RegGroup getReggroup();

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
