package com.ForgeEssentials.core.commands;

import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.PermissionsHandler;
import com.ForgeEssentials.permission.query.PermQueryArea;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.TileEntityCommandBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;

public abstract class ForgeEssentialsCommandBase extends CommandBase
{
	// ---------------------------
	// processing command
	// ---------------------------

	@Override
	public void processCommand(ICommandSender var1, String[] var2)
	{
		if (var1 instanceof EntityPlayer)
			processCommandPlayer((EntityPlayer) var1, var2);
		else if (var1 instanceof TileEntityCommandBlock)
			processCommandBlock((TileEntityCommandBlock) var1, var2);
		else
			processCommandConsole(var1, var2);
	}

	public abstract void processCommandPlayer(EntityPlayer sender, String[] args);

	/**
	 * Override is optional. does nothing by default.
	 */
	public void processCommandBlock(TileEntityCommandBlock block, String[] args)
	{
		// do nothing.
	}

	public abstract void processCommandConsole(ICommandSender sender, String[] args);

	// ---------------------------
	// command usage
	// ---------------------------

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
		{
			String usage;
			try
			{
				usage = "/" + getCommandName() + " " + getCommandSyntax(sender) + " " + getCommandInfo(sender);
			} catch (NullPointerException e)
			{
				usage = "Not usable by player";
			}
			return usage;
		} else if (sender instanceof TileEntityCommandBlock)
			return getSyntaxCommandBlock((TileEntityCommandBlock) sender);
		else
		{
			String usage;
			try
			{
				usage = getCommandSyntax(sender) + " " + getCommandInfo(sender);
			} catch (NullPointerException e)
			{
				usage = "Not usable by console";
			}
			return usage;
		}
	}
	
	public String getCommandInfo(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
			return getInfoPlayer((EntityPlayer) sender);
		else
			return getInfoConsole();
	}
	
	public String getCommandSyntax(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
			return getSyntaxPlayer((EntityPlayer) sender);
		else
			return getSyntaxConsole();
	}

	public String getSyntaxConsole()
	{
		if (canConsoleUseCommand())
			return "/" + getCommandName() + Localization.get("command." + getCommandName() + ".syntax.console");
		return null;
	}

	public String getSyntaxCommandBlock(TileEntityCommandBlock block)
	{
		return "/" + getCommandName();
	}

	public String getSyntaxPlayer(EntityPlayer player)
	{
		if (canPlayerUseCommand(player))
			return "/" + getCommandName() + Localization.get("command." + getCommandName() + ".syntax.player");
		return null;
	}

	public String getInfoConsole()
	{
		if (canConsoleUseCommand())
			return "/" + getCommandName() + Localization.get("command." + getCommandName() + ".info.console");
		return null;
	}

	public String getInfoPlayer(EntityPlayer player)
	{
		if (canPlayerUseCommand(player))
			return "/" + getCommandName() + Localization.get("command." + getCommandName() + ".info.player");
		return null;
	}

	// ---------------------------
	// permissions
	// ---------------------------

	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
			return canPlayerUseCommand((EntityPlayer) sender);
		else if (sender instanceof TileEntityCommandBlock)
			return canCommandBlockUseCommand((TileEntityCommandBlock) sender);
		else
			return canConsoleUseCommand();
	}

	public abstract boolean canConsoleUseCommand();

	/**
	 * returns false by default. Override if you want to change that.
	 */
	public boolean canCommandBlockUseCommand(TileEntityCommandBlock block)
	{
		return false;
	}

	public abstract boolean canPlayerUseCommand(EntityPlayer player);

	/**
	 * Simply prints a usage message to the sender of the command.
	 * @param sender Object that issued the command
	 */
	public void error(ICommandSender sender)
	{
		this.error(sender, this.getCommandUsage(sender));
	}
	
	/**
	 * Prints an error message to the sender of the command.
	 * @param sender Object that issued the command
	 * @param message Error message
	 */
	public void error(ICommandSender sender, String message)
	{
		if (sender instanceof EntityPlayer)
		{
			OutputHandler.chatError((EntityPlayer) sender, message);
		}
		else
		{
			sender.sendChatToPlayer(message);
		}
	}

	public boolean checkCommandPerm(EntityPlayer player)
	{
		return PermissionsAPI.checkPermAllowed(new PermQueryPlayer(player, getCommandPerm()));
	}

	public abstract String getCommandPerm();

}
