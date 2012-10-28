package com.ForgeEssentials.WorldControl;

import java.nio.ByteBuffer;

import com.ForgeEssentials.WorldControl.ExtendedPlayerControllerMP;

import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

@SideOnly(value=Side.CLIENT)
public class ClientPacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		if(packet.channel.equals("WCR"))
		{
			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.put(packet.data);
			ExtendedPlayerControllerMP.reachDistance = bb.getFloat(0);
		}
	}
}
