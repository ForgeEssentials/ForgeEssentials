package com.forgeessentials.serverNetwork.packetbase.packets;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.PacketBuffer;

public class Packet12ServerPlayerSync extends FEPacket
{
    public Set<String> uuids;

    public Packet12ServerPlayerSync() {}
    
    public Packet12ServerPlayerSync(Set<UUID> ids){
        for(UUID id : ids) {
            uuids.add(id.toString());
        }
    }
    @Override
    public void encode(PacketBuffer buf)
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
    public void decode(PacketBuffer buf)
    {
        uuids = new HashSet<String>();
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
        Set<UUID> uuid = new HashSet<UUID>();
        for(String st : uuids) {
            uuid.add(UUID.fromString(st));
        }
        return uuid;
    }
    
}
