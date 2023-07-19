package com.forgeessentials.serverNetwork.packetbase;

import com.forgeessentials.serverNetwork.packetbase.packets.Packet00ClientValidation;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet10SharedCommandSending;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet11SharedCommandResponse;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet01ServerValidationResponse;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet02ClientNewConnectionData;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet03ClientConnectionData;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet04ServerConnectionData;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet05SharedCloseSession;
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

    //TODO change to private void in java 9+
    default void handleNoGet(FEPacket packet) {
        LoggingHandler.felog.error(packet.getClass().getSimpleName()+" unhandled");
    }

    default void handle(Packet00ClientValidation packet) {
        handleNoGet(packet);
    }

    default void handle(Packet01ServerValidationResponse packet) {
        handleNoGet(packet);
    }

    default void handle(Packet02ClientNewConnectionData packet) {
        handleNoGet(packet);
    }
    
    default void handle(Packet03ClientConnectionData packet) {
        handleNoGet(packet);
    }

    default void handle(Packet04ServerConnectionData packet) {
        handleNoGet(packet);
    }

    default void handle(Packet05SharedCloseSession packet) {
        handleNoGet(packet);
    }

    default void handle(Packet10SharedCommandSending packet) {
        handleNoGet(packet);
    }

    default void handle(Packet11SharedCommandResponse packet) {
        handleNoGet(packet);
    }
}
