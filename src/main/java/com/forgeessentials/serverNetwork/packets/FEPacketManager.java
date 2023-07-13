package com.forgeessentials.serverNetwork.packets;

import java.util.ArrayList;

import com.forgeessentials.serverNetwork.packets.client.ClientPasswordPacket;
import com.forgeessentials.serverNetwork.packets.shared.CloseSessionPacket;

public class FEPacketManager
{
    private PacketHandler packetHandler;
    private final ArrayList<FEPacket> packetTypes;

    public FEPacketManager(PacketHandler packethandler)
    {
        packetTypes = new ArrayList<>();
        this.packetHandler = packethandler;
        
        //Client packets
        packetTypes.add(new ClientPasswordPacket());
        //Server packets
        //Shared Packets
        packetTypes.add(new CloseSessionPacket());
    }
    
    public int getPacketId(FEPacket packet) {
        return packetTypes.indexOf(packet);
    }

    public FEPacket getPacket(int id) {
        for(FEPacket packet : packetTypes) {
            if(packet.getID()==id) {
                return packet;
            }
        }
        throw new IllegalArgumentException("Packet with id " + id + " not found!");
    }

    public ArrayList<FEPacket> getPackets() {
        return packetTypes;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public void setPacketHandler(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }
}
