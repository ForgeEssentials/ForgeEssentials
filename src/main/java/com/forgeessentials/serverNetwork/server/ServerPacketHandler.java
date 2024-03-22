package com.forgeessentials.serverNetwork.server;

import java.util.Map.Entry;

import com.forgeessentials.serverNetwork.ModuleNetworking;
import com.forgeessentials.serverNetwork.NetworkClientSendingOnParentCommandSender;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet00ClientValidation;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet10SharedCommandSending;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet11SharedCommandResponse;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet12ServerPlayerSync;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet14ClientPlayerSync;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet01ServerValidationResponse;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet02ClientNewConnectionData;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet03ClientConnectionData;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet04ServerConnectionData;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet05SharedCloseSession;
import com.forgeessentials.serverNetwork.utils.ConnectionData.ConnectedClientData;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.forgeessentials.serverNetwork.utils.EncryptionUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

public class ServerPacketHandler implements PacketHandler
{

    @Override
    public void handle(Packet00ClientValidation responcePacket) {
     // Validate the connection
        if(responcePacket.getChannelName().equals(FENetworkServer.getInstance().getChannelNameM())) {
            if(responcePacket.getChannelVersion()==FENetworkServer.getInstance().getChannelVersionM()) {
                // Connection is valid, send Validation packet
                FENetworkServer.getInstance().getConnectedChannels().replace(responcePacket.getChannel(), true);
                FENetworkServer.getInstance().sendPacketFor(responcePacket.getChannel(), new Packet01ServerValidationResponse(ModuleNetworking.getLocalServer().getLocalServerId()));
                return;
            }
            else if(responcePacket.getChannelVersion()<FENetworkServer.getInstance().getChannelVersionM()){
                LoggingHandler.felog.error("FENetworkServer Client tried joining with an outdated channel version! Closing connection.");
            }
            else if(responcePacket.getChannelVersion()>FENetworkServer.getInstance().getChannelVersionM()) {
                LoggingHandler.felog.error("FENetworkServer Client tried joining with a channel version from the future! Closing connection.");
            }
            else {
                LoggingHandler.felog.error("FENetworkServer Client tried joining with mismatched channel version! Closing connection.");

            }
        }
        LoggingHandler.felog.error("FENetworkServer Invalid connection detected! Closing connection.");
        String errorMessage = "Invalid protocol detected trying to access this ForgeEssentials Server Network!\n"
                + "This protocol can only be used for connecting between two servers running ForgeEssentials 16.0.0+";
        ByteBuf errorBuffer = Unpooled.copiedBuffer(errorMessage, CharsetUtil.UTF_8);
        responcePacket.getChannel().writeAndFlush(errorBuffer);
        FENetworkServer.getInstance().getBlockedChannels().add(responcePacket.getChannel());
        responcePacket.getChannel().flush();
        responcePacket.getChannel().close();
    }
 
    @Override
    public void handle(Packet02ClientNewConnectionData newClientData)
    {
        if(!ModuleNetworking.getClients().containsKey(newClientData.getClientId())) {
            ConnectedClientData data = new ConnectedClientData(newClientData.getClientId());
            String privateKey = EncryptionUtils.generatePrivateKey();
            data.setPrivateKey(privateKey);
            ModuleNetworking.getClients().put(newClientData.getClientId(), data);
            LoggingHandler.felog.info("FENetworkServer Connected a new client: "+ newClientData.getClientId());
            LoggingHandler.felog.info("FENetworkServer Copy the privateKey from RemoteFENetworkClientData.json into the client/other server's LocalFENetworkClientData.json");
            newClientData.getChannel().close();
        }
        else {
            LoggingHandler.felog.error("FENetworkServer new client tried joining with alredy known clientId: "+ newClientData.getClientId());
            LoggingHandler.felog.error("FENetworkServer kicking client");
            newClientData.getChannel().close();
        }
    }

