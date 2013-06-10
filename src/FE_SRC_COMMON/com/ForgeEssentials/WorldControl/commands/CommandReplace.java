package com.ForgeEssentials.WorldControl.commands;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskReplaceSelection;
import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.AreaSelector.Selection;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.tasks.TaskRegistry;

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
			PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
			if (info.getSelection() == null)
			{
				OutputHandler.chatError(player, Localization.get(Localization.ERROR_NOSELECTION));
				return;
			}
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
				temp = FunctionHelper.parseIdAndMetaFromString(args[1], true);
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
				Selection sel = info.getSelection();
				BackupArea back = new BackupArea();

				String result = APIRegistry.perms.checkPermResult(player, getCommandPerm(), sel);

				if (result.equals("ALLOW"))
					TaskRegistry.registerTask(new TickTaskReplaceSelection(player, firstID, firstMeta, secondID, secondMeta, back, sel));
				else OutputHandler.chatError(player, Localization.get(Localization.ERROR_PERMDENIED));
				
			}

			player.sendChatToPlayer("Working on replace.");
		}
		else
		{
			// The syntax of the command is not correct.
			error(player);
		}
	}

}
