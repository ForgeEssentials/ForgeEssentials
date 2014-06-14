package com.forgeessentials.client.network;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.gui.GuiEconomy;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class C4PacketEconomy extends ForgeEssentialsPacketClient {

    public static final byte packetID = 4;

    private Packet250CustomPayload packet;

    public C4PacketEconomy()
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
            ForgeEssentialsClient.feclientlog.info("Error creating packet EconRequest");
        }
    }

    @Override
    public Packet250CustomPayload getPayload()
    {
        // TODO Auto-generated method stub
        return packet;
    }

    public static void readClient(DataInputStream stream, WorldClient world,
            EntityPlayer player)
    {
        try
        {
            GuiEconomy.amount = stream.readInt();
        }
        catch (IOException e)
        {
            ForgeEssentialsClient.feclientlog.severe("Failed to read packet EconRequest");
        }
    }

}
