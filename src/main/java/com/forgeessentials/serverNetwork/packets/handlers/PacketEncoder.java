package com.forgeessentials.serverNetwork.packets.handlers;

import com.forgeessentials.serverNetwork.packets.FEPacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.PacketBuffer;

public class PacketEncoder extends MessageToByteEncoder<FEPacket> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, FEPacket packet, ByteBuf byteBuf) throws Exception {
        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
        int id = packet.getID();

        packetBuffer.writeVarInt(id);
        packet.encode(packetBuffer);
    }
}
