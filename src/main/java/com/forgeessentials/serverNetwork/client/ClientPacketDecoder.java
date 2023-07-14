package com.forgeessentials.serverNetwork.client;

import java.io.IOException;
import java.util.List;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.minecraft.network.PacketBuffer;

public class ClientPacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);

        if (packetBuffer.readableBytes() < 1) {
            return;
        }

        int packetID = packetBuffer.readVarInt();
        FEPacket packet = FENetworkClient.getInstance().getPacketManager().getPacket(packetID);

        System.out.println("[IN] " + packetID + " " + packet.getClass().getSimpleName());
        packet.decode(packetBuffer);

        if (packetBuffer.readableBytes() > 0) {
            throw new IOException("Packet  (" + packet.getClass().getSimpleName() + ") was larger than expected, found " + packetBuffer.readableBytes() + " bytes extra whilst reading packet " + packet);
        }

        list.add(packet);
    }
}
