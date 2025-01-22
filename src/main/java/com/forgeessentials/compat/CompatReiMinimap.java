package com.forgeessentials.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.output.ChatOutputHandler;





public class CompatReiMinimap
{

    public static final String PERM = "fe.reimm.compat";
    public static final String PERM_CAVEMAP = PERM + ".cavemap";
    public static final String PERM_RADAR = PERM + ".radar";
    public static final String PERM_RADAR_PLAYER = PERM_RADAR + ".player";
    public static final String PERM_RADAR_ANIMAL = PERM_RADAR + ".animal";
    public static final String PERM_RADAR_MOD = PERM_RADAR + ".mod";
    public static final String PERM_RADAR_SLIME = PERM_RADAR + ".slime";
    public static final String PERM_RADAR_SQUID = PERM_RADAR + ".squid";
    public static final String PERM_RADAR_OTHER = PERM_RADAR + ".other";

    @SubscribeEvent
    public void registerPerms(FEModuleServerInitEvent e)
    {
        APIRegistry.perms.registerPermissionDescription(PERM, "Rei's minimap permissions");
        PermissionManager.registerPermission(PERM_CAVEMAP, PermissionLevel.TRUE);
        PermissionManager.registerPermission(PERM_RADAR_ANIMAL, PermissionLevel.TRUE);
        PermissionManager.registerPermission(PERM_RADAR_MOD, PermissionLevel.TRUE);
        PermissionManager.registerPermission(PERM_RADAR_OTHER, PermissionLevel.TRUE);
        PermissionManager.registerPermission(PERM_RADAR_PLAYER, PermissionLevel.TRUE);
        PermissionManager.registerPermission(PERM_RADAR_SLIME, PermissionLevel.TRUE);
        PermissionManager.registerPermission(PERM_RADAR_OTHER, PermissionLevel.TRUE);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
    {
        ChatOutputHandler.sendMessage(e.player, new ChatComponentText(getPermissionCodes(e.player)));
    }

    public static String getPermissionCodes(EntityPlayer user)
    {
        String MOTD = "\u00a7e\u00a7f";
        if (PermissionManager.checkPermission(user, PERM_CAVEMAP))
        {
            MOTD = "\u00a77" + MOTD;
        }
        if (PermissionManager.checkPermission(user, PERM_RADAR_SQUID))
        {
            MOTD = "\u00a76" + MOTD;
        }
        if (PermissionManager.checkPermission(user, PERM_RADAR_SLIME))
        {
            MOTD = "\u00a75" + MOTD;
        }
        if (PermissionManager.checkPermission(user, PERM_RADAR_MOD))
        {
            MOTD = "\u00a74" + MOTD;
        }
        if (PermissionManager.checkPermission(user, PERM_RADAR_ANIMAL))
        {
            MOTD = "\u00a73" + MOTD;
        }
        if (PermissionManager.checkPermission(user, PERM_RADAR_PLAYER))
        {
            MOTD = "\u00a72" + MOTD;
        }
        if (PermissionManager.checkPermission(user, PERM_CAVEMAP))
        {
            MOTD = "\u00a71" + MOTD;
        }
        MOTD = "\u00a70\u00a70" + MOTD;
        return MOTD;
    }

}
