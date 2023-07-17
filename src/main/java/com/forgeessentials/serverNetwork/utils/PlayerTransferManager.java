package com.forgeessentials.serverNetwork.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet06AuthLogin;
import com.forgeessentials.commons.network.packets.Packet10ClientTransfer;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerAboutToStartEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.events.player.FEPlayerEvent.ClientHandshakeEstablished;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class PlayerTransferManager extends ServerEventHandler
{
    protected static final Set<UUID> onlinePlayers = Collections.synchronizedSet(new HashSet<>());
    @Override
    @SubscribeEvent
    public void serverAboutToStart(FEModuleServerAboutToStartEvent event)
    {
        super.serverAboutToStart(event);
    }

    @Override
    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent e)
    {
        super.serverStopped(e);
    }

    @SubscribeEvent
    public void onClientHandshake(ClientHandshakeEstablished e)
    {
        onlinePlayers.add(e.getPlayer().getUUID());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        onlinePlayers.remove(event.getPlayer().getUUID());
    }

    public void sendPlayerTo(PlayerEntity player, String server) {
        NetworkUtils.sendTo(new Packet10ClientTransfer(server,server), (ServerPlayerEntity) player);
    }
    
    public void sendAllPlayersTo(String server) {
        for(PlayerEntity p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            sendPlayerTo(p, server);
        }
    }
}
