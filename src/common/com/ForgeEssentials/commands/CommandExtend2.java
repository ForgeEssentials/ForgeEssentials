package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandExtend2 extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "extend2";
	}

	public List getCommandAliases()
	{
		return Arrays.asList(new String[] { "ex2", "ext2" });
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2)
	{
		try
		{
			if (var2.length == 3)
			{
				EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
				Selection select = Selection.getPlayerSelection(ep);
				Point point = select.getEnd2();

				int x = Integer.parseInt(var2[0]);
				int y = Integer.parseInt(var2[1]);
				int z = Integer.parseInt(var2[2]);
				point.add(new Point(x, y, z));
				Point.setPlayerPoint1(ep, point);
				ep.addChatMessage("Selection 1 extended by: " + x + ", " + y + ", " + z + " to: " + point.x + ", " + point.y + ", " + point.z);
			}
			else
			{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Extend2 Command Failed(Try /extend2 <X> <Y> <Z>");
			}
		}
		catch (NumberFormatException e)
		{
			this.getCommandSenderAsPlayer(var1).addChatMessage("Extend2 Command Failed(Try /extend2 <X> <Y> <Z>");
		}
	}

}
