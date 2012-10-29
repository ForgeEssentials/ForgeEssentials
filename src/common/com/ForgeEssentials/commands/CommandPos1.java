package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.MathHelper;

public class CommandPos1 extends CommandBase {

	@Override
	public String getCommandName() {
		return "pos1";
	}
	
	public List getCommandAliases()
    {
		return Arrays.asList(new String[] { "p1" });
    }

	@Override
	public void processCommand(ICommandSender var1, String[] var2)
	{
		try
		{
			EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
			Point point = new Point(MathHelper.floor_double(ep.posX), MathHelper.floor_double(ep.posZ), MathHelper.floor_double(ep.posZ));
			Point.setPlayerPoint1(ep, point);
			this.getCommandSenderAsPlayer(var1).addChatMessage("Pos1 set to: " + point.x + ", " + point.y + ", " + point.z);
		}
		catch (Exception e)
		{
			this.getCommandSenderAsPlayer(var1).addChatMessage("Pos1 Command Failed!(Unknown Reason)");
		}
	}

}
