package com.forgeessentials.serverNetwork.server;

import java.util.ArrayList;
import java.util.Objects;

import com.forgeessentials.serverNetwork.packets.FEPacket;
import com.forgeessentials.serverNetwork.packets.FEPacketManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

@ChannelHandler.Sharable
public class FENetworkServer
{
    private static FENetworkServer instance;
    
    private final String remoteServerHost;
    private final int remoteServerPort;
    private final String channelNameM;
    private final int channelVersionM;
    
    private NioEventLoopGroup nioEventLoopGroup;
    private ServerBootstrap bootstrap;
    private ChannelFuture channelFuture;
    
    private FEPacketManager packetManager;
    private ArrayList<Channel> connectedChannels;

    public FENetworkServer(String remoteServerHost, int remoteServerPort, String channelname, int channelversion) {
        instance = this;
        this.remoteServerHost = remoteServerHost;
        this.remoteServerPort = remoteServerPort;
        this.channelNameM = channelname;
        this.channelVersionM = channelversion;
    }

    public final int startServer() {
        cleanConnection();
        System.out.println("Starting server on " + remoteServerHost + ":" + remoteServerPort);

        bootstrap = new ServerBootstrap();
        bootstrap.group(nioEventLoopGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ClientChannelHandler(this));
        try {
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.AUTO_CLOSE, true)
                    .option(ChannelOption.SO_REUSEADDR, true);
        } catch(ChannelException ignored) {}
        try {
            channelFuture = bootstrap.localAddress(remoteServerHost, remoteServerPort).bind().syncUninterruptibly();

            if(channelFuture.isSuccess())
                System.out.println("Server started successfully");
            else
                return 1;
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Could not start server on " + remoteServerHost + ":" + remoteServerPort);
            return 1;
        }
        return 0;
    }

    public final int stopServer() {
        try {
            for (Channel channel : getConnectedChannels()) {
                if(channel != null && channel.isOpen()) {
                    channel.close();
                }
            }

            if(nioEventLoopGroup != null)
                nioEventLoopGroup.shutdownGracefully();

            if(channelFuture != null)
                channelFuture.channel().close();

            cleanConnection();
        }catch(Exception e) {
            return 1;
        }
        return 0;
    }
    public ArrayList<Channel> getConnectedChannels()
    {
        return connectedChannels;
    }

    private void cleanConnection()
    {
        nioEventLoopGroup = new NioEventLoopGroup(1);
        connectedChannels = new ArrayList<>();
        bootstrap = null;
        packetManager = new FEPacketManager(new ServerPacketHandler());
    }

    public void sendPacket(FEPacket packet) {
        Objects.requireNonNull(packet, "Packet cannot be null");

        System.out.println("[OUT] " + packet.getClass().getSimpleName() + " " + packet.getID());

        for (Channel channel : getConnectedChannels()) {
            if(!channel.isOpen())
                continue;

            channel.writeAndFlush(packet).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(!channelFuture.isSuccess()) {
                        channelFuture.cause().printStackTrace();
                    }
                }
            });
        }
    }

    public void sendPacketFor(Channel channel, FEPacket packet) {
        Objects.requireNonNull(packet, "Packet cannot be null");
        Objects.requireNonNull(channel, "Channel cannot be null");

        System.out.println("[OUT] " + packet.getClass().getSimpleName() + " " + packet.getID());

        if(!channel.isOpen())
            return;

        channel.writeAndFlush(packet).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(!channelFuture.isSuccess()) {
                    channelFuture.cause().printStackTrace();
                }
            }
        });
    }

    public static FENetworkServer getInstance()
    {
        return instance;
    }

    public ServerBootstrap getBootstrap()
    {
        return bootstrap;
    }

    public ChannelFuture getChannelFuture()
    {
        return channelFuture;
    }

    public FEPacketManager getPacketManager()
    {
        return packetManager;
    }

}
