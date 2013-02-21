package com.ForgeEssentials.WorldControl.commands;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.ForgeEssentials.WorldControl.BlockArray;
import com.ForgeEssentials.WorldControl.BlockArrayBackup;
import com.ForgeEssentials.WorldControl.BlockInfo;
import com.ForgeEssentials.WorldControl.TickTasks.TickTaskReplaceSelection;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerArea;
import com.ForgeEssentials.core.PlayerInfo;
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
			PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
			if (info.getSelection() == null)
			{
				OutputHandler.chatError(player, Localization.get(Localization.ERROR_NOSELECTION));
				return;
			}
			BlockInfo from = new BlockInfo();
			BlockInfo to = new BlockInfo();

			from.parseText(player, args[0]);
			to.parseText(player, args[1]);
			Selection sel = info.getSelection();

			PermQueryPlayerArea query = new PermQueryPlayerArea(player, getCommandPerm(), sel, false);
			PermResult result = PermissionsAPI.checkPermResult(query);

			switch (result)
			{
				case ALLOW:
					TickTaskHandler.addTask(new TickTaskReplaceSelection(player, sel, from, to));
				case PARTIAL:
					TickTaskHandler.addTask(new TickTaskReplaceSelection(player, sel, from, to, query.applicable));
				default:
					OutputHandler.chatError(player, Localization.get(Localization.ERROR_PERMDENIED));
			}

			player.sendChatToPlayer("Working on replace.");
		}
		else
		{
			error(player);
		}
	}

}
