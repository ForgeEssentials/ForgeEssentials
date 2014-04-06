package com.forgeessentials.teleport.util;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.teleport.TeleportModule;
import com.forgeessentials.util.OutputHandler;

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

		timeout = TeleportModule.timeout;
		tphere = tpaHere;
	}

	public void count()
	{
		if (sender == null)
		{
			TickHandlerTP.tpaListToRemove.add(this);
			return;
		}

		if (receiver == null)
		{
			TickHandlerTP.tpaListToRemove.add(this);
			return;
		}

		if (timeout == 0)
		{
			TickHandlerTP.tpaListToRemove.add(this);
			if (tphere)
			{
				OutputHandler.chatWarning(receiver, String.format("TPA from %s timed out.", sender.username));
				OutputHandler.chatWarning(sender, String.format("TPA to %s timed out.", receiver.username));
			}
			else
			{
				OutputHandler.chatWarning(receiver, String.format("TPA from %s timed out", sender.username));
				OutputHandler.chatWarning(sender, String.format("TPA to %s timed out.", receiver.username));
			}
		}
		timeout--;
	}
}
