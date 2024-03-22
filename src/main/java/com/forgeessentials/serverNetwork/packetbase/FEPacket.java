package com.forgeessentials.serverNetwork.packetbase;

import net.minecraft.network.FriendlyByteBuf;

public abstract class FEPacket extends BasePacket {

    public abstract void encode(FriendlyByteBuf buf);

    public abstract void decode(FriendlyByteBuf buf);

    public abstract void handle(PacketHandler packetHandler);

    public abstract int getID();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FEPacket && ((FEPacket) obj).getID() == getID();
    }
}
