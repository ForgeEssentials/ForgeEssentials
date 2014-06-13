package com.forgeessentials.core.commands;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.OutputHandler;

public abstract class ForgeEssentialsCommandBase extends CommandBase
{
	@Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par1)
    {
        return true;
    }

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
			processCommandBlock((TileEntityCommandBlock) var1, var2);
		}
		else
		{
			processCommandConsole(var1, var2);
		}
	}

	public void processCommandPlayer(EntityPlayer sender, String[] args){}

	/**
	 * Override is optional.
	 */
	public void processCommandBlock(TileEntityCommandBlock block, String[] args)
	{
		processCommandConsole(block, args);
	}

	public void processCommandConsole(ICommandSender sender, String[] args){};

	// ---------------------------
	// command usage
	// ---------------------------

	@Override
	public abstract String getCommandUsage(ICommandSender sender);

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
	 * @param sender
	 * Object that issued the command
	 */
	public void error(ICommandSender sender)
	{
		this.error(sender, getCommandUsage(sender));
	}

	/**
	 * Prints an error message to the sender of the command.
	 * @param sender
	 * Object that issued the command
	 * @param message
	 * Error message
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
		String perm = getCommandPerm();
		if (perm == null)
			return true;
		else
			return APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(player, perm));
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args){return null;}

	// permissions
	
	public abstract String getCommandPerm();

	public abstract RegGroup getReggroup();

	/*
	 * Helper methods
	 */
	
	/**
	 * Parse int with support for relative int.
	 * @param sender
	 * @param string
	 * @param relativeStart
	 * @return
	 */
	public static int parseInt(ICommandSender sender, String string, double relativeStart)
    {
        if (string.startsWith("~"))
        {
            string = string.substring(1);
            return (int) (relativeStart + parseInt(sender, string));
        }
        else
        {
            return parseInt(sender, string);
        }
    }

	@Override
	public int compareTo(Object o) {
		if (o instanceof ICommand)
		{
			return this.compareTo((ICommand) o);
		}
		else return 0;
	}

	
    
}
