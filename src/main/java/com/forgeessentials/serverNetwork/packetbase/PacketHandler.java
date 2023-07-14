package com.forgeessentials.serverNetwork.packetbase;

import com.forgeessentials.serverNetwork.packetbase.packets.Packet0ClientValidation;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet1ServerValidationResponce;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet2ClientPassword;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet3ServerPasswordResponce;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet4SharedCloseSession;
import com.forgeessentials.serverNetwork.server.FENetworkServer;
import com.forgeessentials.util.output.logger.LoggingHandler;

import io.netty.channel.Channel;

public interface PacketHandler {

    default void handle(Channel channel, Object obj) {
        if(obj instanceof FEPacket) {
            FEPacket packet = (FEPacket) obj;
            packet.setChannel(channel);
            packet.handle(this);
        } else {
            if(FENetworkServer.getInstance().getConnectedChannels().get(channel)){
                throw new IllegalArgumentException("The received object is not a packet! Object Type: " + obj.getClass().getName());
            }
            LoggingHandler.felog.error("Skipped unValidated non packet");
        }
    }
    default void handle(Object obj) {
        if(obj instanceof FEPacket) {
            FEPacket packet = (FEPacket) obj;
            packet.handle(this);
        } else
            throw new IllegalArgumentException("The received object is not a packet! Object Type: " + obj.getClass().getName());
    }

    default void handle(Packet0ClientValidation responcePacket) {
        LoggingHandler.felog.error("Packet0ClientValidation unhandled");
    }

    default void handle(Packet1ServerValidationResponce passwordPacket) {
        LoggingHandler.felog.error("Packet1ServerValidationResponce unhandled");
    };

    default void handle(Packet2ClientPassword passwordPacket) {
        LoggingHandler.felog.error("Packet2ClientPassword unhandled");
    };
    
    default void handle(Packet3ServerPasswordResponce responcePacket) {
        LoggingHandler.felog.error("Packet3ServerPasswordResponce unhandled");
    }

    default void handle(Packet4SharedCloseSession closePacket) {
        LoggingHandler.felog.error("Packet4SharedCloseSession unhandled");
    };

}
