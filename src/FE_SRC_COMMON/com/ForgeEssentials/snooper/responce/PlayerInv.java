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
import net.minecraft.network.rcon.RConOutputStream;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.economy.Wallet;
import com.ForgeEssentials.snooper.ConfigSnooper;
import com.ForgeEssentials.snooper.ModuleSnooper;
import com.ForgeEssentials.snooper.TextFormatter;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class PlayerInv extends Response
{
	public PlayerInv(DatagramPacket packet)
	{
		super(packet);
		this.allowed = ConfigSnooper.send_Player_armor;
		if(!this.allowed) return;
		
		String username = new String(Arrays.copyOfRange(packet.getData(), 11, packet.getLength()));
    	EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(username.trim());
    	if(player == null) return;
    	
    	HashMap<String, String> PlayerData = new HashMap();
    	ArrayList<String> tempArgs = new ArrayList();
    	for(ItemStack stack : player.inventory.mainInventory)
    	{
    		if(stack != null)
        	{
    			tempArgs.add(TextFormatter.toJSON(stack, true));
        	}
    	}
    	PlayerData.put("inv", TextFormatter.toJSON(tempArgs));
    	PlayerData.put("ench", "true");
    	dataString = TextFormatter.toJSON(PlayerData);
    	
    	if (dataString.length() > 2043)
    	{
    		PlayerData.clear();
    		tempArgs.clear();
    		for(ItemStack stack : player.inventory.mainInventory)
        	{
        		if(stack != null)
            	{
        			tempArgs.add(TextFormatter.toJSON(stack, false));
            	}
        	}
    		PlayerData.put("inv", TextFormatter.toJSON(tempArgs));
        	PlayerData.put("ench", "false");
        	dataString = TextFormatter.toJSON(PlayerData);
    	}
		
		System.out.println("Length: " + dataString.length());
	}
}
