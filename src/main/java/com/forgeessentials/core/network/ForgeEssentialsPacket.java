package com.forgeessentials.core.network;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class ForgeEssentialsPacket {
    public static final String FECHANNEL = "ForgeEssentials";

    public abstract Packet250CustomPayload getPayload();

    public static void readServer(DataInputStream stream, WorldClient world,
            EntityPlayer player) throws IOException
    {
    }
}
