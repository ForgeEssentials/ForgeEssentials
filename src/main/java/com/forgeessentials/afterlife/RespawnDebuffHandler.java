package com.forgeessentials.afterlife;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.ServerUtil;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class RespawnDebuffHandler
{

    public RespawnDebuffHandler()
    {
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e)
    {
        if (e.player.worldObj.isRemote)
            return;

        String potionEffects = APIRegistry.perms.getUserPermissionProperty(UserIdent.get(e.player), ModuleAfterlife.PERM_DEBUFFS);
        if (potionEffects != null)
            PlayerUtil.applyPotionEffects(e.player, potionEffects);

        Integer respawnHP = ServerUtil.tryParseInt(APIRegistry.perms.getUserPermissionProperty(UserIdent.get(e.player), ModuleAfterlife.PERM_HP));
        if (respawnHP != null)
            e.player.setHealth(respawnHP);

        Integer respawnFood = ServerUtil.tryParseInt(APIRegistry.perms.getUserPermissionProperty(UserIdent.get(e.player), ModuleAfterlife.PERM_FOOD));
        if (respawnFood != null)
            e.player.getFoodStats().addStats(-1 * (20 - respawnFood), 0);
    }

}