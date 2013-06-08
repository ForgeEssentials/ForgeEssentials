package com.ForgeEssentials.client.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PacketHandler implements IPacketHandler
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
			
			OutputHandler.felog.finest("Received packet with ID " + ID);

			switch (ID)
				{
				// cast to the correct instance of ForgeEssentialsPacketbase and use
				// the read methods.
					case 0:
						PacketSelectionUpdate.readClient(stream, world, player);
						break;
					case 1:
						PacketPlayerLogger.readClient(stream, world, player);
						break;
					case 2:
					    PacketRollback.readClient(stream, world, player);
					    break;
					case 3:
						PacketPermNodeList.readClient(stream, world, player);
						break;
				}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
