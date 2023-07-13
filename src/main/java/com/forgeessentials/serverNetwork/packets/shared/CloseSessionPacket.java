package com.forgeessentials.serverNetwork.packets.shared;

import com.forgeessentials.serverNetwork.packets.FEPacket;
import com.forgeessentials.serverNetwork.packets.PacketHandler;

import net.minecraft.network.PacketBuffer;

public class CloseSessionPacket extends FEPacket {

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
        return 0;
    }
}
