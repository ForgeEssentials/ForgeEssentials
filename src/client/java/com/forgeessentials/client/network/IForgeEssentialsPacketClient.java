package com.forgeessentials.client.network;

import net.minecraft.network.packet.Packet250CustomPayload;

public abstract interface IForgeEssentialsPacketClient
{
	public static final String	FECHANNEL	= "ForgeEssentials";

	public abstract Packet250CustomPayload getPayload();
}
