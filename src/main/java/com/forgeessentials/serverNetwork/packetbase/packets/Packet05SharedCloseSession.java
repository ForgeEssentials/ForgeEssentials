package com.forgeessentials.serverNetwork.packetbase.packets;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.PacketBuffer;

public class Packet05SharedCloseSession extends FEPacket {

    String reason;
    
    public Packet05SharedCloseSession() {
        reason="";
    }
    
    public Packet05SharedCloseSession(String reason) {
        this.reason = reason;
    }
    @Override
    public void encode(PacketBuffer buf) {
        buf.writeUtf(reason);
    }

    @Override
    public void decode(PacketBuffer buf) {
        reason = buf.readUtf();
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    @Override
    public int getID() {
        return 5;
    }

    public String getReason()
    {
        return reason;
    }
}
