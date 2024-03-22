package com.forgeessentials.serverNetwork.packetbase.handlers;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.FriendlyByteBuf;

public class PacketEncoder extends MessageToByteEncoder<FEPacket> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, FEPacket packet, ByteBuf byteBuf) throws Exception {
        FriendlyByteBuf packetBuffer = new FriendlyByteBuf(byteBuf);
        int id = packet.getID();

        packetBuffer.writeVarInt(id);
        packet.encode(packetBuffer);
    }
}
