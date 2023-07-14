package com.forgeessentials.serverNetwork.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.FEPacketManager;
import com.forgeessentials.util.output.logger.LoggingHandler;

import java.util.Objects;

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
    private Map<Channel, Boolean> connectedChannels;
    private ArrayList<Channel> blockedChannels;

    public FENetworkServer(String remoteServerHost, int remoteServerPort, String channelname, int channelversion) {
        instance = this;
        this.remoteServerHost = remoteServerHost;
        this.remoteServerPort = remoteServerPort;
        this.channelNameM = channelname;
        this.channelVersionM = channelversion;
    }

    public final int startServer() {
        cleanConnection();
        LoggingHandler.felog.info("Starting FENetworkServer at " + remoteServerHost + ":" + remoteServerPort);

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
                LoggingHandler.felog.info("Server started successfully");
            else
                return 1;
        } catch(Exception e) {
            e.printStackTrace();
            LoggingHandler.felog.error("Failed to start FENetworkServer on " + remoteServerHost + ":" + remoteServerPort);
            return 1;
        }
        return 0;
    }

    public final int stopServer() {
        try {
            for (Channel channel : getConnectedChannels().keySet()) {
                if(channel != null && channel.isOpen()) {
                    channel.flush();
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
    public Map<Channel, Boolean> getConnectedChannels()
    {
        return connectedChannels;
    }
    public ArrayList<Channel> getBlockedChannels()
    {
        return blockedChannels;
    }

    private void cleanConnection()
    {
        nioEventLoopGroup = new NioEventLoopGroup(1);
        connectedChannels = new HashMap<>();
        blockedChannels = new ArrayList<>();
        bootstrap = null;
        packetManager = new FEPacketManager(new ServerPacketHandler());
    }

    public void sendPacket(FEPacket packet) {
        Objects.requireNonNull(packet, "Packet cannot be null");

        LoggingHandler.felog.debug("[OUT] " + packet.getClass().getSimpleName() + " " + packet.getID());

        for (Entry<Channel, Boolean> channel : getConnectedChannels().entrySet()) {
            if(!channel.getKey().isOpen())
                continue;
            if(channel.getValue()) {
                channel.getKey().writeAndFlush(packet).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if(!channelFuture.isSuccess()) {
                            channelFuture.cause().printStackTrace();
                        }
                    }
                });
            }
        }
    }

    public void sendPacketFor(Channel channel, FEPacket packet) {
        Objects.requireNonNull(packet, "Packet cannot be null");
        Objects.requireNonNull(channel, "Channel cannot be null");

        LoggingHandler.felog.debug("[OUT] " + packet.getID() + " " + packet.getClass().getSimpleName());

        if(!channel.isOpen())
            return;

        if(getConnectedChannels().get(channel)) {
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

    public String getChannelNameM()
    {
        return channelNameM;
    }

    public int getChannelVersionM()
    {
        return channelVersionM;
    }

}
