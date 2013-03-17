package com.ForgeEssentials.api.permissions;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.query.PermQuery;
import com.ForgeEssentials.api.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.api.permissions.query.PropQuery;

@SuppressWarnings(value = { "rawtypes" })
public interface IPermissionsHelper
{
	boolean checkPermAllowed(PermQuery query);

	PermResult checkPermResult(PermQuery query);

	void getPermissionProp(PropQuery query);

	Group createGroupInZone(String groupName, String zoneName, String prefix, String suffix, String parent, int priority);

	String setPlayerPermission(String username, String permission, boolean allow, String zoneID);

	String setGroupPermission(String group, String permission, boolean allow, String zoneID);

	String setPlayerPermissionProp(String username, String permission, String value, String zoneID);

	String setGroupPermissionProp(String group, String permission, String value, String zoneID);

	ArrayList<Group> getApplicableGroups(EntityPlayer player, boolean includeDefaults);

	ArrayList<Group> getApplicableGroups(String player, boolean includeDefaults, String zoneID);

	Group getGroupForName(String name);

	Group getHighestGroup(EntityPlayer player);

	String setPlayerGroup(String group, String player, String zone);

	String addPlayerToGroup(String group, String player, String zone);

	String clearPlayerGroup(String group, String player, String zone);

	String clearPlayerPermission(String player, String node, String zone);

	String clearPlayerPermissionProp(String player, String node, String zone);

	void deleteGroupInZone(String group, String zone);

	boolean updateGroup(Group group);

	String clearGroupPermission(String name, String node, String zone);

	String clearGroupPermissionProp(String name, String node, String zone);

	ArrayList<Group> getGroupsInZone(String zoneName);

	String getPermissionForGroup(String target, String zone, String perm);

	String getPermissionPropForGroup(String target, String zone, String perm);

	ArrayList getPlayerPermissions(String target, String zone);

	ArrayList getPlayerPermissionProps(String target, String zone);

	ArrayList getGroupPermissions(String target, String zone);

	ArrayList getGroupPermissionProps(String target, String zone);

	String getEPPrefix();

	void setEPPrifix(String ePPrefix);

	String getEPSuffix();

	void setEPSuffix(String ePSuffix);

	Group getDEFAULT();

	String getEntryPlayer();
}
