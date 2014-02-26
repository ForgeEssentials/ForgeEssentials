package com.forgeessentials.worldcontrol.commands;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.query.PermQuery.PermResult;
import com.forgeessentials.api.permissions.query.PermQueryPlayerArea;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.core.misc.FriendlyItemList;
import com.forgeessentials.util.BackupArea;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.Localization;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.AreaSelector.Selection;
import com.forgeessentials.util.tasks.TaskRegistry;
import com.forgeessentials.worldcontrol.TickTasks.TickTaskSetSelection;

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

				PermQueryPlayerArea query = new PermQueryPlayerArea(player, getCommandPerm(), sel, false);
				PermResult result = APIRegistry.perms.checkPermResult(query);

				switch (result)
					{
						case ALLOW:
							TaskRegistry.registerTask(new TickTaskSetSelection(player, ID, metadata, back, sel));
							return;
						case PARTIAL:
							TaskRegistry.registerTask(new TickTaskSetSelection(player, ID, metadata, back, sel, query.applicable));
						default:
							OutputHandler.chatError(player, Localization.get(Localization.ERROR_PERMDENIED));
							return;
					}
			}
			ChatUtils.sendMessage(player, "Working on set.");
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

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
