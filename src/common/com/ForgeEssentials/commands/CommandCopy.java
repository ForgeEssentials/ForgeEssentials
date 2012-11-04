package com.ForgeEssentials.commands;

import com.ForgeEssentials.ConsoleInfo;
import com.ForgeEssentials.OutputHandler;
import com.ForgeEssentials.PlayerInfo;
import com.ForgeEssentials.WorldControl.CopyArea;
import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.WorldServer;

public class CommandCopy extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "copy";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2)
	{
		if (var1 instanceof EntityPlayer)
		{
			// get PlayerInfo
			EntityPlayer player = this.getCommandSenderAsPlayer(var1);
			PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
			
			// actually do copying.
			info.copy = new CopyArea(player.worldObj, info.getSelection());
			OutputHandler.chatConfirmation(player, "Blocks Copied");
		}
		else
		{
			if (var2.length < 1)
			{
				OutputHandler.SOP("No world specified");
				return;
			}
			
			WorldServer world = FunctionHandler.getWorldForName(var2[0]);
			
			if (world == null)
			{
				OutputHandler.SOP("No world with name '"+var2[0]+"' exists");
				return;
			}
			
			ConsoleInfo.instance.copy = new CopyArea(world, ConsoleInfo.instance.getSelection());
			OutputHandler.SOP("Blocks Copied");
		}
	}

}
