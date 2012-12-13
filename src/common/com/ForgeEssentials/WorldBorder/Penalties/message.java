package com.ForgeEssentials.WorldBorder.Penalties;

import net.minecraftforge.common.Configuration;

public class message implements IPenalty
{
	private String message;
	
	@Override
	public void registerConfig(Configuration config, String category)
	{
		message = config.get(category, "Message", "You passed the world border!", "Message to send to the player. You can use color codes.").value;
	}
}
