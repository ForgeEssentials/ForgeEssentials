package com.ForgeEssentials.commands.util;

import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.commands.CommandTPA;

public class TPAdata
{
	public EntityPlayerMP	sender;
	public EntityPlayerMP	receiver;
	int						timeout;
	public boolean			tphere;

	public TPAdata(EntityPlayerMP sender, EntityPlayerMP receiver, boolean tpaHere)
	{
		this.sender = sender;
		this.receiver = receiver;
		
		this.timeout = CommandTPA.timeout;
		this.tphere = tpaHere;
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
			if(tphere)
			{
				this.receiver.sendChatToPlayer(	"TPAhere from " + 	this.sender.username 	+ " timed out");
				this.sender.sendChatToPlayer(	"TPAhere to " + 	this.receiver.username 	+ " timed out");
			}
			else
			{
				this.receiver.sendChatToPlayer(	"TPA from " + 	this.sender.username 	+ " timed out");
				this.sender.sendChatToPlayer(	"TPA to " + 	this.receiver.username 	+ " timed out");
			}
		}
		timeout--;
	}
}
