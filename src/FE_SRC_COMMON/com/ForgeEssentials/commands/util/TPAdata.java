package com.ForgeEssentials.commands.util;

import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.commands.CommandAFK;
import com.ForgeEssentials.commands.CommandTPA;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class TPAdata
{
	public EntityPlayerMP	sender;
	public EntityPlayerMP	receiver;
	int						timeout;

	public TPAdata(EntityPlayerMP sender, EntityPlayerMP receiver)
	{
		this.sender = sender;
		this.receiver = receiver;
		
		timeout = CommandTPA.timeout;
	}

	public void count()
	{
		if (sender == null)
		{
			TickHandlerCommands.tpaListToRemove.add(this);
			return;
		}
		
		if (receiver == null)
		{
			TickHandlerCommands.tpaListToRemove.add(this);
			return;
		}
		
		if (timeout == 0)
		{
			TickHandlerCommands.tpaListToRemove.add(this);
			this.receiver.sendChatToPlayer(	"TPA from " + 	this.sender.username 	+ " timed out");
			this.sender.sendChatToPlayer(	"TPA to " + 	this.receiver.username 	+ " timed out");
		}
		timeout--;
	}
}
