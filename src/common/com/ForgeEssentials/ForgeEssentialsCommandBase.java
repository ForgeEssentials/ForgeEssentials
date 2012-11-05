package com.ForgeEssentials;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.TileEntityCommandBlock;

public abstract class ForgeEssentialsCommandBase extends CommandBase
{
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
	
	public abstract void processCommandBlock(TileEntityCommandBlock block, String[] args);
	
	public abstract void processCommandConsole(ICommandSender sender, String[] args);
	
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
	
	public abstract String getUsageCommandBlock(TileEntityCommandBlock block);
	
	public abstract String getUsagePlayer(EntityPlayer player);

}
