package com.forgeessentials.serverNetwork.packetbase.packets;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.FriendlyByteBuf;

public class Packet02ClientNewConnectionData extends FEPacket {

    private String clientId;

    public Packet02ClientNewConnectionData() {}

    public Packet02ClientNewConnectionData(String clientId) {
        this.clientId = clientId;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(clientId);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
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