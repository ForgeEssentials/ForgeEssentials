package com.ForgeEssentials.WorldControl.commands;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskReplaceSelection;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.query.PermQuery.PermResult;
import com.ForgeEssentials.permission.query.PermQueryPlayerArea;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
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
		if (args.length == 2)
		{
			int[] temp;
			int firstID = -1;
			int firstMeta = -1;
			int secondID = -1;
			int secondMeta = -1;

			// Begin parsing 1st argument pair

			try
			{
				temp = FunctionHelper.parseIdAndMetaFromString(args[0], true);
				firstID = temp[0];
				firstMeta = temp[1];
			}
			catch (Exception e)
			{
				OutputHandler.chatError(player, e.getMessage());
				return;
			}

			// Begin parsing 2nd argument pair if 1st was good.
			try
			{
				temp = FunctionHelper.parseIdAndMetaFromString(args[2], true);
				secondID = temp[0];
				secondMeta = temp[1];
			}
			catch (Exception e)
			{
				OutputHandler.chatError(player, e.getMessage());
				return;
			}

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

				PermQueryPlayerArea query = new PermQueryPlayerArea(player, getCommandPerm(), sel, false);
				PermResult result = PermissionsAPI.checkPermResult(query);

				switch (result)
					{
						case ALLOW:
							TickTaskHandler.addTask(new TickTaskReplaceSelection(player, firstID, firstMeta, secondID, secondMeta, back, sel));
							return;
						case PARTIAL:
							TickTaskHandler.addTask(new TickTaskReplaceSelection(player, firstID, firstMeta, secondID, secondMeta, back, sel, query.applicable));
						default:
							OutputHandler.chatError(player, Localization.get(Localization.ERROR_PERMDENIED));
							return;
					}

			}
		}
		else
		{
			// The syntax of the command is not correct.
			error(player);
		}
	}

}
