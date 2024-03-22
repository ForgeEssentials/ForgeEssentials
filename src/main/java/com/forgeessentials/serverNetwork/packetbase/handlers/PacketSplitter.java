package com.forgeessentials.serverNetwork.packetbase.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.FriendlyByteBuf;

public class PacketSplitter extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf buffer, ByteBuf byteBuf) throws Exception {
        int var4 = buffer.readableBytes();
        int var5 = FriendlyByteBuf.getVarIntSize(var4);

        if (var5 > 3) {
            throw new IllegalArgumentException("unable to fit " + var4 + " into " + '\003');
        }

        FriendlyByteBuf packetBuffer = new FriendlyByteBuf(byteBuf);
        packetBuffer.ensureWritable(var5 + var4);
        packetBuffer.writeVarInt(var4);
        packetBuffer.writeBytes(buffer, buffer.readerIndex(), var4);
    }
}
