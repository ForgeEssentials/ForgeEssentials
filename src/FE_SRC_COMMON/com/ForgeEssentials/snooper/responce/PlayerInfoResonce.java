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

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.economy.Wallet;
import com.ForgeEssentials.permission.Group;
import com.ForgeEssentials.permission.GroupManager;
import com.ForgeEssentials.snooper.ConfigSnooper;
import com.ForgeEssentials.snooper.ModuleSnooper;
import com.ForgeEssentials.snooper.TextFormatter;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class PlayerInfoResonce extends Response
{
	public PlayerInfoResonce(DatagramPacket packet)
	{
		super(packet);
		this.allowed = ConfigSnooper.send_Player_info;
		if(!this.allowed) return;
		
		HashMap<String, String> PlayerData = new HashMap();
    	HashMap<String, String> tempMap = new HashMap();
    	ArrayList<String> tempArgs = new ArrayList();
    	
    	String username = new String(Arrays.copyOfRange(packet.getData(), 11, packet.getLength()));
    	EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(username.trim());
    	if(player == null) return;
    	
    	PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
		if(pi != null)
		{
			if(pi.home != null) PlayerData.put("home", TextFormatter.toJSON(pi.home));
			if(pi.lastDeath != null) PlayerData.put("lastDeath", TextFormatter.toJSON(pi.lastDeath));
		}
		
		PlayerData.put("armor", "" + player.inventory.getTotalArmorValue());
		PlayerData.put("wallet", "" + Wallet.getWallet(player));
		PlayerData.put("health", "" + player.getHealth());
		PlayerData.put("pos", TextFormatter.toJSON(new WorldPoint(player)));
		PlayerData.put("ping", "" + player.ping);
		PlayerData.put("gm", player.theItemInWorldManager.getGameType().getName());
		
		if(!player.getActivePotionEffects().isEmpty())
		{
			PlayerData.put("potion", TextFormatter.toJSON(player.getActivePotionEffects()));
		}
		
		{
			tempMap.clear();
			tempMap.put("lvl", "" + player.experienceLevel);
			tempMap.put("bar", "" + player.experience);
		}
		PlayerData.put("xp", TextFormatter.toJSON(tempMap));
		
		{
			tempMap.clear();
			tempMap.put("food", "" + player.getFoodStats().getFoodLevel());
			tempMap.put("saturation", "" + player.getFoodStats().getSaturationLevel());
		}
		PlayerData.put("foodStats", TextFormatter.toJSON(tempMap));
		
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
			ArrayList<Group> groups = GroupManager.getApplicableGroups(player);
			Group group = groups.get(groups.size() - 1);
			PlayerData.put("group", group.name);
		}catch(Exception e){}
		
		dataString = TextFormatter.toJSON(PlayerData);
	}
}
