package com.forgeessentials.serverNetwork.client;

import com.forgeessentials.serverNetwork.packetbase.PacketHandler;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet1ServerValidationResponce;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet2ClientPassword;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet3ServerPasswordResponce;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet4SharedCloseSession;

public class ClientPacketHandler implements PacketHandler
{

    @Override
    public void handle(Packet1ServerValidationResponce validationResponce)
    {
        FENetworkClient.getInstance().sendPacket(new Packet2ClientPassword("passowrd"));
    }

    @Override
    public void handle(Packet3ServerPasswordResponce passwordPacket)
    {
        System.out.println("Received login confirmation: " + passwordPacket.isAuthenticated());
    }

    @Override
    public void handle(Packet4SharedCloseSession closeSession)
    {
        System.out.println("Received close orders");
    }
}
