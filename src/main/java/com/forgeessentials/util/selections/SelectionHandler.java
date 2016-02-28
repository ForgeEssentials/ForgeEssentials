package com.forgeessentials.util.selections;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet1SelectionUpdate;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.events.FEPlayerEvent.ClientHandshakeEstablished;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SelectionHandler extends ServerEventHandler
{

    public static ISelectionProvider selectionProvider = new PlayerInfoSelectionProvider();

    @SubscribeEvent
    public void onClientConnect(final ClientHandshakeEstablished e)
    {
        TaskRegistry.runLater(new Runnable() {
            @Override
            public void run()
            {
                sendUpdate(e.getPlayer());
            }
        });
    }

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

        WorldPoint point = new WorldPoint(player.dimension, event.pos);

        // left Click
        if (event.action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
        {
            SelectionHandler.setStart((EntityPlayerMP) event.entityPlayer, point);
            String message = Translator.format("Pos1 set to %d, %d, %d", event.pos.getX(), event.pos.getY(), event.pos.getZ());
            ChatOutputHandler.sendMessage(player, message, EnumChatFormatting.DARK_PURPLE);
            event.setCanceled(true);
        }
        // right Click
        else if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
        {
            SelectionHandler.setEnd((EntityPlayerMP) event.entityPlayer, point);
            String message = Translator.format("Pos2 set to %d, %d, %d", event.pos.getX(), event.pos.getY(), event.pos.getZ());
            ChatOutputHandler.sendMessage(player, message, EnumChatFormatting.DARK_PURPLE);
            event.setCanceled(true);
        }
    }

    public static void sendUpdate(EntityPlayerMP player)
    {
        if (PlayerInfo.get(player).getHasFEClient())
        {
            try
            {
                NetworkUtils.netHandler.sendTo(new Packet1SelectionUpdate(selectionProvider.getSelection(player)), player);
            }
            catch (NullPointerException e)
            {
                LoggingHandler.felog.error("Error sending selection update to player");
            }
        }
    }

    public static Selection getSelection(EntityPlayerMP player)
    {
        return selectionProvider.getSelection(player);
    }

    public static void setDimension(EntityPlayerMP player, int dim)
    {
        selectionProvider.setDimension(player, dim);
    }

    public static void setStart(EntityPlayerMP player, Point start)
    {
        selectionProvider.setStart(player, start);
    }

    public static void setEnd(EntityPlayerMP player, Point end)
    {
        selectionProvider.setEnd(player, end);
    }

    public static void select(EntityPlayerMP player, int dimension, AreaBase area)
    {
        selectionProvider.select(player, dimension, area);
    }

}
