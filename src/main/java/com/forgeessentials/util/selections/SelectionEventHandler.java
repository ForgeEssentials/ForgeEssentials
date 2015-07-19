package com.forgeessentials.util.selections;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.events.ServerEventHandler;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SelectionEventHandler extends ServerEventHandler
{

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        // Only handle server events
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        // get info now rather than later
        EntityPlayer player = event.entityPlayer;
        PlayerInfo info = PlayerInfo.get(player);

        if (!info.isWandEnabled())
            return;

        // Check if wand should activate
        if (player.getCurrentEquippedItem() == null)
        {
            if (info.getWandID() != "hands")
                return;
        }
        else
        {
            if (!(player.getCurrentEquippedItem().getItem().getUnlocalizedName().equals(info.getWandID())))
                return;
            if (player.getCurrentEquippedItem().getItemDamage() != info.getWandDmg())
                return;
        }

        WorldPoint point = new WorldPoint(player.dimension, event.x, event.y, event.z);

        // left Click
        if (event.action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
        {
            SelectionHandler.selectionProvider.setStart((EntityPlayerMP) event.entityPlayer, point);
            String message = Translator.format("Pos1 set to %d, %d, %d", event.x, event.y, event.z);
            ChatOutputHandler.sendMessage(player, message, EnumChatFormatting.DARK_PURPLE);
            event.setCanceled(true);
        }
        // right Click
        else if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
        {
            SelectionHandler.selectionProvider.setEnd((EntityPlayerMP) event.entityPlayer, point);
            String message = Translator.format("Pos2 set to %d, %d, %d", event.x, event.y, event.z);
            ChatOutputHandler.sendMessage(player, message, EnumChatFormatting.DARK_PURPLE);
            event.setCanceled(true);
        }
    }

}
