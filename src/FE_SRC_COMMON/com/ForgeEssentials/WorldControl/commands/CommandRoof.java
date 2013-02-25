package com.ForgeEssentials.WorldControl.commands;

//Depreciated - Huh? Do you mean depracated?
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskReplaceSelection;
import com.ForgeEssentials.WorldControl.TickTasks.TickTaskRoof;
import com.ForgeEssentials.WorldControl.TickTasks.TickTaskSetSelection;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerArea;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.misc.ItemList;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.BlockArray;
import com.ForgeEssentials.util.BlockArrayBackup;
import com.ForgeEssentials.util.BlockInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TickTaskHandler;
import com.ForgeEssentials.util.AreaSelector.Selection;

public class CommandRoof extends WorldControlCommandBase
{

	public CommandRoof()
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
		return "roof";
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
				TickTaskHandler.addTask(new TickTaskRoof(player, sel, to));
			}else if(result==PermResult.PARTIAL) {
				TickTaskHandler.addTask(new TickTaskRoof(player, sel, to, query.applicable));
			}else{
				OutputHandler.chatError(player, Localization.get(Localization.ERROR_PERMDENIED));
			}

			player.sendChatToPlayer("Working on roof.");
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
