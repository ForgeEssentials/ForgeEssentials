package com.ForgeEssentials.WorldControl.commands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskTopManipulator;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.BlockArray;
import com.ForgeEssentials.util.BlockArrayBackup;
import com.ForgeEssentials.util.BlockInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TickTaskHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;

public class CommandTopManipulate extends WorldControlCommandBase
{

	private String						name;
	private TickTaskTopManipulator.Mode	manipulateMode;
	
	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.WorldControl.topmanipulate";
	}

	public CommandTopManipulate(String cmdName, TickTaskTopManipulator.Mode mode)
	{
		super(false);
		name = cmdName;
		manipulateMode = mode;
	}

	@Override
	public String getName()
	{
		return "/"+name;
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		if (args.length == 0 || args.length == 1)
		{
			AreaBase area = new AreaBase();
			PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
			if(args.length == 0) {
				if(info.getSelection()==null) {
					OutputHandler.chatError(player, "You must have a selection set.");
					return;
				}
				area = info.getSelection();
			}else if (args.length == 1)
			{
				if(!FunctionHelper.isInt(args[0])) {
					OutputHandler.chatError(player, "Radius is not a number.");
					return;
				}
				int radius = Integer.parseInt(args[0]);
				area.setPoints(new Point((int) player.posX - 1 - radius/2, (int) player.posY - radius/2, (int) player.posZ - radius/2), new Point((int) player.posX - 1 + radius/2, (int) player.posY + radius/2, (int) player.posZ + radius/2));
			}
			TickTaskHandler.addTask(new TickTaskTopManipulator(player, area, manipulateMode));
			player.sendChatToPlayer("Working on " + name + ".");
		}
		else
		{
			error(player);
		}
	}

}
