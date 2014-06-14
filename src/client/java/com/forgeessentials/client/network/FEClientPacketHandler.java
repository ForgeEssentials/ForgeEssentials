package com.forgeessentials.client.network;

import com.forgeessentials.client.ForgeEssentialsClient;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

@SideOnly(Side.CLIENT)
public class FEClientPacketHandler implements IPacketHandler {
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player playerFake)
    {
        try
        {
            ByteArrayInputStream streambyte = new ByteArrayInputStream(packet.data);
            DataInputStream stream = new DataInputStream(streambyte);

            EntityPlayer player = (EntityPlayer) playerFake;
            WorldClient world = (WorldClient) player.worldObj;

            int ID = stream.read();

            ForgeEssentialsClient.feclientlog.finest("Received packet with ID " + ID);

            switch (ID)
            {
            // cast to the correct instance of ForgeEssentialsPacketbase and use
            // the read methods.
            case 0:
                C0PacketSelectionUpdate.readClient(stream, world, player);
                break;
            case 1:
                C1PacketPlayerLogger.readClient(stream, world, player);
                break;
            case 2:
                C2PacketRollback.readClient(stream, world, player);
                break;
            case 3:
                C3PacketPermNodeList.readClient(stream, world, player);
                break;
            case 4:
                C4PacketEconomy.readClient(stream, world, player);
                break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
