package com.forgeessentials.serverNetwork.packets;

import net.minecraft.network.PacketBuffer;

public abstract class FEPacket extends BasePacket {

    public abstract void encode(PacketBuffer buf);

    public abstract void decode(PacketBuffer buf);

    public abstract void handle(PacketHandler packetHandler);

    public abstract int getID();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FEPacket && ((FEPacket) obj).getID() == getID();
    }
}
