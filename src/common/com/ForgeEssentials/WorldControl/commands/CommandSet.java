package com.ForgeEssentials.WorldControl.commands;

//Depreciated - Huh? Do you mean depracated?
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskHandler;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskSetSelection;
import com.ForgeEssentials.core.PlayerInfo;

public class CommandSet extends WorldControlCommandBase
{

	@Override
	public String getName()
	{
		return "set";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		int ID = 0;
		int metadata = 0;

		if (args.length == 1)
		{
			int[] data = this.interpretIDAndMetaFromString(args[0]);
			ID = data[0];
			metadata = data[1];
		} else
		{
			error(player);
			return;
		}

		PlayerInfo info = PlayerInfo.getPlayerInfo(player);
		World world = player.worldObj;
		Selection sel = info.getSelection();
		BackupArea back = new BackupArea();

		// do this once the Ticktask is finished
		TickTaskHandler.addTask(new TickTaskSetSelection(player, ID, metadata, back, sel));
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		// TODO: check permissions.
		return true;
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/" + getCommandName() + " [id:metadata]";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Set the your selection to a certain id and metadata";
	}
}
