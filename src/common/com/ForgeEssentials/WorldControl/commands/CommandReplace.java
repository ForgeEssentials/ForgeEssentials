package com.ForgeEssentials.WorldControl.commands;

import javax.activation.CommandInfo;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.World;

import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskHandler;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskReplaceSelection;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskSetSelection;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.Localization;

import cpw.mods.fml.common.registry.LanguageRegistry;

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
					player.sendChatToPlayer(String.format(Localization.get("forgeEssentials.wc.blockIdOutOfRange"), Block.blocksList.length));
				}
				else if (Block.blocksList[firstID] == null)
				{
					player.sendChatToPlayer(String.format(Localization.get("forgeEssentials.wc.invalidBlockId"), firstID));
				}
				else if (Block.blocksList[secondID] == null)
				{
					player.sendChatToPlayer(String.format(Localization.get("forgeEssentials.wc.invalidBlockId"), secondID));
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

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		// TODO: check permissions.
		return true;
	}

}
