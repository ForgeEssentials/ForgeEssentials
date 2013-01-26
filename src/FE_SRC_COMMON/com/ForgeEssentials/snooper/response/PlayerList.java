package com.ForgeEssentials.snooper.response;

import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.api.snooper.TextFormatter;

import net.minecraftforge.common.Configuration;

import java.net.DatagramPacket;

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
	public void readConfig(String category, Configuration config)
	{
		// Don't need that here
	}

	@Override
	public void writeConfig(String category, Configuration config)
	{
		// Don't need that here
	}
}
