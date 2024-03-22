package com.forgeessentials.util.selections;

import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet01SelectionUpdate;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.events.player.FEPlayerEvent.ClientHandshakeEstablished;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.ChatFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

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
                sendUpdate((ServerPlayer) e.getPlayer());
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent.LeftClickBlock event)
    {
        // Only handle server events
        if (FMLEnvironment.dist.isClient())
            return;

        // get info now rather than later
        Player player = event.getPlayer();
        PlayerInfo info = PlayerInfo.get(player);

        if (!info.isWandEnabled())
            return;

        // Check if wand should activate
        if (player.getMainHandItem() == null)
        {
            if (!info.getWandID().equals("hands"))
                return;
        }
        else
        {
            if (!(player.getMainHandItem().getItem().getRegistryName().getPath().equals(info.getWandID())))
                return;
        }

        WorldPoint point = new WorldPoint(player.level, event.getPos());

        SelectionHandler.setStart((ServerPlayer) event.getPlayer(), point);
        SelectionHandler.setDimension((ServerPlayer) event.getPlayer(), point.getDimension());
        String message = Translator.format("Pos1 set to %d, %d, %d", event.getPos().getX(), event.getPos().getY(),
                event.getPos().getZ());
        ChatOutputHandler.sendMessage(player.createCommandSourceStack(), message, ChatFormatting.DARK_PURPLE);
        SelectionHandler.sendUpdate((ServerPlayer) event.getPlayer());
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent.RightClickBlock event)
    {
        // Only handle server events
        if (FMLEnvironment.dist.isClient())
            return;

        // get info now rather than later
        Player player = event.getPlayer();
        PlayerInfo info = PlayerInfo.get(player);

        if (!info.isWandEnabled() || event.getHand() == InteractionHand.OFF_HAND)
        {
            return;
        }

        // Check if wand should activate
        if (player.getMainHandItem() == null)
        {
            if (!info.getWandID().equals("hands"))
                return;
        }
        else
        {
            if (!(player.getMainHandItem().getItem().getRegistryName().getPath().equals(info.getWandID())))
                return;
        }

        WorldPoint point = new WorldPoint(player.level, event.getPos());

        SelectionHandler.setEnd((ServerPlayer) event.getPlayer(), point);
        SelectionHandler.setDimension((ServerPlayer) event.getPlayer(), point.getDimension());
        String message = Translator.format("Pos2 set to %d, %d, %d", event.getPos().getX(), event.getPos().getY(),
                event.getPos().getZ());
        ChatOutputHandler.sendMessage(player.createCommandSourceStack(), message, ChatFormatting.DARK_PURPLE);
        SelectionHandler.sendUpdate((ServerPlayer) event.getPlayer());
        event.setCanceled(true);

    }

    public static void sendUpdate(ServerPlayer player)
    {
        if (PlayerInfo.get(player).getHasFEClient())
        {
            try
            {
                NetworkUtils.sendTo(new Packet01SelectionUpdate(selectionProvider.getSelection(player)), player);
            }
            catch (NullPointerException e)
            {
                LoggingHandler.felog.error("Error sending selection update to player");
            }
        }
    }

    public static Selection getSelection(ServerPlayer player)
    {
        return selectionProvider.getSelection(player);
    }

    public static void setDimension(ServerPlayer player, String dim)
    {
        selectionProvider.setDimension(player, dim);
    }

    public static void setStart(ServerPlayer player, Point start)
    {
        selectionProvider.setStart(player, start);
    }

    public static void setEnd(ServerPlayer player, Point end)
    {
        selectionProvider.setEnd(player, end);
    }

    public static void select(ServerPlayer player, String dimension, AreaBase area)
    {
        selectionProvider.select(player, dimension, area);
    }

}
