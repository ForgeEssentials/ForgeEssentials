package com.ForgeEssentials.permission.autoPromote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.data.api.IReconstructData;
import com.ForgeEssentials.data.api.SaveableObject;
import com.ForgeEssentials.data.api.SaveableObject.Reconstructor;
import com.ForgeEssentials.data.api.SaveableObject.SaveableField;
import com.ForgeEssentials.data.api.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.util.ChatUtils;
import com.ForgeEssentials.util.FunctionHelper;

@SaveableObject
public class AutoPromote
{
	@UniqueLoadingKey
	@SaveableField
	public String					zone;

	@SaveableField
	public boolean					enable;

	@SaveableField
	public HashMap<String, String>	promoteList;

	@SaveableField
	public boolean					sendMsg;

	@SaveableField
	public String					msg;

	public AutoPromote(String zone, boolean enable)
	{
		this.zone = zone;
		this.enable = enable;
		promoteList = new HashMap<String, String>();
		/*
		 * Available options:
		 * '%group' = new group.
		 * '%time' = nicely formatted time.
		 * All color codes with '&'.
		 */
		msg = "&5You have been promoted to %group for playing for %time.";
		sendMsg = true;
	}

	@SuppressWarnings("unchecked")
	@Reconstructor
	private static AutoPromote reconstruct(IReconstructData tag)
	{
		AutoPromote data = new AutoPromote((String) tag.getFieldValue("zone"), (Boolean) tag.getFieldValue("enable"));
		try
		{
			data.promoteList = (HashMap<String, String>) tag.getFieldValue("promoteList");
		}
		catch (Exception e)
		{
		}
		if (data.promoteList == null)
		{
			data.promoteList = new HashMap<String, String>();
		}
		return data;
	}

	public void tick(EntityPlayerMP player)
	{
		try
		{
			if (promoteList.containsKey(PlayerInfo.getPlayerInfo(player.username).getTimePlayed() + ""))
			{
				String groupName = promoteList.get(PlayerInfo.getPlayerInfo(player.username).getTimePlayed() + "");
				// Only add player to group if he isn't already.
				if (!APIRegistry.perms.getApplicableGroups(player.username, false, zone).contains(APIRegistry.perms.getGroupForName(groupName)))
				{
					APIRegistry.perms.addPlayerToGroup(groupName, player.username, zone);
					if (sendMsg)
					{
						String msg = this.msg;
						msg = FunctionHelper.formatColors(msg);
						msg = msg.replaceAll("%group", groupName);
						msg = msg.replaceAll("%time", FunctionHelper.parseTime(PlayerInfo.getPlayerInfo(player.username).getTimePlayed() * 60));
						ChatUtils.sendMessage(player, msg);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public ArrayList<String> getList()
	{
		ArrayList<String> result = new ArrayList<String>();
		for (String i : promoteList.keySet())
		{
			result.add(i);
		}
		Collections.sort(result);
		return result;
	}
}
