package com.ForgeEssentials.permission;

import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.permission.query.PropQueryPlayerZone;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.events.PlayerChangedZone;

public class EventHandler
{
	@ForgeSubscribe(priority = EventPriority.LOW)
	public void onZoneChange(PlayerChangedZone event)
	{
		PropQueryPlayerZone query1 = new PropQueryPlayerZone(event.entityPlayer, "ForgeEssentials.Permissions.Zone.exit", event.beforeZone, false);
		PropQueryPlayerZone query2 = new PropQueryPlayerZone(event.entityPlayer, "ForgeEssentials.Permissions.Zone.entry", event.afterZone, false);

		APIRegistry.perms.getPermissionProp(query1);
		if (query1.hasValue())
		{
			event.entityPlayer.sendChatToPlayer(FunctionHelper.formatColors(query1.getStringValue()));
		}

		APIRegistry.perms.getPermissionProp(query2);
		if (query2.hasValue())
		{
			event.entityPlayer.sendChatToPlayer(FunctionHelper.formatColors(query2.getStringValue()));
		}

	}
}
