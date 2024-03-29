package com.forgeessentials.serverNetwork.server;

import java.util.concurrent.TimeUnit;

import com.forgeessentials.serverNetwork.packetbase.handlers.PacketEncoder;
import com.forgeessentials.serverNetwork.packetbase.handlers.PacketPrepender;
import com.forgeessentials.serverNetwork.packetbase.handlers.PacketSplitter;
import com.forgeessentials.serverNetwork.packetbase.handlers.ServerPacketDecoder;
import com.forgeessentials.util.output.logger.LoggingHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class ClientChannelHandler extends ChannelInitializer<NioSocketChannel> {

    private final FENetworkServer feServer;

    public ClientChannelHandler(FENetworkServer feServer) {
        this.feServer = feServer;
    }

    @Override
    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
        this.feServer.getConnectedChannels().put(nioSocketChannel, false);

        nioSocketChannel.pipeline()
                .addLast("timeout", new ReadTimeoutHandler(365L, TimeUnit.DAYS))
                .addLast("prepender", new PacketPrepender())
                .addLast("decoder", new ServerPacketDecoder())
                .addLast("splitter", new PacketSplitter())
                .addLast("encoder", new PacketEncoder())
                .addLast("handler", new ChannelReader());
    }

    public FENetworkServer getFENetworkServer() {
        return feServer;
    }
    public class ChannelReader extends SimpleChannelInboundHandler<Object> {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            super.channelRegistered(ctx);
            Channel channel = ctx.channel();
            LoggingHandler.felog.info("FENetworkServer New channel registered: " + channel);
        }
        
        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            super.channelUnregistered(ctx);
            Channel channel = ctx.channel();
            feServer.getConnectedChannels().remove(channel);
            feServer.getBlockedChannels().remove(channel);
            LoggingHandler.felog.info("FENetworkServer Channel unregistered: " + channel);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channel, Object object) throws Exception {
            if(!feServer.getBlockedChannels().contains(channel.channel())) {
                feServer.getPacketManager().getPacketHandler().handle(channel.channel(), object);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            if(!cause.getMessage().equals("Connection reset"))
                super.exceptionCaught(ctx, cause);
        }
    }
}
