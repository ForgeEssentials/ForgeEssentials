package com.ForgeEssentials.core.network;

import net.minecraft.src.Packet250CustomPayload;

public abstract interface IForgeEssentialsPacket
{
	public static final String FECHANNEL = "ForgeEssentials";
	
	public abstract Packet250CustomPayload getPayload();
}
