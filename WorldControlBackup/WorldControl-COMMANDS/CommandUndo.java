package com.ForgeEssentials.commands;

import com.ForgeEssentials.ConsoleInfo;
import com.ForgeEssentials.OutputHandler;
import com.ForgeEssentials.PlayerInfo;
import com.ForgeEssentials.WorldControl.FunctionHandler;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.WorldServer;

public class CommandUndo extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "undo";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2)
	{		 
		if (var1 instanceof EntityPlayer)
		{
			// get PlayerInfo
			EntityPlayer player = this.getCommandSenderAsPlayer(var1);
			PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
			
			// if the backupID is less than 0. error.
			// that means either no actions have been done, or all possible actions have been undone.
			if (!info.canUndo())
			{
				OutputHandler.chatError(player, "Nothing to undo!");
				return;
			}
			
			// everythign working.. fine..    actually undo the backup.
			info.getBackupForUndo().loadAreaBefore(player.worldObj);
			
			// print confirmation.
			OutputHandler.chatConfirmation(player, "Undo Succesful");
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
			
			if (!ConsoleInfo.instance.canUndo())
			{
				OutputHandler.SOP("Nothing to undo!");
				return;
			}
			
			ConsoleInfo.instance.getBackupForUndo().loadAreaBefore(world);
			OutputHandler.SOP("Undo Succesful");
			
			OutputHandler.debug("1");
		}
		 
	}

}