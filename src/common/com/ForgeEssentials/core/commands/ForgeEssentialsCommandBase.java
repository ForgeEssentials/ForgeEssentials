package com.ForgeEssentials.core.commands;

import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.permissions.PermissionsHandler;
import com.ForgeEssentials.permissions.PermQueryArea;
import com.ForgeEssentials.permissions.PermQueryPlayer;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.TileEntityCommandBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;

public abstract class ForgeEssentialsCommandBase extends CommandBase
{
	public ForgeEssentialsCommandBase()
	{
		super();
		PermissionsHandler.registerPermission(this.getCommandPerm(), true);
	}
	
	// ---------------------------
	// processing command
	// ---------------------------

	@Override
	public final void processCommand(ICommandSender var1, String[] var2)
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
	public final String getCommandUsage(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
		{
			String usage;
			try
			{
				usage = getSyntaxPlayer((EntityPlayer) sender) + " " + getInfoPlayer((EntityPlayer) sender);
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
				usage = getSyntaxConsole() + " " + getInfoConsole();
			} catch (NullPointerException e)
			{
				usage = "Not usable by console";
			}
			return usage;
		}
	}

	public abstract String getSyntaxConsole();

	public String getSyntaxCommandBlock(TileEntityCommandBlock block)
	{
		return "/" + getCommandName();
	}

	public abstract String getSyntaxPlayer(EntityPlayer player);

	public abstract String getInfoConsole();

	public abstract String getInfoPlayer(EntityPlayer player);

	// ---------------------------
	// permissions
	// ---------------------------

	public final boolean canCommandSenderUseCommand(ICommandSender sender)
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
	 * returns false by default. Override if you wanna change that.
	 */
	public boolean canCommandBlockUseCommand(TileEntityCommandBlock block)
	{
		return false;
	}

	public abstract boolean canPlayerUseCommand(EntityPlayer player);
	
	public void error(ICommandSender sender)
	{
		String usage = this.getCommandUsage(sender);
		
		if (sender instanceof EntityPlayer)
			OutputHandler.chatError((EntityPlayer) sender, usage);
		else
			sender.sendChatToPlayer(usage);
	}
	
	public boolean checkCommandPerm(EntityPlayer player)
	{
		return PermissionsHandler.checkPermAllowed(new PermQueryPlayer(player, getCommandPerm()));
	}
	
	public abstract String getCommandPerm();

}
