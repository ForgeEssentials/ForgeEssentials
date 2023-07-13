package com.forgeessentials.serverNetwork.packets.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.PacketBuffer;

public class PacketSplitter extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf buffer, ByteBuf byteBuf) throws Exception {
        int var4 = buffer.readableBytes();
        int var5 = PacketBuffer.getVarIntSize(var4);

        if (var5 > 3) {
            throw new IllegalArgumentException("unable to fit " + var4 + " into " + '\003');
        }

        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
        packetBuffer.ensureWritable(var5 + var4);
        packetBuffer.writeVarInt(var4);
        packetBuffer.writeBytes(buffer, buffer.readerIndex(), var4);
    }
}
