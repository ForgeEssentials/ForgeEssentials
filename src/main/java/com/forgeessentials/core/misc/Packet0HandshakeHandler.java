package com.forgeessentials.core.misc;

import com.forgeessentials.commons.network.packets.Packet00Handshake;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;

public class Packet0HandshakeHandler extends Packet00Handshake
{
    public Packet0HandshakeHandler()
    {
    }

    public static Packet0HandshakeHandler decode(FriendlyByteBuf buf)
    {
        return new Packet0HandshakeHandler();
    }

    @Override
    public void handle(Context context)
    {
        PlayerInfo.get(context.getSender()).setHasFEClient(true);
        LoggingHandler.felog.info(Translator.format("Recieved Handshake packet from %s",
                context.getSender().getDisplayName().getString()));
    }
}
