package com.forgeessentials.serverNetwork.server;

import com.forgeessentials.serverNetwork.packets.PacketHandler;
import com.forgeessentials.serverNetwork.packets.client.ClientPasswordPacket;
import com.forgeessentials.serverNetwork.packets.server.ServerPasswordResponcePacket;
import com.forgeessentials.serverNetwork.packets.shared.CloseSessionPacket;

public class ServerPacketHandler implements PacketHandler
{


    @Override
    public void handle(CloseSessionPacket packetPing)
    {
        System.out.println("Received close orders");
    }

    @Override
    public void handle(ClientPasswordPacket passwordPacket)
    {
        System.out.println("Received password: " + passwordPacket.getPassword());
    }
    
    @Override
    public void handle(ServerPasswordResponcePacket passwordPacket)
    {
        System.out.println("Received confirmation: " + passwordPacket.isAuthenticated());
    }

}
