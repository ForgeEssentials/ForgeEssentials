package com.forgeessentials.core.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.WorldServer;

import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.AreaSelector.Point;

public class PacketSelectionUpdate implements IForgeEssentialsPacket
{
	public static final byte		packetID	= 0;

	private Packet250CustomPayload	packet;

	public PacketSelectionUpdate(PlayerInfo info)
	{
		packet = new Packet250CustomPayload();

		ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(streambyte);

		try
		{
			stream.write(packetID);

			if (info != null && info.getPoint1() != null)
			{
				Point p1 = info.getPoint1();
				stream.writeBoolean(true);
				stream.writeDouble(p1.x);
				stream.writeDouble(p1.y);
				stream.writeDouble(p1.z);
			}
			else
			{
				stream.writeBoolean(false);
			}

			if (info != null && info.getPoint2() != null)
			{
				Point p2 = info.getPoint2();
				stream.writeBoolean(true);
				stream.writeDouble(p2.x);
				stream.writeDouble(p2.y);
				stream.writeDouble(p2.z);
			}
			else
			{
				stream.writeBoolean(false);
			}

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
