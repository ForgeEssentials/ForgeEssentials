package com.forgeessentials.client.handler;

import java.util.HashSet;
import java.util.Set;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.network.packets.Packet03PlayerPermissions;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;


public class Packet03PlayerPermissionsHandler extends Packet03PlayerPermissions
{

    public Packet03PlayerPermissionsHandler(boolean reset, Set<String> placeIds, Set<String> breakeIds)
    {
        super(reset, placeIds, breakeIds);
    }

    public static Packet03PlayerPermissionsHandler decode(FriendlyByteBuf buf)
    {
        boolean reset1 = buf.readBoolean();
        Set<String> placeIds1 = new HashSet<>();
        Set<String> breakIds1 = new HashSet<>();
        int count = buf.readShort();
        for (int i = 0; i < count; i++)
            placeIds1.add(buf.readUtf());

        count = buf.readShort();
        for (int i = 0; i < count; i++)
            breakIds1.add(buf.readUtf());
        return new Packet03PlayerPermissionsHandler(reset1, placeIds1, breakIds1);
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        if (reset)
        {
            ForgeEssentialsClient.permissionOverlay.breakIds.clear();
            ForgeEssentialsClient.permissionOverlay.placeIds.clear();
        }
        else
        {
            ForgeEssentialsClient.permissionOverlay.placeIds.addAll(placeIds);
            ForgeEssentialsClient.permissionOverlay.breakIds.addAll(breakIds);
        }
    }
}
