package com.forgeessentials.playerlogger.network;

import com.forgeessentials.core.network.ForgeEssentialsPacket;
import com.forgeessentials.playerlogger.blockChange;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

public class PacketRollback extends ForgeEssentialsPacket {
    public static final byte packetID = 2;

    private Packet250CustomPayload packet = new Packet250CustomPayload();

    public PacketRollback(int dim, ArrayList<blockChange> changes)
    {
        ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(streambyte);

        try
        {
            stream.write(packetID);
            if (changes == null)
            {
                stream.writeByte(0);
            }
            else
            {
                stream.writeByte(1);
                stream.writeInt(changes.size());
                System.out.println("Sending " + changes.size());
                for (blockChange bc : changes)
                {
                    if (bc.dim == dim)
                    {
                        System.out.println(bc.toString());
                        stream.writeInt(bc.X);
                        stream.writeInt(bc.Y);
                        stream.writeInt(bc.Z);
                        // True if the change was a placement.
                        stream.writeInt(bc.type);
                    }
                }
            }

            packet.channel = FECHANNEL;
            packet.data = streambyte.toByteArray();
            packet.length = packet.data.length;

            stream.close();
            streambyte.close();
        }
        catch (Exception e)
        {
            OutputHandler.felog.info("Error creating packet >> " + this.getClass());
            e.printStackTrace();
        }
    }

    @Override
    public Packet250CustomPayload getPayload()
    {
        return packet;
    }
}
