package com.forgeessentials.permissions;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.query.PropQueryPlayerZone;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.events.PlayerChangedZone;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.server.CommandHandlerForge;

public class PermsEventHandler {

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onZoneChange(PlayerChangedZone event)
    {
        PropQueryPlayerZone query1 = new PropQueryPlayerZone(event.entityPlayer, "fe.perm.zone.exit", event.beforeZone, false);
        PropQueryPlayerZone query2 = new PropQueryPlayerZone(event.entityPlayer, "fe.perm.zone.entry", event.afterZone, false);

        APIRegistry.perms.getPermissionProp(query1);
        if (query1.hasValue())
        {
            ChatUtils.sendMessage(event.entityPlayer, FunctionHelper.formatColors(query1.getStringValue()));
        }

        APIRegistry.perms.getPermissionProp(query2);
        if (query2.hasValue())
        {
            ChatUtils.sendMessage(event.entityPlayer, FunctionHelper.formatColors(query2.getStringValue()));
        }

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkCommandPerm(CommandEvent e)
    {
        if (!(e.sender instanceof EntityPlayer)) {
            return;
        } else if (!CommandHandlerForge.canUse(e.command, e.sender)) {
            e.setCanceled(true);
        }
    }
}
