package com.ForgeEssentials.WorldControl.commands;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskReplaceSelection;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.TickTaskHandler;
import com.ForgeEssentials.util.AreaSelector.Selection;

public class CommandReplace extends WorldControlCommandBase
{

	public CommandReplace()
	{
		super(true);
	}

	@Override
	public String getName()
	{
		return "replace";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		if (args.length >= 2)
		{
			int firstID = -1;
			int firstMeta = -1;
			int secondID = -1;
			int secondMeta = -1;
			
			// Begin parsing 1st argument pair
			
			String[] first = args[0].split(":");			
			try
			{
				firstID = Integer.parseInt(first[0]);
				if (first.length > 1)
				{
					firstMeta = Integer.parseInt(first[1]);
				}
			}
			catch (Exception e)
			{
				error(player);
				firstID = -1;
			}
			
			// Begin parsing 2nd argument pair if 1st was good.
			if (firstID != -1)
			{
				String[] second = args[1].split(":");
				
				try
				{
					secondID = Integer.parseInt(second[0]);
					if (second.length > 1)
					{
						secondMeta = Integer.parseInt(second[1]);
					}
				}
				catch (Exception e)
				{
					error(player);
					secondID = -1;						
				}
			}
			
			// Execute command if both arguments are okay.
			if (firstID != -1 && secondID != -1)
			{
				if (firstID >= Block.blocksList.length || secondID >= Block.blocksList.length)
				{
					error(player, Localization.format("message.wc.blockIdOutOfRange", Block.blocksList.length));
				}
				else if (firstID != 0 && Block.blocksList[firstID] == null)
				{
					error(player, Localization.format("message.wc.invalidBlockId", firstID));
				}
				else if (secondID != 0 && Block.blocksList[secondID] == null)
				{
					error(player, Localization.format("message.wc.invalidBlockId", secondID));
				}
				else
				{
					PlayerInfo info = PlayerInfo.getPlayerInfo(player);
					World world = player.worldObj;
					Selection sel = info.getSelection();
					BackupArea back = new BackupArea();

					TickTaskHandler.addTask(new TickTaskReplaceSelection(player, firstID, firstMeta, secondID, secondMeta, back, sel));
				}
			}
			else
			{
				error(player);
			}
		}
		else
		{
			// The syntax of the command is not correct.
			error(player);
		}
	}

}
