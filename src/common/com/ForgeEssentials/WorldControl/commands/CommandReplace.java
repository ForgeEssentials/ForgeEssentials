package com.ForgeEssentials.WorldControl.commands;

import javax.activation.CommandInfo;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.World;

import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskHandler;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskReplaceSelection;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskSetSelection;
import com.ForgeEssentials.core.PlayerInfo;

public class CommandReplace extends WorldControlCommandBase
{

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
				player.sendChatToPlayer("Invalid use of /" + getCommandName() + ": First argument is invalid.");
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
					player.sendChatToPlayer("Invalid use of /" + getCommandName() + ": Second argument is invalid.");
					secondID = -1;						
				}
			}
			
			// Execute command if both arguments are okay.
			if (firstID != -1 && secondID != -1)
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

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/" + getCommandName() + " <id[:metadata]> <id[:metadata]";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Searches an area for the first id:metadata pair and replaces it with the second id:metadata. -1 in first pair will replace ALL occurances.";
	}

}
