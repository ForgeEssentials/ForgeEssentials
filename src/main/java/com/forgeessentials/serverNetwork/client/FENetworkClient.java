package com.forgeessentials.serverNetwork.client;

import com.forgeessentials.serverNetwork.packets.FEPacket;
import com.forgeessentials.serverNetwork.packets.FEPacketManager;
import com.forgeessentials.serverNetwork.packets.client.ClientPasswordPacket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

@ChannelHandler.Sharable
public class FENetworkClient {

    private static FENetworkClient instance;

    private final String remoteServerHost;
    private final int remoteServerPort;
    private final String channelNameM;
    private final int channelVersionM;

    private Bootstrap bootstrap;
    private NioSocketChannel nioSocketChannel;
    private ChannelFuture channelFuture;
    private NioEventLoopGroup nioEventLoopGroup;

    private FEPacketManager packetManager;

    public FENetworkClient(String remoteServerHost, int remoteServerPort, String channelname, int channelversion) {
        instance = this;
        this.remoteServerHost = remoteServerHost;
        this.remoteServerPort = remoteServerPort;
        this.channelNameM = channelname;
        this.channelVersionM = channelversion;
    }

    public int connect() {
        reset();
        System.out.println("Connecting to " + remoteServerHost + ":" + remoteServerPort);

        bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ServerChannelHandler(this));

        try {
            bootstrap.option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .option(ChannelOption.SO_KEEPALIVE, true);
        } catch(ChannelException ignored) {}

        try {
            channelFuture = bootstrap.connect(remoteServerHost, remoteServerPort).syncUninterruptibly();

            if(channelFuture.isSuccess()) {
                System.out.println("Connected successfully");
                sendPacket(new ClientPasswordPacket("HI!"));
            } else
                return 1;
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Could not connect to " + remoteServerHost + ":" + remoteServerPort);

            return 1;
        }
        return 0;
    }

    public void disconnect() {
        if(nioSocketChannel != null && nioSocketChannel.isOpen())
            nioSocketChannel.close();

        if(channelFuture != null)
            channelFuture.channel().close();

        if(nioEventLoopGroup != null)
            nioEventLoopGroup.shutdownGracefully();

        reset();
    }

    public void sendPacket(FEPacket packet) {
        if (packet == null)
            throw new NullPointerException("Packet cannot be null");

        System.out.println("[OUT] " + packet.getClass().getSimpleName() + " " + packet.getID());

        nioSocketChannel.writeAndFlush(packet).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    channelFuture.cause().printStackTrace();
                }
            }
        });
    }

    public void reset() {
        bootstrap = new Bootstrap();
        nioSocketChannel = new NioSocketChannel();
        channelFuture = null;
        nioEventLoopGroup = new NioEventLoopGroup(1);
        packetManager = new FEPacketManager(new ClientPacketHandler());
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public NioSocketChannel getNioSocketChannel() {
        return nioSocketChannel;
    }

    public void setNioSocketChannel(NioSocketChannel nioSocketChannel) {
        this.nioSocketChannel = nioSocketChannel;
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public static FENetworkClient getInstance() {
        return instance;
    }

    public NioEventLoopGroup getNioEventLoopGroup() {
        return nioEventLoopGroup;
    }

    public FEPacketManager getPacketManager() {
        return packetManager;
    }
}
