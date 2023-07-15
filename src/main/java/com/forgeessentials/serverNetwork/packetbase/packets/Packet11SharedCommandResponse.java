package com.forgeessentials.serverNetwork.packetbase.packets;

import com.forgeessentials.serverNetwork.packetbase.FEPacket;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;

import net.minecraft.network.PacketBuffer;

public class Packet11SharedCommandResponse extends FEPacket
{
    String commandResponse;

    public Packet11SharedCommandResponse() {}
    
    public Packet11SharedCommandResponse(String commandResponse){
        this.commandResponse = commandResponse;
        
    }
    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeUtf(commandResponse);
    }

    @Override
    public void decode(PacketBuffer buf)
    {
        commandResponse = buf.readUtf();
    }

    @Override
    public void handle(PacketHandler packetHandler)
    {
        packetHandler.handle(this);
    }

    @Override
    public int getID()
    {
        return 11;
    }

    public String getCommandResponse()
    {
        return commandResponse;
    }
    
}
