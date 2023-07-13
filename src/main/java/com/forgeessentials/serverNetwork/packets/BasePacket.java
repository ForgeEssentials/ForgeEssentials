package com.forgeessentials.serverNetwork.packets;

import io.netty.channel.Channel;

public class BasePacket
{
    private Channel channel;

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }
}
