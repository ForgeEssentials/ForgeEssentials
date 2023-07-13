package com.forgeessentials.serverNetwork.server.packets.clientTypes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ClientValidationPacket{
    private String channelName;
    private int versionNumber;

    public ClientValidationPacket(String channelName, int versionNumber) {
        this.channelName = channelName;
        this.versionNumber = versionNumber;
    }

    public byte[] encode() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(13);
        byteBuffer.put(channelName.getBytes(StandardCharsets.UTF_8));
        byteBuffer.putInt(versionNumber);
        return byteBuffer.array();
    }
}
