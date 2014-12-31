package com.forgeessentials.economy;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.events.PlotEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.permissions.PermissionsManager;

public class EconEventHandler extends ServerEventHandler
{
    public static boolean convertXPDrops;
    public static int threshold;

    @SubscribeEvent
    public void onXPPickup(PlayerPickupXpEvent e)
    {
        if (!convertXPDrops || !PermissionsManager.checkPermission(e.entityPlayer, ModuleEconomy.PICKUP_XP_PERM))
        {
            return;
        }

        int xp = e.entityPlayer.getEntityData().getInteger("FEXPThreshold");
        xp = xp + e.orb.xpValue;

        if (xp >= threshold)
        {
            APIRegistry.wallet.addToWallet(threshold, new UserIdent(e.entityPlayer).getUuid());
            e.entityPlayer.getEntityData().setInteger("FEXPThreshold", xp - threshold);
        }
    }

    /*
    olee i need your help with this
    OwnerSet and Define require adding the player to the plot-owners group in zones
    OwnerUnset and RentDefaulted require removing the player from the plot-owners group
    @SubscribeEvent
    public void onPlotSet(PlotEvent.Define e)
    {
    }

    @SubscribeEvent
    public void onOwnerAdd(PlotEvent.OwnerSet e)
    {
    }

    @SubscribeEvent
    public void onOwnerUnset(PlotEvent.OwnerUnset e)
    {
    }

    @SubscribeEvent
    public void onRentDefault(PlotEvent.RentDefaulted e)
    {
    }
    */
}
