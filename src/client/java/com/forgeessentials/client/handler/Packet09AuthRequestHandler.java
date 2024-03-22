package com.forgeessentials.client.handler;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.network.packets.Packet09AuthRequest;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class Packet09AuthRequestHandler extends Packet09AuthRequest
{
    public Packet09AuthRequestHandler(String hash)
    {
        super(hash);
    }

    public static Packet09AuthRequestHandler decode(FriendlyByteBuf buf)
    {
        return new Packet09AuthRequestHandler(buf.readUtf());
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        Minecraft mc = Minecraft.getInstance();
        ForgeEssentialsClient.authDatabase.setKey(mc.getCurrentServer().ip, hash);
    }
}