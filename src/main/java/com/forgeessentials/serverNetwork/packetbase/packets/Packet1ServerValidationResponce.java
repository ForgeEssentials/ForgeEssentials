package com.forgeessentials.serverNetwork.packetbase.packets;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.PacketBuffer;

public class Packet1ServerValidationResponce extends FEPacket {

    @Override
    public void encode(PacketBuffer buf) {}

    @Override
    public void decode(PacketBuffer buf) {}

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    @Override
    public int getID() {
        return 1;
    }
}
