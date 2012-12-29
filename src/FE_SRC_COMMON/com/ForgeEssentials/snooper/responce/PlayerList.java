package com.ForgeEssentials.snooper.responce;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.List;

import com.ForgeEssentials.snooper.ConfigSnooper;
import com.ForgeEssentials.snooper.ModuleSnooper;
import com.ForgeEssentials.snooper.TextFormatter;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

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
