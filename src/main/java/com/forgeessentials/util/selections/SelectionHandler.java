package com.forgeessentials.util.selections;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet1SelectionUpdate;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.PlayerInfo;

public class SelectionHandler
{

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
                ForgeEssentials.log.warn("There was a problem starting the WorldEdit selection provider. Switching to FE's own provider.");
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
                ForgeEssentials.log.error("Error sending selection update to player");
            }
        }
    }

}
