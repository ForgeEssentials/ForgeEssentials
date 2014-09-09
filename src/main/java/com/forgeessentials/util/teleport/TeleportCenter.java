package com.forgeessentials.util.teleport;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.PlayerInfo;
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
    
    private static int teleportWarmup;
    private static int teleportCooldown;
    
    private static ArrayList<TeleportData> queue = new ArrayList<TeleportData>();
    private static ArrayList<TeleportData> removeQueue = new ArrayList<TeleportData>();

    public static void addToTpQue(WarpPoint point, EntityPlayer player)
    {
        if (PlayerInfo.getPlayerInfo(player.getPersistentID()).getTeleportCooldown() != 0 && !PermissionsManager.checkPerm(player, BYPASS_COOLDOWN))
        {
            ChatUtils.sendMessage(player,
                    String.format("Cooldown still active. %s seconds to go.",
                            FunctionHelper.parseTime(PlayerInfo.getPlayerInfo(player.getPersistentID()).getTeleportCooldown())));
        }
        else
        {
            PlayerInfo.getPlayerInfo(player.getPersistentID()).setTeleportCooldown(teleportCooldown);
            TeleportData data = new TeleportData(point, player);
            if (teleportWarmup == 0 || PermissionsManager.checkPerm(player, BYPASS_WARMUP))
            {
                data.teleport();
            }
            else
            {
                ChatUtils.sendMessage(player, String.format("Teleporting, please stand still for %s seconds.", FunctionHelper.parseTime(teleportWarmup)));
                queue.add(data);
            }
        }
    }

    public static void abort(TeleportData tpData)
    {
        removeQueue.add(tpData);
        ChatUtils.sendMessage(tpData.getPlayer(), "Teleport cancelled.");
    }

    public static void TPdone(TeleportData tpData)
    {
        removeQueue.add(tpData);
        ChatUtils.sendMessage(tpData.getPlayer(), "Teleported.");
    }

    @SubscribeEvent
    public void tickStart(TickEvent.ServerTickEvent e)
    {
        for (TeleportData data : queue)
        {
            data.count();
        }
        queue.removeAll(removeQueue);
        removeQueue.clear();
        for (Object player : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
        {
        	PlayerInfo pi = PlayerInfo.getPlayerInfo((EntityPlayer) player);
        	pi.setTeleportCooldown(pi.getTeleportCooldown() - 1);
        }
    }

	public static int getTeleportWarmup()
	{
		return teleportWarmup;
	}

	public static void setTeleportWarmup(int teleportWarmup)
	{
		TeleportCenter.teleportWarmup = teleportWarmup;
	}
	
	public static int getTeleportCooldown()
	{
		return teleportCooldown;
	}



	public static void setTeleportCooldown(int teleportCooldown)
	{
		TeleportCenter.teleportCooldown = teleportCooldown;
	}

}
