package com.forgeessentials.serverNetwork.server;

import com.forgeessentials.serverNetwork.ModuleNetworking;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet0ClientValidation;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet1ServerValidationResponce;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet2ClientNewConnectionData;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet3ClientConnectionData;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet4ServerPasswordResponce;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet5SharedCloseSession;
import com.forgeessentials.serverNetwork.utils.ConnectionData.ConnectedClientData;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.forgeessentials.serverNetwork.utils.EncryptionUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class ServerPacketHandler implements PacketHandler
{

    @Override
    public void handle(Packet0ClientValidation responcePacket) {
     // Validate the connection
        if(responcePacket.getChannelName().equals(FENetworkServer.getInstance().getChannelNameM())) {
            if(responcePacket.getChannelVersion()==FENetworkServer.getInstance().getChannelVersionM()) {
                // Connection is valid, send Validation packet
                FENetworkServer.getInstance().getConnectedChannels().replace(responcePacket.getChannel(), true);
                FENetworkServer.getInstance().sendPacketFor(responcePacket.getChannel(), new Packet1ServerValidationResponce(ModuleNetworking.getLocalServer().getLocalServerId()));
                return;
            }
            LoggingHandler.felog.error("FENetworkServer Client tried joining with mismatched channel version! Closing connection.");
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
    public void handle(Packet2ClientNewConnectionData newClientData)
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
    public void handle(Packet3ClientConnectionData clientData)
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
                FENetworkServer.getInstance().sendPacketFor(clientData.getChannel(), new Packet4ServerPasswordResponce(true));
                data.setAuthenticated(true);
                data.incrementNumberTimesConnected();
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
        data.setAuthenticated(false);
        ModuleNetworking.getClients().put(clientData.getClientId(), data);
        FENetworkServer.getInstance().sendPacketFor(clientData.getChannel(), new Packet4ServerPasswordResponce(false));
    }

    @Override
    public void handle(Packet5SharedCloseSession closeSession)
    {
        System.out.println("FENetworkServer Received close orders");
    }
}
