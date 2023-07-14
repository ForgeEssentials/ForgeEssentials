package com.forgeessentials.serverNetwork.client;

import java.net.ConnectException;

import com.forgeessentials.serverNetwork.ModuleNetworking;
import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.FEPacketManager;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet0ClientValidation;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet2ClientNewConnectionData;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet3ClientConnectionData;
import com.forgeessentials.util.output.logger.LoggingHandler;

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
        LoggingHandler.felog.info("FENetworkClient Connecting to FENetworkServer " + remoteServerHost + ":" + remoteServerPort);

        packetManager = new FEPacketManager(new ClientPacketHandler());
        nioSocketChannel = new NioSocketChannel();
        nioEventLoopGroup = new NioEventLoopGroup(1);
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
                LoggingHandler.felog.info("FENetworkClient Connection successful");
                sendPacket(new Packet0ClientValidation(channelNameM, channelVersionM));
            } else
                return 1;
        } catch(Exception e) {
            if(e instanceof ConnectException) {
                LoggingHandler.felog.error("FENetworkClient Failed to connect to FENetworkServer " + remoteServerHost + ":" + remoteServerPort);
                LoggingHandler.felog.error("FENetworkClient coundn't find FENetworkServer");
                disconnect();
                return 1;
            }
            e.printStackTrace();
            LoggingHandler.felog.error("FENetworkClient Failed to connect to FENetworkServer " + remoteServerHost + ":" + remoteServerPort);
            disconnect();
            return 1;
        }
        return 0;
    }

    public int disconnect() {
        LoggingHandler.felog.debug("FENetworkClient disconnecting");
        try {
            if(nioSocketChannel != null && nioSocketChannel.isOpen())
                nioSocketChannel.close();

            if(channelFuture != null)
                channelFuture.channel().close();

            if(nioEventLoopGroup != null)
                nioEventLoopGroup.shutdownGracefully();

            reset();
        }catch(Exception e) {
            LoggingHandler.felog.error("FENetworkClient failed during disconnecting");
            return 1;
        }
        LoggingHandler.felog.debug("FENetworkClient disconnected successfully");
        Thread.currentThread().getStackTrace();
        return 0;
    }

    public boolean isChannelOpen() {
        return channelFuture != null && channelFuture.channel().isOpen();
    }

    public void sendPacket(FEPacket packet) {
        if (packet == null)
            throw new NullPointerException("Packet cannot be null");

        if(!(packet instanceof Packet0ClientValidation
                || packet instanceof Packet2ClientNewConnectionData
                || packet instanceof Packet3ClientConnectionData)) {
            if(!ModuleNetworking.getLocalClient().isAuthenticated()) {
                LoggingHandler.felog.info("FENetworkClient can't send packet "+packet.getClass().getSimpleName()
                        +" bacause it is not authenticated");
                return;
            }
        }
        LoggingHandler.felog.debug("FENetworkClient [OUT] " + packet.getID() + " " + packet.getClass().getSimpleName());

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
        bootstrap = null;
        nioSocketChannel = null;
        nioEventLoopGroup = null;
        packetManager = null;
        channelFuture = null;
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
