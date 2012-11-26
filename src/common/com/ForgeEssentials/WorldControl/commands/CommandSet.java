package com.ForgeEssentials.WorldControl.commands;

//Depreciated - Huh? Do you mean depracated?
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskHandler;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskReplaceSelection;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskSetSelection;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.Localization;

public class CommandSet extends WorldControlCommandBase
{

	@Override
	public String getName()
	{
		return "/set";
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
			
			if (ID >= Block.blocksList.length)
			{
				player.sendChatToPlayer(String.format(Localization.get("forgeEssentials.wc.blockIdOutOfRange"), Block.blocksList.length));
			}
			else if (Block.blocksList[ID] == null)
			{
				player.sendChatToPlayer(String.format(Localization.get("forgeEssentials.wc.invalidBlockId"), ID));
			}
			else
			{
				PlayerInfo info = PlayerInfo.getPlayerInfo(player);
				World world = player.worldObj;
				Selection sel = info.getSelection();
				BackupArea back = new BackupArea();

				TickTaskHandler.addTask(new TickTaskSetSelection(player, ID, metadata, back, sel));
			}
		}
		else
		{
			error(player);
		}

	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		// TODO: check permissions.
		return true;
	}
}
