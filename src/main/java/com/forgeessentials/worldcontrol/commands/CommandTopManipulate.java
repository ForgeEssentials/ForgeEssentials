package com.forgeessentials.worldcontrol.commands;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.util.BackupArea;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.AreaSelector.Point;
import com.forgeessentials.util.tasks.TaskRegistry;
import com.forgeessentials.worldcontrol.TickTasks.TickTaskTopManipulator;

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

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
