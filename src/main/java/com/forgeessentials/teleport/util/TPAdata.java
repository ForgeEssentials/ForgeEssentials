package com.forgeessentials.teleport.util;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.teleport.CommandTPA;
import com.forgeessentials.util.Localization;
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

		timeout = CommandTPA.timeout;
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
				OutputHandler.chatWarning(receiver, Localization.format("command.tpahere.gotTimeout", sender.username));
				OutputHandler.chatWarning(sender, Localization.format("command.tpahere.sendTimeout", receiver.username));
			}
			else
			{
				OutputHandler.chatWarning(receiver, Localization.format("command.tpa.gotTimeout", sender.username));
				OutputHandler.chatWarning(sender, Localization.format("command.tpa.sendTimeout", receiver.username));
			}
		}
		timeout--;
	}
}
