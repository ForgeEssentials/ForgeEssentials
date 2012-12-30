package com.ForgeEssentials.snooper.response;

import java.net.DatagramPacket;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.snooper.API.Response;
import com.ForgeEssentials.snooper.API.TextFormatter;

public class PlayerList extends Response
{
	@Override
	public String getResponceString(DatagramPacket packet)
	{
		return dataString = TextFormatter.toJSON(server.getAllUsernames());
	}

	@Override
	public String getName() 
	{
		return "PlayerList";
	}

	@Override
	public void setupConfig(String category, Configuration config)
	{
		//Don't need that here
	}
}
