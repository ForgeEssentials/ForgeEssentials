package com.ForgeEssentials.api.snooper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Event;

public class VoteEvent extends Event
{
	public EntityPlayer player;
	public String serviceName;
	public String ip;
	public String timeStamp;
	
	public VoteEvent(EntityPlayer player, String serviceName, String ip, String timeStamp)
	{
		this.player = player;
		this.serviceName = serviceName;
		this.ip = ip;
		this.timeStamp = timeStamp;
	}
}
