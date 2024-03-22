package com.forgeessentials.compat;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CompatReiMinimap
{
    // TODO determine if this should be removed
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
    public void registerPerms(FEModuleServerStartingEvent e)
    {
        APIRegistry.perms.registerPermissionDescription(PERM, "Rei's minimap permissions");
        APIRegistry.perms.registerNode(PERM_CAVEMAP, DefaultPermissionLevel.ALL, "Allow cavemaps");
        APIRegistry.perms.registerNode(PERM_RADAR_ANIMAL, DefaultPermissionLevel.ALL, "Allow animal radar");
        APIRegistry.perms.registerNode(PERM_RADAR_MOD, DefaultPermissionLevel.ALL, "Allow mod radars");
        APIRegistry.perms.registerNode(PERM_RADAR_OTHER, DefaultPermissionLevel.ALL, "Allow other radars");
        APIRegistry.perms.registerNode(PERM_RADAR_PLAYER, DefaultPermissionLevel.ALL, "Allow player radars");
        APIRegistry.perms.registerNode(PERM_RADAR_SLIME, DefaultPermissionLevel.ALL, "Allow slime radars");
        APIRegistry.perms.registerNode(PERM_RADAR_OTHER, DefaultPermissionLevel.ALL, "Allow other radars");
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
    {
        ChatOutputHandler.sendMessage(e.getPlayer().createCommandSourceStack(),
                new TextComponent(getPermissionCodes(e.getPlayer()) + "Weird stuff be afoot"));
    }

    public static String getPermissionCodes(Player user)
    {
        String MOTD = "\u00a7e\u00a7f";
        if (APIRegistry.perms.checkPermission(user, PERM_CAVEMAP))
        {
            MOTD = "\u00a77" + MOTD;
        }
        if (APIRegistry.perms.checkPermission(user, PERM_RADAR_SQUID))
        {
            MOTD = "\u00a76" + MOTD;
        }
        if (APIRegistry.perms.checkPermission(user, PERM_RADAR_SLIME))
        {
            MOTD = "\u00a75" + MOTD;
        }
        if (APIRegistry.perms.checkPermission(user, PERM_RADAR_MOD))
        {
            MOTD = "\u00a74" + MOTD;
        }
        if (APIRegistry.perms.checkPermission(user, PERM_RADAR_ANIMAL))
        {
            MOTD = "\u00a73" + MOTD;
        }
        if (APIRegistry.perms.checkPermission(user, PERM_RADAR_PLAYER))
        {
            MOTD = "\u00a72" + MOTD;
        }
        if (APIRegistry.perms.checkPermission(user, PERM_CAVEMAP))
        {
            MOTD = "\u00a71" + MOTD;
        }
        MOTD = "\u00a70\u00a70" + MOTD;
        return MOTD;
    }

}
