package com.ForgeEssentials.core.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public abstract class ForgeEssentialsCommandBase extends CommandBase
{
	public boolean				enableCmdBlock	= true;
	public boolean				enableConsole	= true;
	public boolean				enablePlayer	= true;

	public ArrayList<String>	aliasList		= new ArrayList();

	// ---------------------------
	// configuration interaction
	// ---------------------------

	/**
	 * Override if you want configuration interaction.
	 * @param config
	 * @param category
	 */
	public void doConfig(Configuration config, String category)
	{

	}

	@Override
	public List getCommandAliases()
	{
		return aliasList;
	}

	public String[] getDefaultAliases()
	{
		return new String[] {};
	}

	public boolean usefullCmdBlock()
	{
		return this.canConsoleUseCommand();
	}

	public boolean usefullPlayer()
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

	public abstract void processCommandPlayer(EntityPlayer sender, String[] args);

	/**
	 * Override is optional.
	 */
	public void processCommandBlock(TileEntityCommandBlock block, String[] args)
	{
		processCommandConsole(block, args);
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
			}
			catch (NullPointerException e)
			{
				usage = "Not usable by player";
			}
			return usage;
		}
		else if (sender instanceof TileEntityCommandBlock)
		{
			return getSyntaxCommandBlock((TileEntityCommandBlock) sender);
		}
		else
		{
			String usage;
			try
			{
				usage = getCommandSyntax(sender) + " " + getCommandInfo(sender);
			}
			catch (NullPointerException e)
			{
				usage = "Not usable by console";
			}
			return usage;
		}
	}

	public String getCommandInfo(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
		{
			return getInfoPlayer((EntityPlayer) sender);
		}
		else
		{
			return getInfoConsole();
		}
	}

	public String getCommandSyntax(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
		{
			return getSyntaxPlayer((EntityPlayer) sender);
		}
		else
		{
			return getSyntaxConsole();
		}
	}

	public String getSyntaxConsole()
	{
		if (canConsoleUseCommand())
		{
			return Localization.get("command." + getCommandName() + ".syntax.console");
		}
		return null;
	}

	public String getSyntaxCommandBlock(TileEntityCommandBlock block)
	{
		return "/" + getCommandName();
	}

	public String getSyntaxPlayer(EntityPlayer player)
	{
		if (canPlayerUseCommand(player))
		{
			return Localization.get("command." + getCommandName() + ".syntax.player");
		}
		return null;
	}

	public String getInfoConsole()
	{
		if (canConsoleUseCommand())
		{
			return Localization.get("command." + getCommandName() + ".info.console");
		}
		return null;
	}

	public String getInfoPlayer(EntityPlayer player)
	{
		if (canPlayerUseCommand(player))
		{
			return Localization.get("command." + getCommandName() + ".info.player");
		}
		return null;
	}

	// ---------------------------
	// permissions
	// ---------------------------

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
		{
			if (!enablePlayer)
				return false;
			else
				return canPlayerUseCommand((EntityPlayer) sender);
		}
		else if (sender instanceof TileEntityCommandBlock)
		{
			if (!enableCmdBlock)
				return false;
			else
				return canCommandBlockUseCommand((TileEntityCommandBlock) sender);
		}
		else
		{
			if (!enableConsole)
				return false;
			else
				return canConsoleUseCommand();
		}
	}

	public abstract boolean canConsoleUseCommand();

	/**
	 * returns false by default. Override if you want to change that.
	 */
	public boolean canCommandBlockUseCommand(TileEntityCommandBlock block)
	{
		return false;
	}

	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		// This will make the /help menu only display allowed commands.
		return checkCommandPerm(player);
	}

	/**
	 * Simply prints a usage message to the sender of the command.
	 * 
	 * @param sender
	 * Object that issued the command
	 */
	public void error(ICommandSender sender)
	{
		this.error(sender, getCommandUsage(sender));
	}

	/**
	 * Prints an error message to the sender of the command.
	 * 
	 * @param sender
	 * Object that issued the command
	 * @param message
	 * Error message
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

	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return FMLCommonHandler.instance().getSidedDelegate().getServer().getPossibleCompletions(sender, args[args.length - 1]);

	}

	public abstract String getCommandPerm();

}
