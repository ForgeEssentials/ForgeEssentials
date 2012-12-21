package com.ForgeEssentials.commands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class EventHandler
{

	@ForgeSubscribe
	public void onPlayerDeath(LivingDeathEvent e)
	{
		if (e.entity instanceof EntityPlayer)
			PlayerInfo.getPlayerInfo((EntityPlayer) e.entity).lastDeath = FunctionHelper.getEntityPoint(e.entity);
	}
}
