package com.forgeessentials.permissions.autoPromote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.util.PlayerInfo;

public class AutoPromote {
    
	private int zone;

	private boolean enabled;

	private HashMap<String, String> promoteList;

	private boolean sendMsg;

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
