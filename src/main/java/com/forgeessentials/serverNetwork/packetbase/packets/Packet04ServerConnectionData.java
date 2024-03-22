package com.forgeessentials.serverNetwork.packetbase.packets;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.FriendlyByteBuf;

public class Packet04ServerConnectionData extends FEPacket {

    private boolean authenticated;
    private boolean disableClientOnlyConnections;
    private String address;

    public Packet04ServerConnectionData() {}

    public Packet04ServerConnectionData(boolean authenticated, boolean disableClientOnlyConnections, String address) {
        this.authenticated = authenticated;
        this.disableClientOnlyConnections = disableClientOnlyConnections;
        this.address = address;
    }
    
    public boolean isAuthenticated() {
        return authenticated;
    }
    
    public String getAddress(){
        return address;
    }

    public boolean isDisableClientOnlyConnections(){
        return disableClientOnlyConnections;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(authenticated);
        buf.writeBoolean(disableClientOnlyConnections);
        buf.writeUtf(address);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        authenticated = buf.readBoolean();
        disableClientOnlyConnections = buf.readBoolean();
        address = buf.readUtf();
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