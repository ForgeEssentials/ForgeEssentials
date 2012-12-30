package com.ForgeEssentials.snooper.responce;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.LinkedHashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.snooper.ConfigSnooper;
import com.ForgeEssentials.snooper.TextFormatter;

public class PlayerArmor extends Response
{
	public PlayerArmor(DatagramPacket packet)
	{
		super(packet);
		this.allowed = ConfigSnooper.send_Player_armor;
		if(!this.allowed) return;
		
		LinkedHashMap<String, String> PlayerData = new LinkedHashMap();
		String username = new String(Arrays.copyOfRange(packet.getData(), 11, packet.getLength()));
    	EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(username.trim());
    	if(player == null) return;
    	
    	for(int i = 0; i < 3; i++)
    	{
    		ItemStack stack = player.inventory.armorInventory[i];
        	if(stack != null)
        	{
        		PlayerData.put("" + i, TextFormatter.toJSON(stack, true));
        	}
    	}
		
		dataString = TextFormatter.toJSON(PlayerData);
	}
}
