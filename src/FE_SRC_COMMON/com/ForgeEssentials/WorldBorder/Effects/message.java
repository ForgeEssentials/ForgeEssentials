package com.ForgeEssentials.WorldBorder.Effects;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.Configuration;

public class message implements IEffect
{
	private String message = "You passed the world border!";
	
	@Override
	public void registerConfig(Configuration config, String category)
	{
		message = config.get(category, "Message", message, "Message to send to the player. You can use color codes.").value;
	}

	@Override
	public void execute(EntityPlayerMP player) 
	{
		player.sendChatToPlayer(message);
	}
}
