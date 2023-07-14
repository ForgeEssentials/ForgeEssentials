package com.forgeessentials.serverNetwork.packetbase.packets;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.PacketBuffer;

public class Packet2ClientNewConnectionData extends FEPacket {

    private String clientId;

    public Packet2ClientNewConnectionData() {}

    public Packet2ClientNewConnectionData(String clientId) {
        this.clientId = clientId;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    @Override
    public void encode(PacketBuffer buf) {
        buf.writeUtf(clientId);
    }

    @Override
    public void decode(PacketBuffer buf) {
        clientId = buf.readUtf();
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    @Override
    public int getID() {
        return 2;
    }
}