package com.forgeessentials.serverNetwork.packetbase;

import java.util.ArrayList;

import com.forgeessentials.serverNetwork.packetbase.packets.Packet0ClientValidation;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet10SharedCommandSending;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet11SharedCommandResponse;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet1ServerValidationResponse;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet2ClientNewConnectionData;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet3ClientConnectionData;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet4ServerPasswordResponce;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet5SharedCloseSession;

public class FEPacketManager
{
    private PacketHandler packetHandler;
    private final ArrayList<FEPacket> packetTypes;

    public FEPacketManager(PacketHandler packethandler)
    {
        packetTypes = new ArrayList<>();
        this.packetHandler = packethandler;
        
        //Client packets
        packetTypes.add(new Packet0ClientValidation());
        packetTypes.add(new Packet1ServerValidationResponse());
        packetTypes.add(new Packet2ClientNewConnectionData());
        packetTypes.add(new Packet3ClientConnectionData());
        packetTypes.add(new Packet4ServerPasswordResponce());
        packetTypes.add(new Packet5SharedCloseSession());
        //Placeholder for packets 6-9
        packetTypes.add(new Packet10SharedCommandSending());
        packetTypes.add(new Packet11SharedCommandResponse());
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
