package com.ForgeEssentials.WorldControl.commands;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.TileEntityCommandBlock;

import com.ForgeEssentials.OutputHandler;
import com.ForgeEssentials.WorldControl.WorldControl;
import com.ForgeEssentials.commands.ForgeEssentialsCommandBase;

public abstract class WorldControlCommandBase extends ForgeEssentialsCommandBase
{

	@Override
	public final String getCommandName()
	{
		if (WorldControl.useExtraSlash)
			return "/"+getName();
		else
			return getName();
	}
	
	public abstract String getName();
	
	@Override
	public String getUsageConsole()
	{
		// almost never called.
		return "/"+getCommandName();
	}

	@Override
	public String getUsageCommandBlock(TileEntityCommandBlock block)
	{
		// almost never called.
		return "/"+getCommandName();
	}

	@Override
	public abstract String getUsagePlayer(EntityPlayer player);

	@Override
	public void processCommandBlock(TileEntityCommandBlock block, String[] args)
	{
		// most probably never used.
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		OutputHandler.SOP("You cannot use the \""+getCommandName()+"\" command from the console");
	}
	
	public static int[] interpretIDAndMetaFromString(String msg)
	{
		int ID;
		int meta = 0;
		
		// try checking if its just an ID
		try
		{
			ID = Integer.parseInt(msg);
			return new int[] {ID, meta};
		}
		catch(NumberFormatException e)
		{
			//do nothing. continue checking.
		}
		
		// perhaps the ID:Meta format
		try
		{
			if (msg.contains(":"))
			{
				String[] pair = msg.split(":", 2);
				ID = Integer.parseInt(pair[0]);
				meta = Integer.parseInt(pair[1]);
				
				return new int[] {ID, meta};
			}
		}
		catch(NumberFormatException e)
		{
			//do nothing. continue checking.
		}
		
		// TODO: add name checking.
		
		return new int[] {0, 0};
	}

}