    @Override
    public void handle(Packet03ClientConnectionData clientData)
    {
        ConnectedClientData data = ModuleNetworking.getClients().getOrDefault(clientData.getClientId(), null);
        if(data==null) {
            LoggingHandler.felog.error("FENetworkServer failed to find a client with the clientId: "+ clientData.getClientId());
            LoggingHandler.felog.error("FENetworkServer kicking client");
            clientData.getChannel().close();
            return;
        }
        if(data.getPassword().equals("notSet")) {
            LoggingHandler.felog.error("FENetworkServer You need to manualy enter the password of FENetworkClient: " + clientData.getClientId()
            +" into the RemoteFENetworkClientData.json file before you can connect!");
            clientData.getChannel().close();
            LoggingHandler.felog.debug("FENetworkServer Needs to add FENetworkClient password");
            return;
        }
        data.setCurrentChannel(clientData.getChannel());
        try
        {
            if(EncryptionUtils.decryptString(clientData.getEncryptedPassword(), data.getPrivateKey()).equals(data.getPassword())) {
                if(ModuleNetworking.getLocalServer().getAddressNameAndPort().equals("notSet")) {
                    LoggingHandler.felog.error("FENetworkServer server public ip/hostname and port are not set!");
                    LoggingHandler.felog.error("FENetworkServer set this in the format \"hostname:port\"");
                    clientData.getChannel().close();
                    return;
                }
                FENetworkServer.getInstance().sendPacketFor(clientData.getChannel(), new Packet04ServerConnectionData(true, ModuleNetworking.getLocalServer().isDisableClientOnlyConnections(), ModuleNetworking.getLocalServer().getAddressNameAndPort()));
                data.setAuthenticated(true);
                data.incrementNumberTimesConnected();
                data.setAddressNameAndPort(clientData.getAddress());
                ModuleNetworking.getClients().put(clientData.getClientId(), data);
                LoggingHandler.felog.debug("FENetworkServer Client authenticated");
                return;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            LoggingHandler.felog.error("FENetworkServer Failed to send PasswordResponce FENetworkServer!");
            clientData.getChannel().close();
            return;
        }
        FENetworkServer.getInstance().sendPacketFor(clientData.getChannel(), new Packet04ServerConnectionData(false, true, "notSet"));
    }

    @Override
    public void handle(Packet05SharedCloseSession closeSession)
    {
        System.out.println("FENetworkServer Received close orders");
    }

    @Override
    public void handle(Packet10SharedCommandSending commandPacket)
    {
        ConnectedClientData data1=null;
        for (Entry<String, ConnectedClientData> data : ModuleNetworking.getClients().entrySet()) {
            if(data.getValue().getCurrentChannel()!=null) {
                if(data.getValue().getCurrentChannel().equals(commandPacket.getChannel())) {
                    data1 = data.getValue();
                    break;
                }
            }
        }
        if(data1==null) {
            LoggingHandler.felog.error("FENetworkServer Failed to find client for command channel!");
            return;
        }
        if(data1.getPermissionLevel()>=2) {
            ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(
                    new NetworkClientSendingOnParentCommandSender(data1.getRemoteClientId()).createCommandSourceStack(), 
                    commandPacket.getCommandToSend());
        }
        else {
            LoggingHandler.felog.error("FENetworkServer client "+data1.getRemoteClientId()+" does not have permissions to send commands!");
        }
    }

    @Override
    public void handle(Packet11SharedCommandResponse commandResponce)
    {
        LoggingHandler.felog.info("CommandResponse from client: "+commandResponce.getCommandResponse());
    }

    @Override
    public void handle(Packet14ClientPlayerSync sync)
    {
        if(sync.loggedIn()) {
            ModuleNetworking.getInstance().getTranferManager().onlinePlayers.add(sync.getPlayerUuid());
        }
        else {
            ModuleNetworking.getInstance().getTranferManager().onlinePlayers.remove(sync.getPlayerUuid());
        }
        for (Entry<String, ConnectedClientData> data : ModuleNetworking.getClients().entrySet()) {
            if(data.getValue().getCurrentChannel()!=sync.getChannel()&&data.getValue().isAuthenticated()) {
                ModuleNetworking.getInstance().getServer().sendPacketFor(data.getValue().getCurrentChannel(), new Packet12ServerPlayerSync(ModuleNetworking.getInstance().getTranferManager().onlinePlayers));
            }
        }
    }
}
