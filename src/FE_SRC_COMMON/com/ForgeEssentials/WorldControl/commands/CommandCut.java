package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskCopy;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerArea;
import com.ForgeEssentials.api.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BlockInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TickTaskHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;

public class CommandCut extends WorldControlCommandBase
{

	public CommandCut()
	{
		super(true);
	}

	@Override
	public String getName()
	{
		return "cut";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		if(args.length>1) {
			OutputHandler.chatError(player, "You must have less than two arguments!");
			return;
		}
		PlayerInfo info = PlayerInfo.getPlayerInfo(player);
		AreaBase sel = info.getSelection();
		if(sel == null) {
			OutputHandler.chatError(player, "You must have a selection!");
			return;
		}
		PermQueryPlayerArea query = new PermQueryPlayerArea(player, getCommandPerm(), sel, false);
		PermResult result = PermissionsAPI.checkPermResult(query);
		
		BlockInfo fill = null;

		if(result==PermResult.ALLOW) {
			TickTaskHandler.addTask(new TickTaskCopy(player, sel, args.length==0?"default":args[0], fill));
		}else if(result==PermResult.PARTIAL) {
			TickTaskHandler.addTask(new TickTaskCopy(player, sel, args.length==0?"default":args[0], fill, query.applicable));
		}else{
			OutputHandler.chatError(player, "You do not have permission!");
		}
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/" + getCommandName() + " [name(default)]";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Copy and clear selection";
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.WorldControl.clipboard";
	}

}
