package com.forgeessentials.client.network;

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.util.ClientPoint;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketRollback implements IForgeEssentialsPacket
{
    public static final byte        packetID    = 2;

    private Packet250CustomPayload  packet;
    
    @SideOnly(Side.CLIENT)
    public static void readClient(DataInputStream stream, WorldClient world, EntityPlayer player) throws IOException
    {
        byte id = stream.readByte();
        if (id == 0)
        {
            ForgeEssentialsClient.getInfo().rbList.clear();
            System.out.println("Clear list");
        }
        else if (id == 1)
        {
            ForgeEssentialsClient.getInfo().rbList.clear();
            System.out.println("Clear list");
            int amount = stream.readInt();
            for (int i = 0; i < amount; i++)
            {
                try
                {
                    ClientPoint p = new ClientPoint(stream.readInt(), stream.readInt(), stream.readInt());
                    System.out.println(p.x + "; " + p.y + "; " + p.z);
                    ForgeEssentialsClient.getInfo().rbList.put(p, stream.readInt());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public Packet250CustomPayload getPayload()
    {
        return packet;
    }
}
