package com.forgeessentials.client.handler;

import java.util.HashSet;
import java.util.Set;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.network.packets.Packet3PlayerPermissions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet3PlayerPermissionsHandler extends Packet3PlayerPermissions
{

    public Packet3PlayerPermissionsHandler(boolean reset, Set<Integer> placeIds, Set<Integer> breakeIds)
    {
        super(reset, placeIds, breakeIds);
    }

    public static Packet3PlayerPermissionsHandler decode(PacketBuffer buf)
    {
        boolean reset1 = buf.readBoolean();
        Set<Integer> placeIds1 = new HashSet<Integer>();
        Set<Integer> breakIds1 = new HashSet<Integer>();
        int count = buf.readShort();
        for (int i = 0; i < count; i++)
            placeIds1.add(buf.readInt());

        count = buf.readShort();
        for (int i = 0; i < count; i++)
            breakIds1.add(buf.readInt());
        return new Packet3PlayerPermissionsHandler(reset1, placeIds1, breakIds1);
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        if (reset)
        {
            ForgeEssentialsClient.permissionOverlay.permissions.breakIds.clear();
            ForgeEssentialsClient.permissionOverlay.permissions.placeIds.clear();
            ForgeEssentialsClient.permissionOverlay.permissions.reset = false;
        }
        else
        {
            ForgeEssentialsClient.permissionOverlay.permissions.placeIds.addAll(placeIds);
            ForgeEssentialsClient.permissionOverlay.permissions.breakIds.addAll(breakIds);

            Minecraft instance = Minecraft.getInstance();
            PlayerEntity player = instance.player;
            ItemStack stack = player.getMainHandItem();
            if (stack != ItemStack.EMPTY)
            {
                int itemId = Item.getId(stack.getItem());
                for (int id : ForgeEssentialsClient.permissionOverlay.permissions.placeIds)
                    if (itemId == id)
                    {
                        player.stopUsingItem();
                        ;
                        break;
                    }
            }
        }
    }
}
