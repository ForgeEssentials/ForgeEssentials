package com.forgeessentials.serverNetwork.packetbase.packets;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.FriendlyByteBuf;

public class Packet00ClientValidation extends FEPacket
{
    private String channelName;
    private int versionNumber;

    public Packet00ClientValidation() {}

    public Packet00ClientValidation(String channelName, int versionNumber) {
        this.channelName = channelName;
        this.versionNumber = versionNumber;
    }

    @Override
    public void encode(FriendlyByteBuf buf)
    {
        buf.writeUtf(channelName);
        buf.writeVarInt(versionNumber);
        
    }

    @Override
    public void decode(FriendlyByteBuf buf)
    {
        channelName = buf.readUtf();
        versionNumber = buf.readVarInt();
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    @Override
    public int getID()
    {
        return 0;
    }

    public String getChannelName()
    {
        return channelName;
    }

    public int getChannelVersion()
    {
        return versionNumber;
    }

}
