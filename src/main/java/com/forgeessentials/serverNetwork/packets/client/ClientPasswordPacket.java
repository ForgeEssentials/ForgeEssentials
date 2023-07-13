package com.forgeessentials.serverNetwork.packets.client;

import com.forgeessentials.serverNetwork.packets.FEPacket;
import com.forgeessentials.serverNetwork.packets.PacketHandler;

import net.minecraft.network.PacketBuffer;

public class ClientPasswordPacket extends FEPacket {

    private String password;

    public ClientPasswordPacket() {}

    public ClientPasswordPacket(String password) {
        this.password = password;
    }
    
    public String getPassword() {
        return password;
    }
    
    @Override
    public void encode(PacketBuffer buf) {
        buf.writeUtf(password);
    }

    @Override
    public void decode(PacketBuffer buf) {
        password = buf.readUtf();
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    @Override
    public int getID() {
        return 1;
    }
}