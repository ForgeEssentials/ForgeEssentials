package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerArea;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

public class CommandChunk extends WorldControlCommandBase
{

	public CommandChunk()
	{
		super(true);
	}

	@Override
	public String getName()
	{
		return "chunk";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		int x = ((int)((int)player.posX)/16-1)*16;
		int z = ((int)((int)player.posZ)/16)*16;
		PlayerInfo info = PlayerInfo.getPlayerInfo(player);
		info.setPoint1(new Point(x, 0, z));
		info.setPoint2(new Point(x+16, 256, z+16));
		OutputHandler.chatConfirmation(player, "Selected chunk");
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/" + getCommandName();
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Select Current Chunk";
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.WorldControl.commands.pos";
	}

}
