package com.ForgeEssentials.questioner;

import java.util.ArrayList;
import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public abstract class QuestionerCommandBase extends CommandBase
{
	public boolean				enableCmdBlock	= true;
	public boolean				enableConsole	= true;
	public boolean				enablePlayer	= true;

	public ArrayList<String>	aliasList		= new ArrayList<String>();

	// ---------------------------
	// config interaction
	// ---------------------------

	/**
	 * Override if you want config interaction.
	 * 
	 * @param config
	 * @param category
	 */
	public void doConfig(Configuration config, String category)
	{

	}

	@Override
	public List<String> getCommandAliases()
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
			ChatUtils.sendMessage(sender, message);
		}
	}

	public boolean checkCommandPerm(EntityPlayer player)
	{
		return APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(player, getCommandPerm()));
	}

	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().getPossibleCommands(sender));
		}
		else
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		}
	}

	public abstract String getCommandPerm();

}
