package com.forgeessentials.serverNetwork.packetbase.packets;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.FriendlyByteBuf;

public class Packet01ServerValidationResponse extends FEPacket {

    String serverId;

    public Packet01ServerValidationResponse(){}

    public Packet01ServerValidationResponse(String serverId){
        this.serverId = serverId;
    }
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(serverId);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
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
