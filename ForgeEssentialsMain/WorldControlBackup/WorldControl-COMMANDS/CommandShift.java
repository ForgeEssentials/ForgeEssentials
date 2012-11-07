package com.ForgeEssentials.commands;

import com.ForgeEssentials.PlayerInfo;
import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandShift extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "shift";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2)
	{
		try
		{
			if (var2.length == 3)
			{
				EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
				int x = Integer.parseInt(var2[0]);
				int y = Integer.parseInt(var2[1]);
				int z = Integer.parseInt(var2[2]);
				Selection select = PlayerInfo.getPlayerInfo(ep.username).getSelection();
				select.shift(x, y, z);
				ep.addChatMessage("Shifted by: " + x + ", " + y + ", " + z);
			}
			else
			{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Shift Command Failed(Try /shift <X> <Y> <Z>");
			}
		}
		catch (Exception e)
		{
			this.getCommandSenderAsPlayer(var1).addChatMessage("Shift Command Failed!(Unknown Reason)");
		}
	}

}
