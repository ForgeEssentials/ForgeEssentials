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
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.events.PlayerChangedZone;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PermissionEventHandler {

    public PermissionEventHandler()
    {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onZoneChange(PlayerChangedZone event)
    {
        String query1 = APIRegistry.perms.getPermissionProperty(new UserIdent(event.entityPlayer), event.beforeZone, FEPermissions.ZONE_EXIT_MESSAGE);
        if (query1 != null)
        {
            OutputHandler.sendMessage(event.entityPlayer, FunctionHelper.formatColors(query1));
        }
        String query2 = APIRegistry.perms.getPermissionProperty(new UserIdent(event.entityPlayer), event.afterZone, FEPermissions.ZONE_ENTRY_MESSAGE);
        if (query2 != null)
        {
            OutputHandler.sendMessage(event.entityPlayer, FunctionHelper.formatColors(query2));
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