package com.forgeessentials.compat;

import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class CompatReiMinimap {
    private static final String base = "fe.reimm.compat";

    public static final String cavemap = base + ".cavemap";
    public static final String radarPlayer = base + ".radarPlayer";
    public static final String radarAnimal = base + ".radarAnimal";
    public static final String radarMod = base + ".radarMod";
    public static final String radarSlime = base + ".radarSlime";
    public static final String radarSquid = base + ".radarSquid";
    public static final String radarOther = base + ".radarOther";

    @SubscribeEvent
    public void registerPerms(FEModuleServerInitEvent e)
    {
        PermissionsManager.registerPermission(cavemap, RegisteredPermValue.TRUE);
        PermissionsManager.registerPermission(radarAnimal, RegisteredPermValue.TRUE);
        PermissionsManager.registerPermission(radarMod, RegisteredPermValue.TRUE);
        PermissionsManager.registerPermission(radarOther, RegisteredPermValue.TRUE);
        PermissionsManager.registerPermission(radarPlayer, RegisteredPermValue.TRUE);
        PermissionsManager.registerPermission(radarSlime, RegisteredPermValue.TRUE);
        PermissionsManager.registerPermission(radarOther, RegisteredPermValue.TRUE);
    }

    public static String reimotd(EntityPlayer username)
    {
        try
        {
            String MOTD = "\u00a7e\u00a7f";

            if (PermissionsManager.checkPermission(username, cavemap))
            {
                MOTD = "\u00a77" + MOTD;
            }
            if (PermissionsManager.checkPermission(username, radarSquid))
            {
                MOTD = "\u00a76" + MOTD;
            }
            if (PermissionsManager.checkPermission(username, radarSlime))
            {
                MOTD = "\u00a75" + MOTD;
            }
            if (PermissionsManager.checkPermission(username, radarMod))
            {
                MOTD = "\u00a74" + MOTD;
            }
            if (PermissionsManager.checkPermission(username, radarAnimal))
            {
                MOTD = "\u00a73" + MOTD;
            }
            if (PermissionsManager.checkPermission(username, radarPlayer))
            {
                MOTD = "\u00a72" + MOTD;
            }
            if (PermissionsManager.checkPermission(username, cavemap))
            {
                MOTD = "\u00a71" + MOTD;
            }

            MOTD = "\u00a70\u00a70" + MOTD;

            return MOTD;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

}
