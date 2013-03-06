package com.ForgeEssentials.auth.events;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class AuthEvent extends PlayerEvent
{
	public final Map<String, String> arguments;
	
	public AuthEvent(EntityPlayer player, Map<String, String> arguments)
	{
		super(player);
		this.arguments = arguments;
	}
	
	public static class AuthLoginEvent extends AuthEvent
	{

		public AuthLoginEvent(EntityPlayer player, Map<String, String> arguments)
		{
			super(player, arguments);
		}
		
	}
	
	public static class AuthRegisterEvent extends AuthEvent
	{

		public AuthRegisterEvent(EntityPlayer player, Map<String, String> arguments)
		{
			super(player, arguments);
		}
		
	}

}
