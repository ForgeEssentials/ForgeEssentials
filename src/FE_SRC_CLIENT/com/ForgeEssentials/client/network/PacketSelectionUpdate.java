package com.ForgeEssentials.client.network;

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.WorldServer;

import com.ForgeEssentials.client.ForgeEssentialsClient;
import com.ForgeEssentials.client.util.Point;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PacketSelectionUpdate implements IForgeEssentialsPacket
{
	public static final byte		packetID	= 0;

	private Packet250CustomPayload	packet;

	@Deprecated
	public PacketSelectionUpdate()
	{
		// should never be sent from the client..
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
		else
		{
			ForgeEssentialsClient.getInfo().setPoint1(null);
		}

		// point 2 available
		if (stream.readBoolean())
		{
			int x = stream.readInt();
			int y = stream.readInt();
			int z = stream.readInt();

			ForgeEssentialsClient.getInfo().setPoint2(new Point(x, y, z));
		}
		else
		{
			ForgeEssentialsClient.getInfo().setPoint2(null);
		}
	}

	@Override
	public Packet250CustomPayload getPayload()
	{
		return packet;
	}

}
