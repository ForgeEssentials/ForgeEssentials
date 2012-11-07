package com.ForgeEssentials.commands;

import com.ForgeEssentials.ConsoleInfo;
import com.ForgeEssentials.OutputHandler;
import com.ForgeEssentials.PlayerInfo;
import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.WorldServer;

public class CommandRedo extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "redo";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2)
	{
		if (var1 instanceof EntityPlayer)
		{
			EntityPlayer player = this.getCommandSenderAsPlayer(var1);
			PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
			
			if (!info.canRedo())
			{
				OutputHandler.chatError(player, "Nothing to redo!");
				return;
			}
			
			info.getBackupForRedo().loadAreaAfter(player.worldObj);
			OutputHandler.chatConfirmation(player, "Redo Succesful");
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
			
			if (!ConsoleInfo.instance.canRedo())
			{
				OutputHandler.SOP("Nothing to Redo!");
				return;
			}
			
			ConsoleInfo.instance.getBackupForRedo().loadAreaAfter(world);
			OutputHandler.SOP("Undo Succesful");
		}
	}

}
