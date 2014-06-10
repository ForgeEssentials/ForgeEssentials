package com.forgeessentials.playerlogger.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.WorldServer;

import com.forgeessentials.core.network.ForgeEssentialsPacket;
import com.forgeessentials.util.OutputHandler;

public class PacketPlayerLogger extends ForgeEssentialsPacket
{
	public static final byte		packetID	= 1;

	private Packet250CustomPayload	packet;

	public PacketPlayerLogger(EntityPlayer player)
	{
		packet = new Packet250CustomPayload();

		ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(streambyte);

		try
		{
			stream.write(packetID);

			stream.writeBoolean(player.getEntityData().getBoolean("lb"));

			stream.close();
			streambyte.close();

			packet.channel = FECHANNEL;
			packet.data = streambyte.toByteArray();
			packet.length = packet.data.length;
		}

		catch (Exception e)
		{
			OutputHandler.felog.info("Error creating packet >> " + this.getClass());
		}
	}

	public static void readServer(DataInputStream stream, WorldServer world, EntityPlayer player) throws IOException
	{
		// should never be received here.
	}

	@Override
	public Packet250CustomPayload getPayload()
	{
		return packet;
	}

}
