package com.forgeessentials.serverNetwork.packetbase.packets;

import java.util.UUID;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.FriendlyByteBuf;

public class Packet14ClientPlayerSync extends FEPacket
{
    String playerUuid;
    boolean loggedIn;

    public Packet14ClientPlayerSync() {}
    
    public Packet14ClientPlayerSync(UUID playerUuid, boolean loggedIn){
        this.playerUuid = playerUuid.toString();
        this.loggedIn = loggedIn;
        
    }
    @Override
    public void encode(FriendlyByteBuf buf)
    {
        buf.writeUtf(playerUuid);
        buf.writeBoolean(loggedIn);
    }

    @Override
    public void decode(FriendlyByteBuf buf)
    {
        playerUuid = buf.readUtf();
        loggedIn = buf.readBoolean();
    }

    @Override
    public void handle(PacketHandler packetHandler)
    {
        packetHandler.handle(this);
    }

    @Override
    public int getID()
    {
        return 14;
    }

    public UUID getPlayerUuid()
    {
        return UUID.fromString(playerUuid);
    }

    public boolean loggedIn() {
        return loggedIn;
    }
}
