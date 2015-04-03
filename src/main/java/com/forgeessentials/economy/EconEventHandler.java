package com.forgeessentials.economy;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.events.PlayerChangedZone;
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

    @SubscribeEvent
    public void onPlotSet(PlotEvent.Define e)
    {
        e.plot.addPlayerToGroup(new UserIdent(e.player), PlotManager.PLOT_GROUP);
    }

    @SubscribeEvent
    public void onOwnerAdd(PlotEvent.OwnerSet e)
    {
        e.plot.addPlayerToGroup(new UserIdent(e.player), PlotManager.PLOT_GROUP);
    }

    @SubscribeEvent
    public void onOwnerUnset(PlotEvent.OwnerUnset e)
    {
        e.plot.removePlayerFromGroup(new UserIdent(e.player), PlotManager.PLOT_GROUP);
    }

    @SubscribeEvent
    public void onRentDefault(PlotEvent.RentDefaulted e)
    {
        e.plot.removePlayerFromGroup(new UserIdent(e.player), PlotManager.PLOT_GROUP);
    }

    @SubscribeEvent
    public void onZoneChange(PlayerChangedZone e)
    {
        if (e.afterZone.checkGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_ISPLOT))
        {
            if (e.afterZone.checkGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_PERMPROP_DENYENTRY)
                    && e.afterZone.getGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_OWNER) != new UserIdent(e.entityPlayer).getUuid().toString())
            {

            }
        }
    }
}
