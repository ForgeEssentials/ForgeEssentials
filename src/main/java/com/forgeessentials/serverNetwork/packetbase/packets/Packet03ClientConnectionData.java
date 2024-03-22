package com.forgeessentials.serverNetwork.packetbase.packets;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.FriendlyByteBuf;

public class Packet03ClientConnectionData extends FEPacket {

    private String clientId;
    private String encryptedPassword;
    private String address;

    public Packet03ClientConnectionData() {}

    public Packet03ClientConnectionData(String clientId, String encryptedPassword, String address) {
        this.clientId = clientId;
        this.encryptedPassword = encryptedPassword;
        this.address = address;
    }
    
    public String getClientId() {
        return clientId;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public String getAddress(){
        return address;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(clientId);
        buf.writeUtf(encryptedPassword);
        buf.writeUtf(address);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        clientId = buf.readUtf();
        encryptedPassword = buf.readUtf();
        address = buf.readUtf();
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
