package com.forgeessentials.serverNetwork.server.packets.clientTypes;

import java.nio.charset.StandardCharsets;

import com.forgeessentials.serverNetwork.server.packets.Packet;
import com.forgeessentials.serverNetwork.server.packets.PacketDirection;
import com.forgeessentials.serverNetwork.server.packets.PacketType;

public class ClientPasswordPacket extends Packet {
    private String password;

    public ClientPasswordPacket(String password) {
        super(PacketType.PASSWORD, PacketDirection.Client_To_Server);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public byte[] encode() {
        return getPassword().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public ClientPasswordPacket decode(byte[] data) {
        String password = new String(data, StandardCharsets.UTF_8);
        return new ClientPasswordPacket(password);
    }
}
