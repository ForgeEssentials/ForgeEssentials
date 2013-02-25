package com.ForgeEssentials.WorldControl.commands;

//Depreciated - Huh? Do you mean depracated?
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskFloor;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerArea;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.misc.ItemList;
import com.ForgeEssentials.util.BlockInfo;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TickTaskHandler;
import com.ForgeEssentials.util.AreaSelector.Selection;

public class CommandFloor extends WorldControlCommandBase
{

	public CommandFloor()
	{
		super(true);
	}
	
	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.WorldControl.blockmanipulation";
	}

	@Override
	public String getName()
	{
		return "floor";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		if (args.length == 1)
		{
			PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
			if (info.getSelection() == null)
			{
				OutputHandler.chatError(player, Localization.get(Localization.ERROR_NOSELECTION));
				return;
			}
			BlockInfo to = BlockInfo.parseAll(args[0], player);
			Selection sel = info.getSelection();

			PermQueryPlayerArea query = new PermQueryPlayerArea(player, getCommandPerm(), sel, false);
			PermResult result = PermissionsAPI.checkPermResult(query);

			if(result==PermResult.ALLOW) {
				TickTaskHandler.addTask(new TickTaskFloor(player, sel, to));
			}else if(result==PermResult.PARTIAL) {
				TickTaskHandler.addTask(new TickTaskFloor(player, sel, to, query.applicable));
			}else{
				OutputHandler.chatError(player, Localization.get(Localization.ERROR_PERMDENIED));
			}

			player.sendChatToPlayer("Working on floor.");
		}
		else
		{
			OutputHandler.chatError(player, "Must specify block identifiers!");
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, ItemList.instance().getBlockList());
		}
		return null;
	}
}
