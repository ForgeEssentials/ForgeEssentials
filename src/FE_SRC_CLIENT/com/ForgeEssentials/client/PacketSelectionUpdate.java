package com.ForgeEssentials.client;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.network.IForgeEssentialsPacket;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.WorldServer;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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
				stream.writeInt(p1.x);
				stream.writeInt(p1.y);
				stream.writeInt(p1.z);
			}
			else
				stream.writeBoolean(false);

			if (info != null && info.getPoint2() != null)
			{
				Point p2 = info.getPoint2();
				stream.writeBoolean(true);
				stream.writeInt(p2.x);
				stream.writeInt(p2.y);
				stream.writeInt(p2.z);
			}
			else
				stream.writeBoolean(false);

			stream.close();
			streambyte.close();

			packet.channel = FECHANNEL;
			packet.data = streambyte.toByteArray();
			packet.length = packet.data.length;
		}

		catch (Exception e)
		{
			OutputHandler.SOP("Error creating packet >> " + this.getClass());
		}
	}

	public static void readServer(DataInputStream stream, WorldServer world, EntityPlayer player) throws IOException
	{
		// should never be received here.
	}

	@SideOnly(Side.CLIENT)
	public static void readClient(DataInputStream stream, WorldClient world, EntityPlayer player) throws IOException
	{
		// point 1 available.
		if (stream.readBoolean())
		{
			int x = stream.readInt();
			int y = stream.readInt();
			int z = stream.readInt();

			ForgeEssentialsClient.getInfo().setPoint1(new Point(x, y, z));
		}

		// point 2 available
		if (stream.readBoolean())
		{
			int x = stream.readInt();
			int y = stream.readInt();
			int z = stream.readInt();

			ForgeEssentialsClient.getInfo().setPoint2(new Point(x, y, z));
		}
	}

	@Override
	public Packet250CustomPayload getPayload()
	{
		return packet;
	}

}
