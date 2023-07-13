package com.forgeessentials.serverNetwork.packets.server;

import com.forgeessentials.serverNetwork.packets.FEPacket;
import com.forgeessentials.serverNetwork.packets.PacketHandler;

import net.minecraft.network.PacketBuffer;

public class ServerPasswordResponcePacket extends FEPacket {

    private boolean authenticated;

    public ServerPasswordResponcePacket() {}

    public ServerPasswordResponcePacket(boolean authenticated) {
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
        return 2;
    }
}