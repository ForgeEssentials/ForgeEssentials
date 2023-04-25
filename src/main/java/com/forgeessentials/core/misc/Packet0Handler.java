package com.forgeessentials.core.misc;

import com.forgeessentials.commons.network.packets.Packet0Handshake;
import com.forgeessentials.util.PlayerInfo;

import net.minecraftforge.fml.network.NetworkEvent.Context;

public class Packet0Handler extends Packet0Handshake
{

    @Override
    public void handle(Context context)
    {
        PlayerInfo.get(context.getSender()).setHasFEClient(true);
        context.setPacketHandled(true);
    }
}
