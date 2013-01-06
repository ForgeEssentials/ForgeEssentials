package com.ForgeEssentials.snooper.response;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.api.snooper.TextFormatter;

public class PlayerInv extends Response
{
	@Override
	public String getResponceString(DatagramPacket packet)
	{
		String username = new String(Arrays.copyOfRange(packet.getData(), 11, packet.getLength()));
    	EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(username.trim());
    	if(player == null) return "";
    	
    	LinkedHashMap<String, String> PlayerData = new LinkedHashMap();
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
    	return dataString;
	}

	@Override
	public String getName() 
	{
		return "PlayerInv";
	}

	@Override
	public void readConfig(String category, Configuration config)
	{
		//Don't need that here
	}
	
	@Override
	public void writeConfig(String category, Configuration config)
	{
		//Don't need that here
	}
}
