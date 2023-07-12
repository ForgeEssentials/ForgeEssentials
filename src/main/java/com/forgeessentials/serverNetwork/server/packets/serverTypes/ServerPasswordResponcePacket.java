package com.forgeessentials.serverNetwork.server.packets.serverTypes;

import java.nio.ByteBuffer;

import com.forgeessentials.serverNetwork.server.packets.Packet;
import com.forgeessentials.serverNetwork.server.packets.PacketDirection;
import com.forgeessentials.serverNetwork.server.packets.PacketType;

public class ServerPasswordResponcePacket extends Packet {
    private boolean authenticated;

    public ServerPasswordResponcePacket(boolean authenticated) {
        super(PacketType.SERVER_PASSWORD_RESPONSE, PacketDirection.Server_To_Client);
        this.authenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public byte[] encode() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1);
        byteBuffer.put(authenticated ? (byte) 1 : (byte) 0);
        return byteBuffer.array();
    }

    @Override
    public ServerPasswordResponcePacket decode(byte[] data) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        boolean authenticated = byteBuffer.get() != 0;
        return new ServerPasswordResponcePacket(authenticated);
    }
}
