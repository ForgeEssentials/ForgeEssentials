package com.ForgeEssentials.network;

import java.nio.ByteBuffer;

import com.ForgeEssentials.WorldControl.ExtendedPlayerControllerMP;
import com.ForgeEssentials.WorldControl.WorldControlMain;

import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class HandlerClient implements IPacketHandler
{
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		if(packet.channel.equals(WorldControlMain.CHANNEL))
		{
			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.put(packet.data);
			ExtendedPlayerControllerMP.reachDistance = bb.getFloat(0);
		}

	}
}
