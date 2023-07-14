package com.forgeessentials.serverNetwork.client;

import java.util.concurrent.TimeUnit;

import com.forgeessentials.serverNetwork.packetbase.handlers.ClientPacketDecoder;
import com.forgeessentials.serverNetwork.packetbase.handlers.PacketEncoder;
import com.forgeessentials.serverNetwork.packetbase.handlers.PacketPrepender;
import com.forgeessentials.serverNetwork.packetbase.handlers.PacketSplitter;
import com.forgeessentials.util.output.logger.LoggingHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class ServerChannelHandler extends ChannelInitializer<NioSocketChannel> {

    private final FENetworkClient feClient;

    public ServerChannelHandler(FENetworkClient nettyClient) {
        this.feClient = nettyClient;
    }

    @Override
    protected void initChannel(NioSocketChannel channel) throws Exception {
        feClient.setNioSocketChannel(channel);

        channel.pipeline()
                .addLast("timeout", new ReadTimeoutHandler(365L, TimeUnit.DAYS))
                .addLast("prepender", new PacketPrepender())
                .addLast("decoder", new ClientPacketDecoder())
                .addLast("splitter", new PacketSplitter())
                .addLast("encoder", new PacketEncoder())
                .addLast("handler", new ChannelReader());
    }

    public FENetworkClient getFENetworkClientt() {
        return feClient;
    }
    
    public class ChannelReader extends SimpleChannelInboundHandler<Object> {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            super.channelRegistered(ctx);
            Channel channel = ctx.channel();
            LoggingHandler.felog.info("FENetworkClient New channel registered: " + channel);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            super.channelUnregistered(ctx);
            Channel channel = ctx.channel();
            LoggingHandler.felog.info("FENetworkClient Channel unregistered: " + channel);
            feClient.disconnect();
        }
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            feClient.disconnect();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channel, Object object) throws Exception {
            feClient.getPacketManager().getPacketHandler().handle(object);
        }
    }
}
