package com.forgeessentials.client.handler;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet06AuthLogin;
import com.forgeessentials.commons.network.packets.Packet08AuthReply;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class Packet06AuthLoginHandler extends Packet06AuthLogin
{
    public Packet06AuthLoginHandler()
    {
        super();
    }

    public static Packet06AuthLoginHandler decode(FriendlyByteBuf buf)
    {
        return new Packet06AuthLoginHandler();
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        // send empty response if the client has disabled this
        if (!ForgeEssentialsClient.allowAuthAutoLogin)
        {
            NetworkUtils.sendToServer(new Packet08AuthReply(""));
        }
        Minecraft mc = Minecraft.getInstance();
        NetworkUtils.sendToServer(new Packet08AuthReply(ForgeEssentialsClient.authDatabase.getKey(mc.getCurrentServer().ip)));
    }
}