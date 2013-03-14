package com.ForgeEssentials.permission.autoPromote;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.permission.PromotionLadder;
import com.ForgeEssentials.permission.SqlHelper;
import com.ForgeEssentials.util.OutputHandler;

@SaveableObject
public class AutoPromote
{
	@UniqueLoadingKey
	@SaveableField
	public String					zone;

	@SaveableField
	public boolean					enable;

	@SaveableField
	public ArrayList<Integer>		promoteList	= new ArrayList<Integer>();

	public AutoPromote(String zone, boolean enable)
	{
		this.zone = zone;
		this.enable = enable;
	}

	@Reconstructor
	private static AutoPromote reconstruct(IReconstructData tag)
	{
		AutoPromote data = new AutoPromote((String) tag.getFieldValue("zone"), (Boolean) tag.getFieldValue("enable"));
		data.promoteList = (ArrayList<Integer>) tag.getFieldValue("promoteList");
		return data;
	}
	
	public void tick(EntityPlayerMP player)
	{
		OutputHandler.debug("Tick " + player.username + " in " + zone); //TODO debug!
		if(promoteList.contains(PlayerInfo.getPlayerInfo(player.username).timePlayed))
		{
			Group currentGroup = PermissionsAPI.getHighestGroup(player);
			PromotionLadder ladder = SqlHelper.getLadderForGroup(currentGroup.name, zone);
			if (ladder != null)
			{
				OutputHandler.debug("Ladderlist: " + ladder.getListGroup()); //TODO debug!
				String newGroup = ladder.getPromotion(currentGroup.name);
				if (newGroup != null)
				{
					PermissionsAPI.addPlayerToGroup(newGroup, player.username, zone);
					OutputHandler.debug(player.username + " autopromoted to " + newGroup + " from " + currentGroup.name); //TODO debug!
				}
			}
		}
	}
}

