package com.ForgeEssentials.WorldBorder.Effects;

import net.minecraft.src.EntityPlayerMP;
import net.minecraftforge.common.Configuration;

public class message implements IEffect
{
	private String message;
	
	@Override
	public void registerConfig(Configuration config, String category)
	{
		message = config.get(category, "Message", "You passed the world border!", "Message to send to the player. You can use color codes.").value;
	}

	@Override
	public void execute(EntityPlayerMP player) 
	{
		player.sendChatToPlayer(message);
	}
}
