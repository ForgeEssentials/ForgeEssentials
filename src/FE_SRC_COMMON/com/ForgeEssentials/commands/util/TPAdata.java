package com.ForgeEssentials.commands.util;

import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.commands.CommandTPA;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

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
