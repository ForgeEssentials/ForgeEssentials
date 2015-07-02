package com.forgeessentials.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

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
        PermissionsManager.registerPermission(PERM_CAVEMAP, RegisteredPermValue.TRUE);
        PermissionsManager.registerPermission(PERM_RADAR_ANIMAL, RegisteredPermValue.TRUE);
        PermissionsManager.registerPermission(PERM_RADAR_MOD, RegisteredPermValue.TRUE);
        PermissionsManager.registerPermission(PERM_RADAR_OTHER, RegisteredPermValue.TRUE);
        PermissionsManager.registerPermission(PERM_RADAR_PLAYER, RegisteredPermValue.TRUE);
        PermissionsManager.registerPermission(PERM_RADAR_SLIME, RegisteredPermValue.TRUE);
        PermissionsManager.registerPermission(PERM_RADAR_OTHER, RegisteredPermValue.TRUE);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
    {
        ChatOutputHandler.sendMessage(e.player, new ChatComponentText(getPermissionCodes(e.player)));
    }

    public static String getPermissionCodes(EntityPlayer user)
    {
        String MOTD = "\u00a7e\u00a7f";
        if (PermissionsManager.checkPermission(user, PERM_CAVEMAP))
        {
            MOTD = "\u00a77" + MOTD;
        }
        if (PermissionsManager.checkPermission(user, PERM_RADAR_SQUID))
        {
            MOTD = "\u00a76" + MOTD;
        }
        if (PermissionsManager.checkPermission(user, PERM_RADAR_SLIME))
        {
            MOTD = "\u00a75" + MOTD;
        }
        if (PermissionsManager.checkPermission(user, PERM_RADAR_MOD))
        {
            MOTD = "\u00a74" + MOTD;
        }
        if (PermissionsManager.checkPermission(user, PERM_RADAR_ANIMAL))
        {
            MOTD = "\u00a73" + MOTD;
        }
        if (PermissionsManager.checkPermission(user, PERM_RADAR_PLAYER))
        {
            MOTD = "\u00a72" + MOTD;
        }
        if (PermissionsManager.checkPermission(user, PERM_CAVEMAP))
        {
            MOTD = "\u00a71" + MOTD;
        }
        MOTD = "\u00a70\u00a70" + MOTD;
        return MOTD;
    }

}
