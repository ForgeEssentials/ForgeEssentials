package com.ForgeEssentials.client;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@NetworkMod(clientSideRequired = false, serverSideRequired = false, clientPacketHandlerSpec = @SidedPacketHandler(channels =
{ "ForgeEssentials" }, packetHandler = com.ForgeEssentials.client.network.PacketHandler.class))
@Mod(modid = "ForgeEssentialsClient", name = "Forge Essentials Client Addon", version = "@VERSION@")
public class ForgeEssentialsClient
{

	@SideOnly(Side.CLIENT)
	private static PlayerInfoClient	info;

	@PreInit
	public void preInit(FMLPreInitializationEvent e)
	{
		if (FMLCommonHandler.instance().getSide().isServer() && ObfuscationReflectionHelper.obfuscation)
			throw new RuntimeException("ForgeEssentialsClient should not be installed on a server!");
	}

	@SideOnly(Side.CLIENT)
	@Init
	public void load(FMLInitializationEvent e)
	{
		NetworkRegistry.instance().registerConnectionHandler(new ClientConnectionHandler());
		MinecraftForge.EVENT_BUS.register(new CUIRenderrer());
	}

	@SideOnly(Side.CLIENT)
	public static PlayerInfoClient getInfo()
	{
		if (info == null)
			info = new PlayerInfoClient();
		return info;
	}

	@SideOnly(Side.CLIENT)
	public static void setInfo(PlayerInfoClient info)
	{
		ForgeEssentialsClient.info = info;
	}

}
