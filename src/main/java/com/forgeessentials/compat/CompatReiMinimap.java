package com.forgeessentials.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

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
        PermissionAPI.registerNode(PERM_CAVEMAP, DefaultPermissionLevel.ALL, "Allow cavemaps");
        PermissionAPI.registerNode(PERM_RADAR_ANIMAL, DefaultPermissionLevel.ALL, "Allow animal radar");
        PermissionAPI.registerNode(PERM_RADAR_MOD, DefaultPermissionLevel.ALL, "Allow mod radars");
        PermissionAPI.registerNode(PERM_RADAR_OTHER, DefaultPermissionLevel.ALL, "Allow other radars");
        PermissionAPI.registerNode(PERM_RADAR_PLAYER, DefaultPermissionLevel.ALL, "Allow player radars");
        PermissionAPI.registerNode(PERM_RADAR_SLIME, DefaultPermissionLevel.ALL, "Allow slime radars");
        PermissionAPI.registerNode(PERM_RADAR_OTHER, DefaultPermissionLevel.ALL, "Allow other radars");
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
    {
        ChatOutputHandler.sendMessage(e.player, new TextComponentString(getPermissionCodes(e.player)));
    }

    public static String getPermissionCodes(EntityPlayer user)
    {
        String MOTD = "\u00a7e\u00a7f";
        if (PermissionAPI.hasPermission(user, PERM_CAVEMAP))
        {
            MOTD = "\u00a77" + MOTD;
        }
        if (PermissionAPI.hasPermission(user, PERM_RADAR_SQUID))
        {
            MOTD = "\u00a76" + MOTD;
        }
        if (PermissionAPI.hasPermission(user, PERM_RADAR_SLIME))
        {
            MOTD = "\u00a75" + MOTD;
        }
        if (PermissionAPI.hasPermission(user, PERM_RADAR_MOD))
        {
            MOTD = "\u00a74" + MOTD;
        }
        if (PermissionAPI.hasPermission(user, PERM_RADAR_ANIMAL))
        {
            MOTD = "\u00a73" + MOTD;
        }
        if (PermissionAPI.hasPermission(user, PERM_RADAR_PLAYER))
        {
            MOTD = "\u00a72" + MOTD;
        }
        if (PermissionAPI.hasPermission(user, PERM_CAVEMAP))
        {
            MOTD = "\u00a71" + MOTD;
        }
        MOTD = "\u00a70\u00a70" + MOTD;
        return MOTD;
    }

}
