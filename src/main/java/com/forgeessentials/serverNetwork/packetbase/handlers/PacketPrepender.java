package com.forgeessentials.serverNetwork.packetbase.handlers;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.minecraft.network.FriendlyByteBuf;

public class PacketPrepender extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> objects) {
        buffer.markReaderIndex();
        byte[] a = new byte[3];

        for (int i = 0; i < a.length; i++) {
            if (!buffer.isReadable()) {
                buffer.resetReaderIndex();
                return;
            }

            a[i] = buffer.readByte();

            if (a[i] >= 0) {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(a));

                try {
                    int varInt = buf.readVarInt();
                    if (buffer.readableBytes() < varInt) {
                        buffer.resetReaderIndex();
                        return;
                    }

                    objects.add(buffer.readBytes(varInt));
                } finally {
                    buf.release();
                }
                return;
            }
        }
        throw new RuntimeException("length wider than 21-bit");
    }
}
