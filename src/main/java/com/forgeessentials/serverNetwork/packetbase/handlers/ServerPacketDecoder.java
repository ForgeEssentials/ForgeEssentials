package com.forgeessentials.serverNetwork.packetbase.handlers;

import java.io.IOException;
import java.util.List;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet00ClientValidation;
import com.forgeessentials.serverNetwork.server.FENetworkServer;
import com.forgeessentials.util.output.logger.LoggingHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.IllegalReferenceCountException;
import net.minecraft.network.FriendlyByteBuf;

public class ServerPacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        FriendlyByteBuf packetBuffer = new FriendlyByteBuf(byteBuf);

        if (packetBuffer.readableBytes() < 1) {
            return;
        }

        int packetID;
        boolean flag;
        try {
            flag = FENetworkServer.getInstance().getConnectedChannels().get(channelHandlerContext.channel());
        }catch(NullPointerException e) {
            channelHandlerContext.channel().close();
            LoggingHandler.felog.error("FENetworkServer Closing null type channel");
            return;
        }
        if(!flag) {
            try {
                packetID = packetBuffer.readVarInt();
            }catch(IllegalReferenceCountException e) {
                channelHandlerContext.channel().close();
                LoggingHandler.felog.error("FENetworkServer Closing invalid type packet channel");
                return;
            }
        }
        else {
            packetID = packetBuffer.readVarInt();
        }
        if(packetID!=(new Packet00ClientValidation()).getID()&&!flag) {
            LoggingHandler.felog.error("FENetworkServer Recieved a packet before recieving validation packet from client");
            channelHandlerContext.pipeline().remove(ServerPacketDecoder.class);
            channelHandlerContext.pipeline().remove(PacketSplitter.class);
            FENetworkServer.getInstance().getBlockedChannels().add(channelHandlerContext.channel());
            channelHandlerContext.flush();
            channelHandlerContext.channel().close();
            return;
        }
        FEPacket packet = FENetworkServer.getInstance().getPacketManager().getPacket(packetID);

        LoggingHandler.felog.debug("FENetworkServer [IN] " + packetID + " " + packet.getClass().getSimpleName());
        packet.decode(packetBuffer);

        if (packetBuffer.readableBytes() > 0) {
            throw new IOException("Packet  (" + packet.getClass().getSimpleName() + ") was larger than expected, found " + packetBuffer.readableBytes() + " bytes extra whilst reading packet " + packet);
        }

        list.add(packet);
    }
}
