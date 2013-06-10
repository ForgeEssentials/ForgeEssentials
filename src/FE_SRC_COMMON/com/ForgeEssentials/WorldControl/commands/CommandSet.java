package com.ForgeEssentials.WorldControl.commands;

//Depreciated - Huh? Do you mean depracated?
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskSetSelection;
import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.AreaSelector.Selection;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.misc.FriendlyItemList;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.tasks.TaskRegistry;

public class CommandSet extends WorldControlCommandBase
{

	public CommandSet()
	{
		super(true);
	}

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
			int[] data = FunctionHelper.parseIdAndMetaFromString(args[0], true);
			ID = data[0];
			metadata = data[1];

			if (ID >= Block.blocksList.length)
			{
				error(player, Localization.format("message.wc.blockIdOutOfRange", Block.blocksList.length));
			}
			else if (ID != 0 && Block.blocksList[ID] == null)
			{
				error(player, Localization.format("message.wc.invalidBlockId", ID));
			}
			else
			{
				PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
				if (info.getSelection() == null)
				{
					OutputHandler.chatError(player, Localization.get(Localization.ERROR_NOSELECTION));
					return;
				}
				Selection sel = info.getSelection();
				BackupArea back = new BackupArea();


				String result = APIRegistry.perms.checkPermResult(player, getCommandPerm(), sel);

				if (result.equals("ALLOW"))
					TaskRegistry.registerTask(new TickTaskSetSelection(player, ID, metadata, back, sel));
				else OutputHandler.chatError(player, Localization.get(Localization.ERROR_PERMDENIED));
			}
			player.sendChatToPlayer("Working on set.");
		}
		else
		{
			error(player);
		}
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsFromIterableMatchingLastWord(args, FriendlyItemList.instance().getBlockList());
		return null;
	}
}
