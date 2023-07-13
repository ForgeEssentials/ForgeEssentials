package com.forgeessentials.serverNetwork.packets;

import com.forgeessentials.serverNetwork.packets.client.ClientPasswordPacket;
import com.forgeessentials.serverNetwork.packets.server.ServerPasswordResponcePacket;
import com.forgeessentials.serverNetwork.packets.shared.CloseSessionPacket;

import io.netty.channel.Channel;

public interface PacketHandler {

    default void handle(Channel channel, Object obj) {
        if(obj instanceof FEPacket) {
            FEPacket packet = (FEPacket) obj;
            packet.setChannel(channel);
            packet.handle(this);
        } else
            throw new IllegalArgumentException("The received object is not a packet! Object Type: " + obj.getClass().getName());
    }
    default void handle(Object obj) {
        if(obj instanceof FEPacket) {
            FEPacket packet = (FEPacket) obj;
            packet.handle(this);
        } else
            throw new IllegalArgumentException("The received object is not a packet! Object Type: " + obj.getClass().getName());
    }

    default void handle(CloseSessionPacket closePacket) {
        System.out.println("CloseSessionPacket unhandled");
    };

    default void handle(ClientPasswordPacket passwordPacket) {
        System.out.println("ClientPasswordPacket unhandled");
    };
    
    default void handle(ServerPasswordResponcePacket responcePacket) {
        System.out.println("ServerPasswordResponcePacket unhandled");
    }
}
