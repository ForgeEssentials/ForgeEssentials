package com.forgeessentials.serverNetwork.packetbase.packets;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.PacketBuffer;

public class Packet1ServerValidationResponse extends FEPacket {

    String serverId;

    public Packet1ServerValidationResponse(){}

    public Packet1ServerValidationResponse(String serverId){
        this.serverId = serverId;
    }
    @Override
    public void encode(PacketBuffer buf) {
        buf.writeUtf(serverId);
    }

    @Override
    public void decode(PacketBuffer buf) {
        serverId = buf.readUtf();
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    @Override
    public int getID() {
        return 1;
    }

    public String getServerId()
    {
        return serverId;
    }
}
