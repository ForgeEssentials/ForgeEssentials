package com.forgeessentials.serverNetwork.dataManagers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet10ClientTransfer;
import com.forgeessentials.serverNetwork.ModuleNetworking;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet12ServerPlayerSync;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet13SharedPlayerTransfer;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet14ClientPlayerSync;
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
    public Set<UUID> onlinePlayers = new HashSet<>();
    public Set<UUID> incommongPlayers = new HashSet<>();

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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedInEvent event) {
        if(ModuleNetworking.getInstance().getServerType()==ServerType.ROOTSERVER) {
            onlinePlayers.add(event.getPlayer().getUUID());
            syncPlayerList();
        }
        if(ModuleNetworking.getInstance().getServerType()==ServerType.CLIENTSERVER) {
            onlinePlayers.add(event.getPlayer().getUUID());
            sendClientEventToServer(event.getPlayer().getUUID(), true);
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if(ModuleNetworking.getInstance().getServerType()==ServerType.ROOTSERVER) {
            onlinePlayers.remove(event.getPlayer().getUUID());
            syncPlayerList();
        }
        if(ModuleNetworking.getInstance().getServerType()==ServerType.CLIENTSERVER) {
            onlinePlayers.remove(event.getPlayer().getUUID());
            sendClientEventToServer(event.getPlayer().getUUID(), false);
        }
    }

    public void sendPlayerToServer(PlayerEntity player, String serverName) {
        if(ModuleNetworking.getInstance().getServerType()==ServerType.ROOTSERVER) {
            for (Entry<String, ConnectedClientData> arg : ModuleNetworking.getClients().entrySet())
            {
                if(arg.getKey().equals(serverName)&&arg.getValue().isAuthenticated()) {
                    ModuleNetworking.getInstance().getServer().sendPacketFor(arg.getValue().getCurrentChannel(), new Packet13SharedPlayerTransfer(player.getGameProfile().getId()));
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

    public static void syncPlayerList() {
        ModuleNetworking.getInstance().getServer().sendAllPacket(new Packet12ServerPlayerSync(ModuleNetworking.getInstance().getTranferManager().onlinePlayers));
    }

    public static void sendClientEventToServer(UUID player, boolean loggedIn) {
        ModuleNetworking.getInstance().getClient().sendPacket(new Packet14ClientPlayerSync(player, loggedIn));
    }
}
