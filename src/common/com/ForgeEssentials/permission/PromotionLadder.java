package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.Arrays;

public class PromotionLadder
{
	public final String				name;
	public final String				zoneID;
	private final ArrayList<String>	groupsList;

	public PromotionLadder(String name, String zoneID, String[] list)
	{
		this.name = name;
		this.zoneID = zoneID;
		groupsList = new ArrayList<String>();
		groupsList.addAll(Arrays.asList(list));
	}

	public boolean containsGroup(String group)
	{
		return groupsList.contains(group);
	}

	/**
	 * @param group
	 * @return NULL if this group is the top, or isn't in this ladder.
	 */
	public String getPromotion(String group)
	{
		if (!groupsList.contains(group))
			return null;

		int index = groupsList.indexOf(group);
		if (index == 0)
			return null;
		else
			return groupsList.get(index - 1);
	}

	/**
	 * @param group
	 * @return NULL if this group is the bottom, or isn't in this ladder.
	 */
	public String getDemotion(String group)
	{
		if (!groupsList.contains(group))
			return null;

		int index = groupsList.indexOf(group);
		if (index == groupsList.size() - 1)
			return null;
		else
			return groupsList.get(index - 1);
	}

	/**
	 * @return an array of groupnames in the order of the ladder.
	 */
	public String[] getListGroup()
	{
		return groupsList.toArray(new String[] {});
	}
}
