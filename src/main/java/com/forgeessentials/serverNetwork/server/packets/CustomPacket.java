package com.forgeessentials.serverNetwork.server.packets;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class CustomPacket extends Packet {
    private String stringValue;
    private int intValue;
    private boolean booleanValue;

    public CustomPacket(String stringValue, int intValue, boolean booleanValue) {
        super(PacketType.CUSTOM, PacketDirection.BiDirectional);
        this.stringValue = stringValue;
        this.intValue = intValue;
        this.booleanValue = booleanValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }

    @Override
    public byte[] encode() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(13 + getStringValue().length());
        byteBuffer.putInt(getIntValue());
        byteBuffer.put((byte) (getBooleanValue() ? 1 : 0));
        byteBuffer.putInt(getStringValue().length());
        byteBuffer.put(getStringValue().getBytes(StandardCharsets.UTF_8));
        return byteBuffer.array();
    }

    @Override
    public CustomPacket decode(byte[] data) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        int intValue = byteBuffer.getInt();
        boolean booleanValue = byteBuffer.get() != 0;
        int stringLength = byteBuffer.getInt();
        byte[] stringBytes = new byte[stringLength];
        byteBuffer.get(stringBytes);
        String stringValue = new String(stringBytes, StandardCharsets.UTF_8);
        return new CustomPacket(stringValue, intValue, booleanValue);
    }
}
