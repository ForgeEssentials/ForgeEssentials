package com.ForgeEssentials.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;

import com.ForgeEssentials.WorldControl.ExtendedPlayerControllerMP;
import com.ForgeEssentials.WorldControl.WorldControlMain;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.WorldClient;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class HandlerClient implements IPacketHandler
{
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player playerFake)
	{
		try
		{
			ByteArrayInputStream streambyte = new ByteArrayInputStream(packet.data);
			DataInputStream stream = new DataInputStream(streambyte);

			EntityPlayer player = (EntityPlayer) playerFake;
			WorldClient world = (WorldClient) player.worldObj;

			if (packet.channel.equals(WorldControlMain.CHANNEL))
			{
				PacketWCSetReach.readClient(stream, world, player);
				return;
			} else
			{

				int ID = stream.read();

				switch (ID)
				{
					case 0:
						break;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
