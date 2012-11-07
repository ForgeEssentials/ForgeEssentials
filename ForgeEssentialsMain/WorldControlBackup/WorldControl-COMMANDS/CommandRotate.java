package com.ForgeEssentials.commands;

import com.ForgeEssentials.PlayerInfo;
import com.ForgeEssentials.WorldControl.CopyArea;
import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandRotate extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "rotate";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2)
	{
		try
		{
			CopyArea cpy = PlayerInfo.getPlayerInfo(this.getCommandSenderAsPlayer(var1).username).copy;
			int id = 0;
			boolean rotateX = false;
			boolean rotateY = false;
			boolean rotateZ = false;
			if (var2.length == 4)
			{
				id = Integer.parseInt(var2[0]);
				rotateX = cpy.rotateX;
				rotateY = cpy.rotateY;
				rotateZ = cpy.rotateZ;
				if (Integer.parseInt(var2[1]) == 1)
				{
					rotateX = !rotateX;
				}
				if (Integer.parseInt(var2[2]) == 1)
				{
					rotateY = !rotateY;
				}
				if (Integer.parseInt(var2[3]) == 1)
				{
					rotateZ = !rotateZ;
				}
				cpy.rotateX = rotateX;
				cpy.rotateY = rotateY;
				cpy.rotateZ = rotateZ;
			}
			else if (var2.length == 3)
			{
				rotateX = cpy.rotateX;
				rotateY = cpy.rotateY;
				rotateZ = cpy.rotateZ;
				if (Integer.parseInt(var2[0]) == 1)
				{
					rotateX = !rotateX;
				}
				if (Integer.parseInt(var2[1]) == 1)
				{
					rotateY = !rotateY;
				}
				if (Integer.parseInt(var2[2]) == 1)
				{
					rotateZ = !rotateZ;
				}
				cpy.rotateX = rotateX;
				cpy.rotateY = rotateY;
				cpy.rotateZ = rotateZ;
				this.getCommandSenderAsPlayer(var1).addChatMessage("Rotated to: " + (rotateX ? 1 : 0) + ", " + (rotateY ? 1 : 0) + ", " + (rotateZ ? 1 : 0));
			}
			else
			{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Rotate Command Failed(Try /rotate (<id>) <isX> <isY> <isZ>)");
				return;
			}
		}
		catch (Exception e)
		{
			this.getCommandSenderAsPlayer(var1).addChatMessage("Rotate Command Failed!(Unknown Reason)");
		}
	}

}
