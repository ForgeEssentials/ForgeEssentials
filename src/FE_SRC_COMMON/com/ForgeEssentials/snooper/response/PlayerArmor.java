package com.ForgeEssentials.snooper.response;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.LinkedHashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.api.snooper.TextFormatter;

public class PlayerArmor extends Response
{
	@Override
	public String getResponceString(DatagramPacket packet)
	{
		LinkedHashMap<String, String> PlayerData = new LinkedHashMap();
		String username = new String(Arrays.copyOfRange(packet.getData(), 11,
				packet.getLength()));
		EntityPlayerMP player = server.getConfigurationManager()
				.getPlayerForUsername(username.trim());
		if (player == null)
		{
			return "";
		}

		for (int i = 0; i < 3; i++)
		{
			ItemStack stack = player.inventory.armorInventory[i];
			if (stack != null)
			{
				PlayerData.put("" + i, TextFormatter.toJSON(stack, true));
			}
		}

		return dataString = TextFormatter.toJSON(PlayerData);
	}

	@Override
	public String getName()
	{
		return "PlayerArmor";
	}

	@Override
	public void readConfig(String category, Configuration config)
	{
		// Don't need that here
	}

	@Override
	public void writeConfig(String category, Configuration config)
	{
		// Don't need that here
	}
}
