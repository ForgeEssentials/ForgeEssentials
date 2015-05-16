package com.forgeessentials.afterlife;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.util.FunctionHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class RespawnDebuffHandler {

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
            FunctionHelper.applyPotionEffects(e.player, potionEffects);

        Integer respawnHP = FunctionHelper.tryParseInt(APIRegistry.perms.getUserPermissionProperty(UserIdent.get(e.player), ModuleAfterlife.PERM_HP));
        if (respawnHP != null)
            e.player.setHealth(respawnHP);

        Integer respawnFood = FunctionHelper.tryParseInt(APIRegistry.perms.getUserPermissionProperty(UserIdent.get(e.player), ModuleAfterlife.PERM_FOOD));
        if (respawnFood != null)
            e.player.getFoodStats().addStats(-1 * (20 - respawnFood), 0);
    }
    
}