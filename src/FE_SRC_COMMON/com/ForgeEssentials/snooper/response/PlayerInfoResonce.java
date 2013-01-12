package com.ForgeEssentials.snooper.response;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.api.snooper.TextFormatter;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.economy.Wallet;
import com.ForgeEssentials.permission.Group;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class PlayerInfoResonce extends Response
{
	private boolean sendhome;
	private boolean sendpotions;
	private boolean sendXP;
	private boolean sendArmorAndHealth;
	private boolean sendFood;
	private boolean sendCapabilities;

	@Override
	public String getResponceString(DatagramPacket packet)
	{
		LinkedHashMap<String, String> PlayerData = new LinkedHashMap();
		LinkedHashMap<String, String> tempMap = new LinkedHashMap();
		ArrayList<String> tempArgs = new ArrayList();

		String username = new String(Arrays.copyOfRange(packet.getData(), 11, packet.getLength()));
		EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(username.trim());
		if (player == null)
		{
			return "";
		}

		PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
		if (pi != null && sendhome)
		{
			if (pi.home != null)
			{
				PlayerData.put("home", TextFormatter.toJSON(pi.home));
			}
			if (pi.back != null)
			{
				PlayerData.put("back", TextFormatter.toJSON(pi.back));
			}
		}

		if (sendArmorAndHealth)
		{
			PlayerData.put("armor", "" + player.inventory.getTotalArmorValue());
			PlayerData.put("health", "" + player.getHealth());
		}

		PlayerData.put("wallet", "" + Wallet.getWallet(player));
		PlayerData.put("pos", TextFormatter.toJSON(new WorldPoint(player)));
		PlayerData.put("ping", "" + player.ping);
		PlayerData.put("gm", player.theItemInWorldManager.getGameType().getName());

		if (!player.getActivePotionEffects().isEmpty() && sendpotions)
		{
			PlayerData.put("potion", TextFormatter.toJSON(player.getActivePotionEffects()));
		}

		if (sendXP)
		{
			tempMap.clear();
			tempMap.put("lvl", "" + player.experienceLevel);
			tempMap.put("bar", "" + player.experience);
			PlayerData.put("xp", TextFormatter.toJSON(tempMap));
		}

		if (sendFood)
		{
			tempMap.clear();
			tempMap.put("food", "" + player.getFoodStats().getFoodLevel());
			tempMap.put("saturation", "" + player.getFoodStats().getSaturationLevel());
			PlayerData.put("foodStats", TextFormatter.toJSON(tempMap));
		}

		if (sendCapabilities)
		{
			tempMap.clear();
			tempMap.put("edit", "" + player.capabilities.allowEdit);
			tempMap.put("allowFly", "" + player.capabilities.allowFlying);
			tempMap.put("isFly", "" + player.capabilities.isFlying);
			tempMap.put("noDamage", "" + player.capabilities.disableDamage);
		}
		PlayerData.put("cap", TextFormatter.toJSON(tempMap));

		try
		{
			Group group = PermissionsAPI.getHighestGroup(player);
			PlayerData.put("group", group.name);
		}
		catch (Exception e)
		{
		}

		return dataString = TextFormatter.toJSON(PlayerData);
	}

	@Override
	public String getName()
	{
		return "PlayerInfoResonce";
	}

	@Override
	public void readConfig(String category, Configuration config)
	{
		sendhome = config.get(category, "sendHome", true).getBoolean(true);
		sendpotions = config.get(category, "sendpotions", true).getBoolean(true);
		sendXP = config.get(category, "sendXP", true).getBoolean(true);
		sendArmorAndHealth = config.get(category, "sendArmorAndHealth", true).getBoolean(true);
		sendFood = config.get(category, "sendFood", true).getBoolean(true);
		sendCapabilities = config.get(category, "sendCapabilities", true).getBoolean(true);
	}

	@Override
	public void writeConfig(String category, Configuration config)
	{
		config.get(category, "sendHome", true).value = "" + sendhome;
		config.get(category, "sendpotions", true).value = "" + sendpotions;
		config.get(category, "sendXP", true).value = "" + sendXP;
		config.get(category, "sendArmorAndHealth", true).value = "" + sendArmorAndHealth;
		config.get(category, "sendFood", true).value = "" + sendFood;
		config.get(category, "sendCapabilities", true).value = "" + sendCapabilities;
	}
}
