package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.Arrays;

public class PromotionLadder
{
	public final String				name;
	public final String				zoneID;
	private final ArrayList<String>	ladder;

	public PromotionLadder(String name, String zoneID, String[] list)
	{
		this.name = name;
		this.zoneID = zoneID;
		ladder = new ArrayList<String>();
		ladder.addAll(Arrays.asList(list));
	}

	public boolean containsGroup(String group)
	{
		return ladder.contains(group);
	}

	/**
	 * @param group
	 * @return NULL if this group is the top, or isn't in this ladder.
	 */
	public String getPromotion(String group)
	{
		if (!ladder.contains(group))
			return null;

		int index = ladder.indexOf(group);
		if (index == 0)
			return null;
		else
			return ladder.get(index - 1);
	}

	/**
	 * @param group
	 * @return NULL if this group is the bottom, or isn't in this ladder.
	 */
	public String getDemotion(String group)
	{
		if (!ladder.contains(group))
			return null;

		int index = ladder.indexOf(group);
		if (index == ladder.size() - 1)
			return null;
		else
			return ladder.get(index - 1);
	}

	/**
	 * @return an array of groupnames in the order of the ladder.
	 */
	public String[] getListGroup()
	{
		return ladder.toArray(new String[] {});
	}
}
