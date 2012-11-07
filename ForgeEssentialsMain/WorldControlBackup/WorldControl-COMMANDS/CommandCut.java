package com.ForgeEssentials.commands;

import com.ForgeEssentials.ConsoleInfo;
import com.ForgeEssentials.OutputHandler;
import com.ForgeEssentials.PlayerInfo;
import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.BlueprintBlock;
import com.ForgeEssentials.WorldControl.CopyArea;
import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.WorldServer;

public class CommandCut extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "cut";
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

			Point[] points = AreaBase.getAlignedPoints(info.getPoint1(), info.getPoint2());
			BackupArea back = new BackupArea();

			for (int x = points[0].x; x < points[1].x; x++)
				for (int y = points[0].y; y < points[1].y; y++)
					for (int z = points[0].z; z < points[1].z; z++)
					{
						back.addBlockBefore(BlueprintBlock.loadFromWorld(player.worldObj, x, y, z));
						player.worldObj.setBlock(x, y, z, 0);
						back.addBlockAfter(BlueprintBlock.loadFromWorld(player.worldObj, x, y, z));
					}

			info.addBackup(back);

			OutputHandler.chatConfirmation(player, "Blocks Cut");
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
				OutputHandler.SOP("No world with name '" + var2[0] + "' exists");
				return;
			}

			ConsoleInfo.instance.copy = new CopyArea(world, ConsoleInfo.instance.getSelection());

			Point[] points = AreaBase.getAlignedPoints(ConsoleInfo.instance.getPoint1(), ConsoleInfo.instance.getPoint2());
			BackupArea back = new BackupArea();

			for (int x = points[0].x; x < points[1].x; x++)
				for (int y = points[0].y; y < points[1].y; y++)
					for (int z = points[0].z; z < points[1].z; z++)
					{
						back.addBlockBefore(BlueprintBlock.loadFromWorld(world, x, y, z));
						world.setBlock(x, y, z, 0);
						back.addBlockAfter(BlueprintBlock.loadFromWorld(world, x, y, z));
					}
			ConsoleInfo.instance.addBackup(back);

			OutputHandler.SOP("Blocks Cut");
		}
	}

}
