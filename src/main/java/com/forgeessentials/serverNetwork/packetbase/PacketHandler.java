package com.forgeessentials.serverNetwork.packetbase;

import com.forgeessentials.serverNetwork.packetbase.packets.Packet0ClientValidation;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet1ServerValidationResponce;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet2ClientNewConnectionData;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet3ClientConnectionData;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet4ServerPasswordResponce;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet5SharedCloseSession;
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

    default void handle(Packet0ClientValidation packet) {
        LoggingHandler.felog.error("Packet0ClientValidation unhandled");
    }

    default void handle(Packet1ServerValidationResponce packet) {
        LoggingHandler.felog.error("Packet1ServerValidationResponce unhandled");
    };

    default void handle(Packet2ClientNewConnectionData packet) {
        LoggingHandler.felog.error("Packet2ClientNewConnectionData unhandled");
    };
    
    default void handle(Packet3ClientConnectionData packet) {
        LoggingHandler.felog.error("Packet3ClientPassword unhandled");
    }

    default void handle(Packet4ServerPasswordResponce packet) {
        LoggingHandler.felog.error("Packet4ServerPasswordResponce unhandled");
    }

    default void handle(Packet5SharedCloseSession packet) {
        LoggingHandler.felog.error("Packet5SharedCloseSession unhandled");
    };

}