package com.forgeessentials.serverNetwork.server;

import com.forgeessentials.serverNetwork.packetbase.PacketHandler;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet0ClientValidation;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet2ClientPassword;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet3ServerPasswordResponce;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet4SharedCloseSession;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class ServerPacketHandler implements PacketHandler
{

    @Override
    public void handle(Packet0ClientValidation responcePacket) {
     // Validate the connection
        if(responcePacket.getChannelName().equals(FENetworkServer.getInstance().getChannelNameM())) {
            if(responcePacket.getChannelVersion()==FENetworkServer.getInstance().getChannelVersionM()) {
                System.out.println("Valid connection detected, Continuing.");
                // Connection is valid, remove the decoder from the pipeline
                FENetworkServer.getInstance().getConnectedChannels().replace(responcePacket.getChannel(), true);
                return;
            }
            System.out.println("Client tried joining with mismatched channel version! Closing connection.");
        }
        System.out.println("Invalid connection detected! Closing connection.");
        String errorMessage = "Invalid protocol detected trying to access this ForgeEssentials Server Network!\n"
                + "This protocol can only be used for connecting between two servers running ForgeEssentials 16.0.0+";
        ByteBuf errorBuffer = Unpooled.copiedBuffer(errorMessage, CharsetUtil.UTF_8);
        responcePacket.getChannel().writeAndFlush(errorBuffer);
        FENetworkServer.getInstance().getBlockedChannels().add(responcePacket.getChannel());
        responcePacket.getChannel().flush();
        responcePacket.getChannel().close();
    }
 
    @Override
    public void handle(Packet2ClientPassword passwordPacket)
    {
        if(passwordPacket.getPassword().equals("password")) {
            FENetworkServer.getInstance().sendPacketFor(passwordPacket.getChannel(), new Packet3ServerPasswordResponce(true));
            return;
        }
        FENetworkServer.getInstance().sendPacketFor(passwordPacket.getChannel(), new Packet3ServerPasswordResponce(false));
    }

    @Override
    public void handle(Packet4SharedCloseSession closeSession)
    {
        System.out.println("Received close orders");
    }
}
