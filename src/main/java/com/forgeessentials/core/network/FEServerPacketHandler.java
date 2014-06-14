package com.forgeessentials.core.network;

import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.WorldServer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FEServerPacketHandler implements IPacketHandler {

    private static Map<Integer, Class<? extends ForgeEssentialsPacket>> packetList = new HashMap<Integer, Class<? extends ForgeEssentialsPacket>>();

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player playerFake)
    {
        try
        {
            ByteArrayInputStream streambyte = new ByteArrayInputStream(packet.data);
            DataInputStream stream = new DataInputStream(streambyte);

            EntityPlayer player = (EntityPlayer) playerFake;
            WorldServer world = (WorldServer) player.worldObj;

            int ID = stream.read();
            OutputHandler.felog.fine("Received packet with ID " + ID);
            Class<? extends ForgeEssentialsPacket> clazz = packetList.get(ID);
            Method method = clazz.getDeclaredMethod("readServer", DataInputStream.class, WorldServer.class, EntityPlayer.class);
            method.invoke(null, stream, world, player);
            OutputHandler.felog.fine("Successfully handled packet ID " + ID + " using class " + clazz.getCanonicalName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void registerPacket(int ID, Class<? extends ForgeEssentialsPacket> clazz)
    {
        packetList.put(ID, clazz);
        OutputHandler.felog.fine("Registered packet " + clazz.getCanonicalName() + " with ID " + ID);
    }

    public static void init()
    {
        registerPacket(0, PacketSelectionUpdate.class);
    }

}
