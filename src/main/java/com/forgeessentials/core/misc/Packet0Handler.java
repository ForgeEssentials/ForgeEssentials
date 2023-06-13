package com.forgeessentials.core.misc;

import com.forgeessentials.commons.network.packets.Packet0Handshake;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class Packet0Handler extends Packet0Handshake
{
    public Packet0Handler(){}

    public static Packet0Handler decode(PacketBuffer buf)
    {
        return new Packet0Handler();
    }
    @Override
    public void handle(Context context)
    {
        PlayerInfo.get(context.getSender()).setHasFEClient(true);
        LoggingHandler.felog.info(Translator.format("Recieved Handshake packet from %s", context.getSender().getDisplayName().getString()));
    }
}
