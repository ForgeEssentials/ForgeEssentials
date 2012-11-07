package com.ForgeEssentials.commands;

import com.ForgeEssentials.ConsoleInfo;
import com.ForgeEssentials.OutputHandler;
import com.ForgeEssentials.PlayerInfo;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.CopyArea;
import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.WorldServer;

public class CommandPaste extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "paste";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args)
	{
		if (commandSender instanceof EntityPlayer)
		{
			// get PlayerInfo
			EntityPlayer player = this.getCommandSenderAsPlayer(commandSender);
			PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
			
			if (info.copy == null)
			{
				OutputHandler.chatError(player, "Nothing Copied!");
				return;
			}
			
			Point point = info.getPoint1();
			boolean clear = false;
			
			switch(args.length)
			{
				case 2: clear = args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("clear");
				case 1:
					if (args[0].equalsIgnoreCase("point2") || args[0].equalsIgnoreCase("pnt2") || args[0].equalsIgnoreCase("p2") || args[0].equalsIgnoreCase("2"))
						point = info.getPoint2();
			}
			
			// actually do copying.
			BackupArea back = new BackupArea();
			info.copy.outputArea(player.worldObj, point, back, false);
			OutputHandler.chatConfirmation(player, "Blocks Pasted");
		}
		else
		{
			if (args.length < 1)
			{
				OutputHandler.SOP("No world specified");
				return;
			}
			
			WorldServer world = FunctionHandler.getWorldForName(args[0]);
			
			if (world == null)
			{
				OutputHandler.SOP("No world with name '"+args[0]+"' exists");
				return;
			}
			
			Point point = ConsoleInfo.instance.getPoint1();
			boolean clear = false;
			
			switch(args.length)
			{
				case 3: clear = args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("clear");
				case 2:
					if (args[0].equalsIgnoreCase("point2") || args[0].equalsIgnoreCase("pnt2") || args[0].equalsIgnoreCase("p2") || args[0].equalsIgnoreCase("2"))
						point = ConsoleInfo.instance.getPoint2();
			}
			
			if (ConsoleInfo.instance.copy == null)
				OutputHandler.SOP("Nothing Copied!");
			
			//ConsoleInfo.instance.copy = new CopyArea(world, ConsoleInfo.instance.getSelection());
			OutputHandler.SOP("Blocks Pasted");
		}
	}
}
