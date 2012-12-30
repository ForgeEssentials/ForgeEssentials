package com.ForgeEssentials.snooper.responce;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.economy.Wallet;
import com.ForgeEssentials.snooper.ConfigSnooper;
import com.ForgeEssentials.snooper.ModuleSnooper;
import com.ForgeEssentials.snooper.TextFormatter;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class PlayerArmor extends Response
{
	public PlayerArmor(DatagramPacket packet)
	{
		super(packet);
		this.allowed = ConfigSnooper.send_Player_armor;
		if(!this.allowed) return;
		
		HashMap<String, String> PlayerData = new HashMap();
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
