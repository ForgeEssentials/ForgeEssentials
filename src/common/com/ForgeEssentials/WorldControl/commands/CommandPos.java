package com.ForgeEssentials.WorldControl.commands;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.WorldControl.FunctionHelper;
import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.PlayerInfo;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MovingObjectPosition;

public class CommandPos extends WorldControlCommandBase
{
	private int type;
	
	public CommandPos(int type)
	{
		this.type = type;
	}

	@Override
	public String getName()
	{
		return "pos"+type;
	}

	@Override
	public String getUsagePlayer(EntityPlayer player)
	{
		return "/"+getCommandName()+" [<x> <y> <z]";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		int x, y, z;
		
		if (args.length > 0)
		{
			if (args.length < 3)
			{
				OutputHandler.chatError(player, "That won't work. try "+getUsagePlayer(player));
				return;
			}
			
			try
			{
				x = Integer.parseInt(args[0]);
				y = Integer.parseInt(args[1]);
				z = Integer.parseInt(args[2]);
			}
			catch(NumberFormatException e)
			{
				OutputHandler.chatError(player, "That won't work. try "+getUsagePlayer(player));
				return;
			}
			
			if (type == 1)
				PlayerInfo.getPlayerInfo(player.username).setPoint1(new Point(x, y, z));
			else
				PlayerInfo.getPlayerInfo(player.username).setPoint2(new Point(x, y, z));
			
			OutputHandler.chatConfirmation(player, "Pos"+type+" set to "+x+", "+y+", "+z);
			return;
		}
		
		MovingObjectPosition mop = FunctionHelper.getPlayerLookingSpot(player);
		x = mop.blockX;
		y = mop.blockY;
		z = mop.blockZ;
		
		
		if (type == 1)
			PlayerInfo.getPlayerInfo(player.username).setPoint1(new Point(x, y, z));
		else
			PlayerInfo.getPlayerInfo(player.username).setPoint2(new Point(x, y, z));
		
		OutputHandler.chatConfirmation(player, "Pos"+type+" set to "+x+", "+y+", "+z);
		return;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		// check permissions
		return true;
	}

}
