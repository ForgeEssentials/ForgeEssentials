package com.forgeessentialsclient.handler;


import com.forgeessentialsclient.utils.commons.network.packets.Packet3PlayerPermissions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PermissionHandler extends Packet3PlayerPermissions
{
	@Override
	public void handle(Context context) {
		Packet3PlayerPermissions permissions = new Packet3PlayerPermissions();
		if (permissions.reset)
        {
            permissions.breakIds.clear();
            permissions.placeIds.clear();
            permissions.reset = false;
        }
        else
        {
        	permissions.placeIds.addAll(permissions.placeIds);
        	permissions.breakIds.addAll(permissions.breakIds);
        	
        	Minecraft instance = Minecraft.getInstance();
            PlayerEntity player = instance.player;
            ItemStack stack = player.getMainHandItem();
            if (stack != ItemStack.EMPTY)
            {
                int itemId = Item.getId(stack.getItem());
                for (int id : permissions.placeIds)
                    if (itemId == id)
                    {
                        player.stopUsingItem();;
                        break;
                    }
            }
        }
		context.setPacketHandled(true);
	}
}
