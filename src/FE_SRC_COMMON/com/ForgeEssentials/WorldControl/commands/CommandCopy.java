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

public class CommandCopy extends WorldControlCommandBase
{

	public CommandCopy()
	{
		super(true);
	}

	@Override
	public String getName()
	{
		return "copy";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		if(args.length>2) {
			OutputHandler.chatError(player, "You must have less than three arguments!");
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
		if(args.length==2) {
			fill = BlockInfo.parseAll(args[1], player);
		}

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
		return "/" + getCommandName() + " [name(default)] [filler]";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Copies selection to clipboard";
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.WorldControl.clipboard";
	}

}
