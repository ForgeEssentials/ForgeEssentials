package com.ForgeEssentials.client.core.network;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.client.core.PlayerInfoClient;
import com.ForgeEssentials.client.core.ProxyClient;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientConnectionHandler implements IConnectionHandler
{

	@Override // SERVER IGNORE!!
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager)
	{
		
	}

	@Override // SERVER! IGNORE!!!
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager)
	{
		return null;
	}

	@Override  // client     MP!!!
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			ProxyClient.setInfo(new PlayerInfoClient());
	}

	@Override // client      SP!!!
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			ProxyClient.setInfo(new PlayerInfoClient());
	}

	@Override // both
	public void connectionClosed(INetworkManager manager)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			ProxyClient.setInfo(null);
	}

	@Override // client
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login)
	{
		// going to use the connections instead.
	}

}
