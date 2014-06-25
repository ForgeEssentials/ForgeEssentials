package com.forgeessentials.afterlife;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

public class RespawnDebuffHandler{
    public static final String BYPASSPOTION = ModuleAfterlife.BASEPERM + ".bypassPotions";
    public static final String BYPASSSTATS = ModuleAfterlife.BASEPERM + ".bypassStats";
    public static ArrayList<PotionEffect> potionEffects;
    public static int hp;
    public static int food;

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e)
    {
        if (e.player.worldObj.isRemote)
        {
            return;
        }
        EntityPlayer player = e.player;
        if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(player, BYPASSPOTION)))
        {
            for (PotionEffect effect : potionEffects)
            {
                player.addPotionEffect(effect);
            }
        }
        if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(player, BYPASSSTATS)))
        {
            player.getFoodStats().addStats(-1 * (20 - food), 0);
            player.setHealth(hp);
        }
    }
}