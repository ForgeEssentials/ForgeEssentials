package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandMiniChunk extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "minichunk";
	}

	public List getCommandAliases()
	{
		return Arrays.asList(new String[] { "mch", "mchunk" });
	}

	public int toChunk(double num)
	{
		int nume = (int) num;
		int temp = (int) (nume / 16);
		return temp * 16;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2)
	{
		try
		{
			EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
			Point.setPlayerPoint1(ep, new Point(toChunk(ep.posX), toChunk(ep.posY), toChunk(ep.posZ)));
			Point.setPlayerPoint2(ep, new Point(toChunk(ep.posX+16), toChunk(ep.posY+16), toChunk(ep.posZ+16)));
			this.getCommandSenderAsPlayer(var1).addChatMessage("Selected MiniChunk");
		}
		catch (Exception e)
		{
			this.getCommandSenderAsPlayer(var1).addChatMessage("MiniChunk Command Failed!(Unknown Reason)");
		}
	}

}
