package com.ForgeEssentials.snooper.response;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.json.JSONException;
import com.ForgeEssentials.api.json.JSONObject;
import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.api.snooper.TextFormatter;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.economy.WalletHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class PlayerInfoResonce extends Response
{
	private boolean	sendhome;
	private boolean	sendpotions;
	private boolean	sendXP;
	private boolean	sendArmorAndHealth;
	private boolean	sendFood;
	private boolean	sendCapabilities;

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getResponce(String input) throws JSONException
	{
		JSONObject PlayerData = new JSONObject();
		JSONObject tempMap = new JSONObject();

		EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(input);
		if (player == null)
			return new JSONObject().put(this.getName(), "");

		PlayerInfo pi = PlayerInfo.getPlayerInfo(player.username);
		if (pi != null && sendhome)
		{
			if (pi.home != null)
			{
				PlayerData.put("home", pi.home.toJSON());
			}
			if (pi.back != null)
			{
				PlayerData.put("back", pi.back.toJSON());
			}
		}

		if (sendArmorAndHealth)
		{
			PlayerData.put("armor", "" + player.inventory.getTotalArmorValue());
			PlayerData.put("health", "" + player.getHealth());
		}

		PlayerData.put("WalletHandler", "" + WalletHandler.getWallet(player));
		PlayerData.put("pos", new WorldPoint(player).toJSON());
		PlayerData.put("ping", "" + player.ping);
		PlayerData.put("gm", player.theItemInWorldManager.getGameType().getName());

		if (!player.getActivePotionEffects().isEmpty() && sendpotions)
		{
			PlayerData.put("potion", TextFormatter.toJSON((Collection<PotionEffect>) player.getActivePotionEffects()));
		}

		if (sendXP)
		{
			tempMap = new JSONObject();
			tempMap.put("lvl", "" + player.experienceLevel);
			tempMap.put("bar", "" + player.experience);
			PlayerData.put("xp", tempMap);
		}

		if (sendFood)
		{
			tempMap = new JSONObject();
			tempMap.put("food", "" + player.getFoodStats().getFoodLevel());
			tempMap.put("saturation", "" + player.getFoodStats().getSaturationLevel());
			PlayerData.put("foodStats", tempMap);
		}

		if (sendCapabilities)
		{
			tempMap = new JSONObject();
			tempMap.put("edit", "" + player.capabilities.allowEdit);
			tempMap.put("allowFly", "" + player.capabilities.allowFlying);
			tempMap.put("isFly", "" + player.capabilities.isFlying);
			tempMap.put("noDamage", "" + player.capabilities.disableDamage);
		}
		PlayerData.put("cap", tempMap);

		try
		{
			Group group = PermissionsAPI.getHighestGroup(player);
			PlayerData.put("group", group.name);
		}
		catch (Exception e)
		{
		}

		return new JSONObject().put(this.getName(), PlayerData);
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
