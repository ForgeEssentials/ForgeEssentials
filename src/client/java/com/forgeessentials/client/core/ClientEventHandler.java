package com.forgeessentials.client.core;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.network.C0PacketHandshake.Message;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandler
{

    @SubscribeEvent
    public void connectionOpened(FMLNetworkEvent.ClientConnectedToServerEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            ForgeEssentialsClient.info = new PlayerInfoClient();
        }

    }

    @SubscribeEvent
    public void connectionClosed(FMLNetworkEvent.ClientDisconnectionFromServerEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            ForgeEssentialsClient.info = null;
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent e)
    {
        if (ForgeEssentialsClient.instance.serverHasFE)
        {
            System.out.println("Dispatching FE handshake packet");
            ForgeEssentialsClient.instance.netHandler.sendToServer(new Message());

        }
    }

}
