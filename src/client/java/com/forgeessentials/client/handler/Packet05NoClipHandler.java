package com.forgeessentials.client.handler;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.network.packets.Packet05Noclip;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class Packet05NoClipHandler extends Packet05Noclip
{
    public Packet05NoClipHandler(boolean noclip)
    {
        super(noclip);
    }

    public static Packet05NoClipHandler decode(FriendlyByteBuf buf)
    {
        return new Packet05NoClipHandler(buf.readBoolean());
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        Minecraft instance = Minecraft.getInstance();
        instance.player.noPhysics = noclip;
        ForgeEssentialsClient.noClip = noclip;
    }
}
