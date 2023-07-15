package com.forgeessentials.serverNetwork.packetbase.packets;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.PacketBuffer;

public class Packet03ClientConnectionData extends FEPacket {

    private String clientId;
    private String encryptedPassword;

    public Packet03ClientConnectionData() {}

    public Packet03ClientConnectionData(String clientId, String encryptedPassword) {
        this.clientId = clientId;
        this.encryptedPassword = encryptedPassword;
    }
    
    public String getClientId() {
        return clientId;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeUtf(clientId);
        buf.writeUtf(encryptedPassword);
    }

    @Override
    public void decode(PacketBuffer buf) {
        clientId = buf.readUtf();
        encryptedPassword = buf.readUtf();
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    @Override
    public int getID() {
        return 3;
    }
}
