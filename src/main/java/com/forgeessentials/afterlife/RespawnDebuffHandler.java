package com.forgeessentials.afterlife;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.permissions.PermissionsManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class RespawnDebuffHandler {
    public static final String BYPASSPOTION = ModuleAfterlife.BASEPERM + ".bypassPotions";
    public static final String BYPASSSTATS = ModuleAfterlife.BASEPERM + ".bypassStats";
    public static ArrayList<PotionEffect> potionEffects;
    public static int hp;
    public static int food;

    public RespawnDebuffHandler()
    {
        FMLCommonHandler.instance().bus().register(this);
    }
    
    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e)
    {
        if (e.player.worldObj.isRemote)
        {
            return;
        }
        EntityPlayer player = e.player;
        if (!PermissionsManager.checkPermission(player, BYPASSPOTION))
        {
            for (PotionEffect effect : potionEffects)
            {
                player.addPotionEffect(effect);
            }
        }
        if (!PermissionsManager.checkPermission(player, BYPASSSTATS))
        {
            player.getFoodStats().addStats(-1 * (20 - food), 0);
            player.setHealth(hp);
        }
    }
    
}