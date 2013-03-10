package com.ForgeEssentials.snooper.response;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.json.JSONArray;
import com.ForgeEssentials.api.json.JSONException;
import com.ForgeEssentials.api.json.JSONObject;
import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.api.snooper.TextFormatter;

public class PlayerInv extends Response
{
	@Override
	public JSONObject getResponce(String input) throws JSONException
	{
		EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(input);
		if (player == null)
			return new JSONObject().put(this.getName(), "");

		JSONObject PlayerData = new JSONObject();
		JSONArray tempArgs = new JSONArray();
		for (ItemStack stack : player.inventory.mainInventory)
		{
			if (stack != null)
			{
				tempArgs.put(TextFormatter.toJSON(stack, true));
			}
		}
		PlayerData.put("inv", tempArgs);
		
		tempArgs = new JSONArray();
		for (int i = 0; i < 3; i++)
		{
			ItemStack stack = player.inventory.armorInventory[i];
			if (stack != null)
			{
				tempArgs.put(TextFormatter.toJSON(stack, true));
			}
		}
		PlayerData.put("armor", tempArgs);
		
		return new JSONObject().put(this.getName(), PlayerData);
	}

	@Override
	public String getName()
	{
		return "PlayerInv";
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
