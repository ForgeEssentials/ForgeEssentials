package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskCopy;
import com.ForgeEssentials.WorldControl.TickTasks.TickTaskPaste;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerArea;
import com.ForgeEssentials.api.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BlockArray;
import com.ForgeEssentials.util.BlockInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TickTaskHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;

public class CommandPaste extends WorldControlCommandBase
{

	public CommandPaste()
	{
		super(true);
	}

	@Override
	public String getName()
	{
		return "paste";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		if(args.length>1) {
			OutputHandler.chatError(player, "You must have less than two arguments!");
			return;
		}
		PlayerInfo info = PlayerInfo.getPlayerInfo(player);
		
		String name = args.length==0?"default":args[0];
		if(!info.copies.containsKey(name)) {
			OutputHandler.chatError(player, "Invalid paste ID");
			return;
		}
		
		BlockArray back = info.copies.get(name);
		
		AreaBase sel = back.isRelative?new AreaBase(new Point((int)player.posX + back.offX, (int)player.posY + back.offY, (int)player.posZ + back.offZ), new Point((int)player.posX + back.offX + back.sizeX, (int)player.posY + back.offY + back.sizeY, (int)player.posZ + back.offZ + back.sizeZ)):new AreaBase(new Point(back.offX, back.offY, back.offZ), new Point(back.offX + back.sizeX, back.offY + back.sizeY, back.offZ + back.sizeZ));
		
		PermQueryPlayerArea query = new PermQueryPlayerArea(player, getCommandPerm(), sel, false);
		PermResult result = PermissionsAPI.checkPermResult(query);

		if(result==PermResult.ALLOW) {
			TickTaskHandler.addTask(new TickTaskPaste(player, back, sel));
		}else if(result==PermResult.PARTIAL) {
			TickTaskHandler.addTask(new TickTaskPaste(player, back, sel, query.applicable));
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
		return "Expand Selection Positions";
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.WorldControl.selection";
	}

}
