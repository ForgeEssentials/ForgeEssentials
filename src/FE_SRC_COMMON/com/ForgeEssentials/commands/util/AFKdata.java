package com.ForgeEssentials.commands.util;

import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.commands.CommandAFK;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class AFKdata
{
	public EntityPlayerMP player;
	private WorldPoint lastPos;
	private WorldPoint currentPos;
	int waittime;
	public boolean needstowait;
	
	public AFKdata(EntityPlayerMP player)
	{
		this.player = player;
		waittime = CommandAFK.warmup;
		lastPos = new WarpPoint(player);
		needstowait = true;
	}
	
	public void count()
	{
		if(player == null)
		{
			TickHandlerCommands.afkListToRemove.add(this);
			return;
		}
		
		currentPos = new WarpPoint(player);
		if (!lastPos.equals(currentPos))
		{
			CommandAFK.abort(this);
		}
		
		if(needstowait)
		{
			if (waittime == 0)
			{
				CommandAFK.makeAFK(this);
			}
			waittime--;
		}
	}
}
