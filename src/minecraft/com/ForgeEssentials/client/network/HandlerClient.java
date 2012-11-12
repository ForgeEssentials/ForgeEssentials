package com.ForgeEssentials.client.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import com.ForgeEssentials.network.PacketSelectionUpdate;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.WorldClient;
import net.minecraft.src.WorldServer;
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

			int ID = stream.read();

			switch (ID)
				{
				// cast to the correct instance of ForgeEssentialsPacketbase and use the read methods.
					case 0:
						((PacketSelectionUpdate) packet).readClient(stream, world, player);
				}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
