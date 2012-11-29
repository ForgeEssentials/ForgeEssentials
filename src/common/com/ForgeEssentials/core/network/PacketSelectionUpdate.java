package com.ForgeEssentials.core.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.WorldClient;
import net.minecraft.src.WorldServer;

import com.ForgeEssentials.client.core.ProxyClient;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.AreaSelector.Point;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class PacketSelectionUpdate extends ForgeEssentialsPacketBase
{
	public static final byte	packetID	= 0;
	
	public PacketSelectionUpdate(PlayerInfo info)
	{
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
			
			this.channel = FECHANNEL;
			this.data = streambyte.toByteArray();
			this.length = data.length;
		}

		catch (Exception e)
		{
			OutputHandler.SOP("Error creating packet >> "+this.getClass());
		}
	}

	@Override
	public void readServer(DataInputStream stream, WorldServer world, EntityPlayer player) throws IOException
	{
		// should never be received here.
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readClient(DataInputStream stream, WorldClient world, EntityPlayer player) throws IOException
	{
		// point 1 available.
		if (stream.readBoolean())
		{
			int x = stream.readInt();
			int y = stream.readInt();
			int z = stream.readInt();
			
			ProxyClient.info.setPoint1(new Point(x, y, z));
		}
		
		// point 2 available
		if (stream.readBoolean())
		{
			int x = stream.readInt();
			int y = stream.readInt();
			int z = stream.readInt();
			
			ProxyClient.info.setPoint2(new Point(x, y, z));
		}
	}

}
