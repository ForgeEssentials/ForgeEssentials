package com.ForgeEssentials.commands.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.FunctionHelper;

public class EventHandler
{

	@ForgeSubscribe(priority = EventPriority.LOW)
	public void onPlayerDeath(LivingDeathEvent e)
	{
		if (e.entity instanceof EntityPlayer)
			PlayerInfo.getPlayerInfo((EntityPlayer) e.entity).lastDeath = FunctionHelper.getEntityPoint(e.entity);
	}
}
