package com.forgeessentials.serverNetwork.server.packets;

import java.nio.charset.StandardCharsets;

public class DataPacket extends Packet {
    private String data;

    public DataPacket(String data) {
        super(PacketType.DATA, PacketDirection.BiDirectional);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public byte[] encode() {
        return getData().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public DataPacket decode(byte[] data) {
        String dataString = new String(data, StandardCharsets.UTF_8);
        return new DataPacket(dataString);
    }
}