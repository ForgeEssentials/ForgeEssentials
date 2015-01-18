package com.forgeessentials.permissions.autoPromote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.commons.IReconstructData;
import com.forgeessentials.commons.SaveableObject;
import com.forgeessentials.commons.SaveableObject.Reconstructor;
import com.forgeessentials.commons.SaveableObject.SaveableField;
import com.forgeessentials.commons.SaveableObject.UniqueLoadingKey;
import com.forgeessentials.util.PlayerInfo;

@SaveableObject
public class AutoPromote {
    
	@UniqueLoadingKey
	@SaveableField
	private int zone;

	@SaveableField
	private boolean enabled;

	@SaveableField
	private HashMap<String, String> promoteList;

	@SaveableField
	private boolean sendMsg;

	@SaveableField
	private String msg;

	public AutoPromote(int zone, boolean enable)
	{
		this.zone = zone;
		this.enabled = enable;
		promoteList = new HashMap<String, String>();
		/*
		 * Available options: '%group' = new group. '%time' = nicely formatted time. All color codes with '&'.
		 */
		msg = "&5You have been promoted to %group for playing for %time.";
		sendMsg = true;
	}

	public int getZone()
	{
		return zone;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public HashMap<String, String> getPromoteList()
	{
		return promoteList;
	}

	public boolean isSendMsg()
	{
		return sendMsg;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	public void setSendMsg(boolean sendMsg)
	{
		this.sendMsg = sendMsg;
	}

	@SuppressWarnings("unchecked")
	@Reconstructor
	private static AutoPromote reconstruct(IReconstructData tag)
	{
		AutoPromote data = new AutoPromote((int) tag.getFieldValue("zone"), (Boolean) tag.getFieldValue("enable"));
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
			if (promoteList.containsKey(PlayerInfo.getPlayerInfo(player.getPersistentID()).getTimePlayed() + ""))
			{
				String groupName = promoteList.get(PlayerInfo.getPlayerInfo(player.getPersistentID()).getTimePlayed() + "");
				// Only add player to group if he isn't already.
				throw new RuntimeException("Not yet implemented!");
//				if (!APIRegistry.perms.getApplicableGroups(player.getPersistentID(), false, zone).contains(APIRegistry.perms.getGroup(groupName)))
//				{
//					APIRegistry.perms.addPlayerToGroup(groupName, player.getPersistentID(), zone);
//					if (sendMsg)
//					{
//						String msg = this.msg;
//						msg = FunctionHelper.formatColors(msg);
//						msg = msg.replaceAll("%group", groupName);
//						msg = msg.replaceAll("%time", FunctionHelper.parseTime(PlayerInfo.getPlayerInfo(player.getPersistentID()).getTimePlayed() * 60));
//						OutputHandler.sendMessage(player, msg);
//					}
//				}
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
