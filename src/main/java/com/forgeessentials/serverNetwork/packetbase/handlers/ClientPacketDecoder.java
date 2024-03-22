package com.forgeessentials.serverNetwork.packetbase.handlers;

import java.io.IOException;
import java.util.List;

import com.forgeessentials.serverNetwork.client.FENetworkClient;
import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.util.output.logger.LoggingHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.minecraft.network.FriendlyByteBuf;

public class ClientPacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        FriendlyByteBuf packetBuffer = new FriendlyByteBuf(byteBuf);

        if (packetBuffer.readableBytes() < 1) {
            return;
        }

        int packetID = packetBuffer.readVarInt();
        FEPacket packet = FENetworkClient.getInstance().getPacketManager().getPacket(packetID);

        LoggingHandler.felog.debug("FENetworkClient [IN] " + packetID + " " + packet.getClass().getSimpleName());
        packet.decode(packetBuffer);

        if (packetBuffer.readableBytes() > 0) {
            throw new IOException("Packet  (" + packet.getClass().getSimpleName() + ") was larger than expected, found " + packetBuffer.readableBytes() + " bytes extra whilst reading packet " + packet);
        }

        list.add(packet);
    }
}
