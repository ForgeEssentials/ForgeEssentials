package com.ForgeEssentials.permission;

public class Group implements Comparable
{

	public final String name;
	public final String parent;
	public final String prefix;
	public final String suffix;
	public final String zoneID;
	public final int priority; // lowest priority is 0

	public Group(String name, String prefix, String suffix, String parent,
			String zoneID, int priority)
	{
		super();
		this.parent = parent;
		this.prefix = prefix;
		this.suffix = suffix;
		this.name = name;
		this.zoneID = zoneID;
		this.priority = priority;
	}

	@Override
	public int compareTo(Object obj)
	{
		if (!(obj instanceof Group))
		{
			return Integer.MIN_VALUE;
		}

		Group g = (Group) obj;

		if (equals(g))
		{
			return 0;
		}

		Zone my = ZoneManager.getZone(zoneID);
		Zone their = ZoneManager.getZone(g.zoneID);

		int end = my.compareTo(their);

		if (end == 0)
		{
			return priority - their.priority;
		}

		return end;
	}

}
