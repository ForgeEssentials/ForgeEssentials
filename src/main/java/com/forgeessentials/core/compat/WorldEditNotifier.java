package com.forgeessentials.core.compat;

import cpw.mods.fml.common.FMLCommonHandler;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class WorldEditNotifier
{
    public static final String NO_WORLDEDIT_NOTIFY_PERM = "fe.compat.worldedit.notify";
    public static final String SK_CURSEFORGE = "http://minecraft.curseforge.com/mc-mods/225608-worldedit";

    public WorldEditNotifier()
    {
        if (!ForgeEssentials.worldEditCompatilityPresent && !Environment.hasWorldEdit())
        {
            FMLCommonHandler.instance().bus().register(this);
            FunctionHelper.FE_INTERNAL_EVENTBUS.register(this);
        }

    }

    @SubscribeEvent
    public void registerPerms(FEModuleServerInitEvent e)
    {
        PermissionsManager.registerPermission(NO_WORLDEDIT_NOTIFY_PERM, RegisteredPermValue.OP);
    }

    public void notify(PlayerLoggedInEvent e)
    {
        if (PermissionsManager.checkPermission(e.player, NO_WORLDEDIT_NOTIFY_PERM))
        {
            OutputHandler.chatNotification(e.player, "You seem to have installed WEIntegrationTools without installing WorldEdit.");
            OutputHandler.chatNotification(e.player, "Download WorldEdit from here: " + SK_CURSEFORGE);
        }
    }
}
