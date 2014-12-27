package com.forgeessentials.permissions.core;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.server.CommandHandlerForge;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.events.PlayerChangedZone;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PermissionEventHandler extends ServerEventHandler {
    
    @SubscribeEvent
    public void playerMoveEvent(PlayerMoveEvent e)
    {
        // Abort processing, if the event has already been cancelled
        if (!e.isCanceled())
        {
            Zone before = APIRegistry.perms.getServerZone().getZonesAt(e.before.toWorldPoint()).get(0);
            Zone after = APIRegistry.perms.getServerZone().getZonesAt(e.after.toWorldPoint()).get(0);
            if (!before.equals(after))
            {
                PlayerChangedZone event = new PlayerChangedZone(e.entityPlayer, before, after, e.before, e.after);
                e.setCanceled(MinecraftForge.EVENT_BUS.post(event));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerChangedZoneEvent(PlayerChangedZone event)
    {
        UserIdent ident = new UserIdent(event.entityPlayer);
        String exitMsg = APIRegistry.perms.getUserPermissionProperty(ident, event.beforeZone, FEPermissions.ZONE_EXIT_MESSAGE);
        if (exitMsg != null)
        {
            OutputHandler.sendMessage(event.entityPlayer, FunctionHelper.formatColors(exitMsg));
        }
        String entryMsg = APIRegistry.perms.getUserPermissionProperty(ident, event.afterZone, FEPermissions.ZONE_ENTRY_MESSAGE);
        if (entryMsg != null)
        {
            OutputHandler.sendMessage(event.entityPlayer, FunctionHelper.formatColors(entryMsg));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkCommandPerm(CommandEvent e)
    {
        if (!(e.command instanceof ForgeEssentialsCommandBase) && e.sender instanceof EntityPlayer && !CommandHandlerForge.canUse(e.command, e.sender))
        {
            e.setCanceled(true);
            permissionDeniedMessage(e.sender);
        }
    }

    public static void permissionDeniedMessage(ICommandSender sender)
    {
        ChatComponentTranslation msg = new ChatComponentTranslation("commands.generic.permission", new Object[0]);
        msg.getChatStyle().setColor(EnumChatFormatting.RED);
        sender.addChatMessage(msg);
    }

}