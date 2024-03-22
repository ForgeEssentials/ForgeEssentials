package com.forgeessentials.serverNetwork.packetbase.packets;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.FriendlyByteBuf;

public class Packet12ServerPlayerSync extends FEPacket
{
    public Set<String> uuids;

    public Packet12ServerPlayerSync() {}
    
    public Packet12ServerPlayerSync(Set<UUID> ids){
        uuids = new HashSet<>();
        for(UUID id : ids) {
            if(id==null)
                continue;
            uuids.add(id.toString());
        }
    }
    @Override
    public void encode(FriendlyByteBuf buf)
    {
        if (uuids != null)
        {
            buf.writeShort(uuids.size());
            for (String id : uuids)
                buf.writeUtf(id);
        }
        else
            buf.writeShort(0);
    }

    @Override
    public void decode(FriendlyByteBuf buf)
    {
        uuids = new HashSet<>();
        int count = buf.readShort();
        for (int i = 0; i < count; i++)
            uuids.add(buf.readUtf());
    }

    @Override
    public void handle(PacketHandler packetHandler)
    {
        packetHandler.handle(this);
    }

    @Override
    public int getID()
    {
        return 12;
    }

    public Set<UUID> getUuids()
    {
        Set<UUID> uuid = new HashSet<>();
        for(String st : uuids) {
            uuid.add(UUID.fromString(st));
        }
        return uuid;
    }
    
}
