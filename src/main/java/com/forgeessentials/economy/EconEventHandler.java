package com.forgeessentials.economy;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.events.ServerEventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;

public class EconEventHandler extends ServerEventHandler
{
    public static boolean convertXPDrops;
    public static int threshold;

    @SubscribeEvent
    public void onXPPickup(PlayerPickupXpEvent e)
    {
        if (!convertXPDrops)
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
}
