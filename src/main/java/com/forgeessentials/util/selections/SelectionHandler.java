package com.forgeessentials.util.selections;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet1SelectionUpdate;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.events.FEPlayerEvent.ClientHandshakeEstablished;
import com.forgeessentials.util.output.LoggingHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SelectionHandler
{
    public SelectionHandler()
    {
        APIRegistry.getFEEventBus().register(this);
    }

    public static ISelectionProvider selectionProvider = pickBestSelectionProvider();

    private static ISelectionProvider pickBestSelectionProvider()
    {
        if (ModuleLauncher.getModuleList().contains("WEIntegrationTools"))
        {
            try
            {
                Class<?> weprovider = Class.forName("com.forgeessentials.compat.worldedit.WESelectionHandler");
                return (ISelectionProvider) weprovider.newInstance();
            }
            catch (ClassNotFoundException | InstantiationException | IllegalAccessException e3)
            {
                LoggingHandler.felog.warn("There was a problem starting the WorldEdit selection provider. Switching to FE's own provider.");
            }
        }
        return new PlayerInfoSelectionProvider();
    }

    public static void sendUpdate(EntityPlayerMP player)
    {
        if (PlayerInfo.get(player).getHasFEClient())
        {
            try
            {
                NetworkUtils.netHandler.sendTo(new Packet1SelectionUpdate(selectionProvider.getSelection(player)), player);
            }
            catch (NullPointerException e)
            {
                LoggingHandler.felog.error("Error sending selection update to player");
            }
        }
    }

    @SubscribeEvent
    public void onClientConnect(ClientHandshakeEstablished e)
    {
        sendUpdate(e.getPlayer());
    }

}
