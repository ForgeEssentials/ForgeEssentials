package com.forgeessentials.serverNetwork.packetbase.packets;

import java.util.UUID;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.FriendlyByteBuf;

public class Packet13SharedPlayerTransfer extends FEPacket
{
    String playerUuid;

    public Packet13SharedPlayerTransfer() {}
    
    public Packet13SharedPlayerTransfer(UUID playerUuid){
        this.playerUuid = playerUuid.toString();
        
    }
    @Override
    public void encode(FriendlyByteBuf buf)
    {
        buf.writeUtf(playerUuid);
    }

    @Override
    public void decode(FriendlyByteBuf buf)
    {
        playerUuid = buf.readUtf();
    }

    @Override
    public void handle(PacketHandler packetHandler)
    {
        packetHandler.handle(this);
    }

    @Override
    public int getID()
    {
        return 13;
    }

    public UUID getPlayerUuid()
    {
        return UUID.fromString(playerUuid);
    }
    
}
