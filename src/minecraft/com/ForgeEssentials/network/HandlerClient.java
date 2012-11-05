package com.ForgeEssentials.network;

import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class HandlerClient implements IPacketHandler
{
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player playerFake)
	{
	}
}
