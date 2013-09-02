package com.ForgeEssentials.WorldControl.commands;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskTopManipulator;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.tasks.TaskRegistry;

public class CommandTopManipulate extends WorldControlCommandBase
{

	private String						name;
	private TickTaskTopManipulator.Mode	manipulateMode;

	public CommandTopManipulate(String cmdName, TickTaskTopManipulator.Mode mode)
	{
		super(false);
		name = cmdName;
		manipulateMode = mode;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		if (args.length == 1 || args.length == 3)
		{
			PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
			if (info.getSelection() == null)
			{
				OutputHandler.chatError(player, Localization.get(Localization.ERROR_NOSELECTION));
				return;
			}
			int radius = -1;
			Point effectPosition = null;

			try
			{
				radius = Integer.parseInt(args[0]);
			}
			catch (Exception e)
			{
				error(player);
				radius = -1;
			}

			if (args.length == 1)
			{
				effectPosition = new Point((int) player.posX - 1, (int) player.posY, (int) player.posZ);
			}
			else
			{
				int x;
				int z;

				try
				{
					x = Integer.parseInt(args[1]);
					z = Integer.parseInt(args[2]);

					effectPosition = new Point(x, 0, z);
				}
				catch (Exception e)
				{
					error(player);
				}
			}

			if (radius != -1 && effectPosition != null)
			{
				BackupArea back = new BackupArea();
				// For some reason, player.posX is out.

				TaskRegistry.registerTask(new TickTaskTopManipulator(player, back, effectPosition, radius, manipulateMode));
			}
			ChatUtils.sendMessage(player, "Working on " + name + ".");
		}
		else
		{
			error(player);
		}
	}

}
