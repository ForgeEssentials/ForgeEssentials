package com.forgeessentials.serverNetwork.packetbase;

import java.util.ArrayList;

import com.forgeessentials.serverNetwork.packetbase.packets.Packet0ClientValidation;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet2ClientPassword;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet4SharedCloseSession;

public class FEPacketManager
{
    private PacketHandler packetHandler;
    private final ArrayList<FEPacket> packetTypes;

    public FEPacketManager(PacketHandler packethandler)
    {
        packetTypes = new ArrayList<>();
        this.packetHandler = packethandler;
        
        //Client packets
        packetTypes.add(new Packet2ClientPassword());
        packetTypes.add(new Packet0ClientValidation());
        //Server packets
        //Shared Packets
        packetTypes.add(new Packet4SharedCloseSession());
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
