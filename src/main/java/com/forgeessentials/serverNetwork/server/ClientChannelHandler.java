package com.forgeessentials.serverNetwork.server;

import java.util.concurrent.TimeUnit;

import com.forgeessentials.serverNetwork.packets.handlers.PacketEncoder;
import com.forgeessentials.serverNetwork.packets.handlers.PacketPrepender;
import com.forgeessentials.serverNetwork.packets.handlers.PacketSplitter;

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
        this.feServer.getConnectedChannels().add(nioSocketChannel);

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
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            feServer.getConnectedChannels().remove(ctx.channel());
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channel, Object object) throws Exception {
            feServer.getPacketManager().getPacketHandler().handle(channel.channel(), object);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            if(!cause.getMessage().equals("Connection reset"))
                super.exceptionCaught(ctx, cause);
        }
    }
}
