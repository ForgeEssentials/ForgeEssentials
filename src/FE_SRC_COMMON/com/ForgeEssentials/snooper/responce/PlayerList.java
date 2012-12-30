package com.ForgeEssentials.snooper.responce;

import java.net.DatagramPacket;

import com.ForgeEssentials.snooper.ConfigSnooper;
import com.ForgeEssentials.snooper.TextFormatter;

public class PlayerList extends Response
{
	public PlayerList(DatagramPacket packet)
	{
		super(packet);
		this.allowed = ConfigSnooper.send_Players;
		if(!this.allowed) return;
		
		this.dataString = TextFormatter.toJSON(server.getAllUsernames());
	}
}
