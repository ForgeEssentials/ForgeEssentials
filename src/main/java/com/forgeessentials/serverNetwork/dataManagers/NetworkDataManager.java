package com.forgeessentials.serverNetwork.dataManagers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet10ClientTransfer;
import com.forgeessentials.serverNetwork.ModuleNetworking;
import com.forgeessentials.serverNetwork.utils.ServerType;
import com.forgeessentials.serverNetwork.utils.ConnectionData.ConnectedClientData;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerAboutToStartEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class NetworkDataManager extends ServerEventHandler
{
    protected final Set<UUID> onlinePlayers = new HashSet<>();
    protected final Map<UUID, String> incommongPlayers = new HashMap<>();
    protected final Map<UUID, String> outgoingPlayers = new HashMap<>();

    public NetworkDataManager(FEModuleServerAboutToStartEvent event)
    {
        super.serverAboutToStart(event);
    }

    @Override
    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent e)
    {
        super.serverStopped(e);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedInEvent event) {
        //onlinePlayers.remove(event.getPlayer().getUUID());
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        //onlinePlayers.remove(event.getPlayer().getUUID());
    }

    public void sendPlayerToServer(PlayerEntity player, String serverName) {
        if(ModuleNetworking.getInstance().getServerType()==ServerType.ROOTSERVER) {
            for (Entry<String, ConnectedClientData> arg : ModuleNetworking.getClients().entrySet())
            {
                if(arg.getKey().equals(serverName)&&arg.getValue().isAuthenticated()) {
                    sendPlayerToAddress(player, arg.getValue().getAddressNameAndPort(), serverName);
                }
            }
        }
        if(ModuleNetworking.getInstance().getServerType()==ServerType.CLIENTSERVER) {
            if(ModuleNetworking.getLocalClient().isAuthenticated()&&ModuleNetworking.getLocalClient().getRemoteServerId().equals(serverName)) {
                sendPlayerToAddress(player, ModuleNetworking.getLocalClient().getRemoteServerAddressNameAndPort(), serverName);
            }
        }
    }
    public void sendPlayerToAddress(PlayerEntity player, String server, String serverName) {
        if(PlayerInfo.get(player).getHasFEClient()) {
            NetworkUtils.sendTo(new Packet10ClientTransfer(server, serverName, null, null, true), (ServerPlayerEntity) player);
        }
    }
    public void sendAllPlayersToAddress(String server, String serverName) {
        for(PlayerEntity p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            sendPlayerToAddress(p, server, serverName);
        }
    }

    public void sendPlayerFallback(PlayerEntity player, String serverFallback, String serverFallbackName) {
        if(PlayerInfo.get(player).getHasFEClient()) {
            NetworkUtils.sendTo(new Packet10ClientTransfer(null, null, serverFallback, serverFallbackName, false), (ServerPlayerEntity) player);
        }
    }
    public void sendAllPlayersFallback(String serverFallback, String serverFallbackName) {
        for(PlayerEntity p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            sendPlayerFallback(p, serverFallback, serverFallbackName);
        }
    }

    public Map<UUID, String> getIncommongPlayers()
    {
        return incommongPlayers;
    }

    public Map<UUID, String> getOutgoingPlayers()
    {
        return outgoingPlayers;
    }

    public Set<UUID> getOnlinePlayers()
    {
        return onlinePlayers;
    }
}
