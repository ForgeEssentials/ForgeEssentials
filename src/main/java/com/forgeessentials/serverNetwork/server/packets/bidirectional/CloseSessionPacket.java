package com.forgeessentials.serverNetwork.server.packets.bidirectional;

import com.forgeessentials.serverNetwork.server.packets.Packet;
import com.forgeessentials.serverNetwork.server.packets.PacketDirection;
import com.forgeessentials.serverNetwork.server.packets.PacketType;

public class CloseSessionPacket extends Packet {
    public CloseSessionPacket() {
        super(PacketType.CLOSE_SESSION, PacketDirection.BiDirectional);
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    @Override
    public CloseSessionPacket decode(byte[] data) {
        return new CloseSessionPacket();
    }
}
