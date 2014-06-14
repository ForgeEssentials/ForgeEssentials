package com.forgeessentials.client.network;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.gui.GuiPermNodeList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class C3PacketPermNodeList extends ForgeEssentialsPacketClient {

    public static final byte packetID = 3;

    private Packet250CustomPayload packet;

    public C3PacketPermNodeList()
    {

        packet = new Packet250CustomPayload();

        ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(streambyte);

        try
        {
            stream.write(packetID);
            {
                stream.writeBytes("REQUEST");

                stream.close();
                streambyte.close();

                packet.channel = FECHANNEL;
                packet.data = streambyte.toByteArray();
                packet.length = packet.data.length;
            }
        }

        catch (Exception e)
        {
            ForgeEssentialsClient.feclientlog.info("Error creating packet PermNodeList");
        }
    }

    @Override
    public Packet250CustomPayload getPayload()
    {

        return packet;
    }

    public static void readClient(DataInputStream stream, WorldClient world,
            EntityPlayer player)
    {
        try
        {
            GuiPermNodeList.nodes = stream.readUTF().split(":");
        }
        catch (IOException e)
        {
            ForgeEssentialsClient.feclientlog.severe("Failed to read packet PermNodeList");
        }

    }

}
