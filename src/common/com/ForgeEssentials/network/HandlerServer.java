package com.ForgeEssentials.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import com.ForgeEssentials.WorldControl.WorldControlMain;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.WorldServer;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class HandlerServer implements IPacketHandler
{

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player playerFake)
	{
		try
		{
			ByteArrayInputStream streambyte = new ByteArrayInputStream(packet.data);
			DataInputStream stream = new DataInputStream(streambyte);

			EntityPlayer player = (EntityPlayer) playerFake;
			WorldServer world = (WorldServer) player.worldObj;

			if (packet.channel.equals(WorldControlMain.CHANNEL))
			{
				PacketWCSetReach.readServer(stream, world, player);
				return;
			}
			else
			{

				int ID = stream.read();

				switch (ID)
					{
						case 0:
							break;
					}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
