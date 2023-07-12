package com.forgeessentials.serverNetwork.server.packets;

public abstract class Packet {
    private final PacketType type;
    private final PacketDirection direction;

    public Packet(PacketType type, PacketDirection direction) {
        this.type = type;
        this.direction = direction;
    }

    public PacketType getType() {
        return type;
    }

    public abstract byte[] encode();

    public abstract Packet decode(byte[] data);

    public PacketDirection getDirection()
    {
        return direction;
    }
}
