package com.forgeessentials.serverNetwork.packetbase.packets;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.PacketBuffer;

public class Packet2ClientPassword extends FEPacket {

    private String password;

    public Packet2ClientPassword() {}

    public Packet2ClientPassword(String password) {
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
        return 2;
    }
}