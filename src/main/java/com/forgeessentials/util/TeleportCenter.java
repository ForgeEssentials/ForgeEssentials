package com.forgeessentials.util;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.selections.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Use this for all TPs. This system does it all for you: warmup, cooldown,
 * bypass for both, going between dimensions.
 *
 * @author Dries007
 */

public class TeleportCenter {
    public static final String BYPASS_WARMUP = "fe.teleport.bypasswarmup";
    public static final String BYPASS_COOLDOWN = "fe.teleport.bypasscooldown";
    public static int tpWarmup;
    public static int tpCooldown;
    private static ArrayList<TPdata> queue = new ArrayList<TPdata>();
    private static ArrayList<TPdata> removeQueue = new ArrayList<TPdata>();

    public static void addToTpQue(WarpPoint point, EntityPlayer player)
    {
        if (PlayerInfo.getPlayerInfo(player.getPersistentID()).TPcooldown != 0 && !PermissionsManager.checkPerm(player, BYPASS_COOLDOWN))
        {
            ChatUtils.sendMessage(player,
                    String.format("Cooldown still active. %s seconds to go.",
                            FunctionHelper.parseTime(PlayerInfo.getPlayerInfo(player.getPersistentID()).TPcooldown)));
        }
        else
        {
            PlayerInfo.getPlayerInfo(player.getPersistentID()).TPcooldown = tpCooldown;
            TPdata data = new TPdata(point, player);
            if (tpWarmup == 0 || PermissionsManager.checkPerm(player, BYPASS_WARMUP))
            {
                data.doTP();
            }
            else
            {
                ChatUtils.sendMessage(player, String.format("Teleporting, please stand still for %s seconds.", FunctionHelper.parseTime(tpWarmup)));
                queue.add(data);
            }
        }
    }

    public static void abort(TPdata tpData)
    {
        removeQueue.add(tpData);
        ChatUtils.sendMessage(tpData.getPlayer(), "Teleport cancelled.");
    }

    public static void TPdone(TPdata tpData)
    {
        removeQueue.add(tpData);
        ChatUtils.sendMessage(tpData.getPlayer(), "Teleported.");
    }

    @SubscribeEvent
    public void tickStart(TickEvent.ServerTickEvent e)
    {
        for (TPdata data : queue)
        {
            data.count();
        }
        queue.removeAll(removeQueue);
        removeQueue.clear();
        for (Object player : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
        {
            PlayerInfo.getPlayerInfo(((EntityPlayer) player).getPersistentID()).TPcooldownTick();
        }
    }

}
