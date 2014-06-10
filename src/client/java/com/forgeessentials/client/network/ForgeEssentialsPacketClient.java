package com.forgeessentials.client.network;

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;

/*
 * creating a packet? read me first
 * ALL packet classes must be named C<ID><packetName>
 * override readClient if you need to read the packet on the client.
 */
public abstract class ForgeEssentialsPacketClient
{
	public static final String	FECHANNEL	= "ForgeEssentials";

	public abstract Packet250CustomPayload getPayload();
	
	public static void readClient(DataInputStream stream, WorldClient world,
			EntityPlayer player)throws IOException{}
}
