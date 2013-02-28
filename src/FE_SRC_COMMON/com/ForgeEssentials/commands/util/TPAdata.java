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

		timeout = CommandTPA.timeout;
		tphere = tpaHere;
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
			if (tphere)
			{
				receiver.sendChatToPlayer("TPAhere from " + sender.username + " timed out");
				sender.sendChatToPlayer("TPAhere to " + receiver.username + " timed out");
			}
			else
			{
				receiver.sendChatToPlayer("TPA from " + sender.username + " timed out");
				sender.sendChatToPlayer("TPA to " + receiver.username + " timed out");
			}
		}
		timeout--;
	}
}
