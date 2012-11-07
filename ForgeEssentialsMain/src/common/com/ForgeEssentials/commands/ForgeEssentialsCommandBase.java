package com.ForgeEssentials.commands;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.TileEntityCommandBlock;

public abstract class ForgeEssentialsCommandBase extends CommandBase
{
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
	
	public abstract void processCommandPlayer(EntityPlayer player, String[] args);
	
	/**
	 * Override is optional. dos nothing by default.
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
			return getUsagePlayer((EntityPlayer) sender);
		else if (sender instanceof TileEntityCommandBlock)
			return getUsageCommandBlock((TileEntityCommandBlock) sender);
		else
			return getUsageConsole();
	}
	
	public abstract String getUsageConsole();
	
	public String getUsageCommandBlock(TileEntityCommandBlock block)
	{
		return "/"+getCommandName();
	}
	
	public abstract String getUsagePlayer(EntityPlayer player);
	
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

}
