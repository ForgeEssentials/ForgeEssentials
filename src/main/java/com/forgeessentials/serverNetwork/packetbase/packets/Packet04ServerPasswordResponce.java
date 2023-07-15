package com.forgeessentials.serverNetwork.packetbase.packets;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.PacketBuffer;

public class Packet04ServerPasswordResponce extends FEPacket {

    private boolean authenticated;

    public Packet04ServerPasswordResponce() {}

    public Packet04ServerPasswordResponce(boolean authenticated) {
        this.authenticated = authenticated;
    }
    
    public boolean isAuthenticated() {
        return authenticated;
    }
    
    @Override
    public void encode(PacketBuffer buf) {
        buf.writeBoolean(authenticated);
    }

    @Override
    public void decode(PacketBuffer buf) {
        authenticated = buf.readBoolean();
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    @Override
    public int getID() {
        return 4;
    }
}