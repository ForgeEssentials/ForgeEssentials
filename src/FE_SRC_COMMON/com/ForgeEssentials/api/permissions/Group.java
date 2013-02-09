package com.ForgeEssentials.api.permissions;

/**
 * This class is not a format that is designed to actually be saved in any way.
 * It is simply an output format for the data that is saved in the DataBase. The
 * fields here are not fnal in order to save memory and CPU when editing and
 * sending the instance back in to update.
 * 
 * @author AbrarSyed
 */
public class Group implements Comparable
{

	public String	name;
	public String	parent;
	public String	prefix;
	public String	suffix;
	public String	zoneName;
	public int		priority;	// lowest priority is 0

	public Group(String name, String prefix, String suffix, String parent, String zoneName, int priority)
	{
		super();
		this.parent = parent;
		this.prefix = prefix;
		this.suffix = suffix;
		this.name = name;
		this.zoneName = zoneName;
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

		Zone my = ZoneManager.getZone(zoneName);
		Zone their = ZoneManager.getZone(g.zoneName);

		int end = my.compareTo(their);

		if (end == 0)
		{
			return priority - their.priority;
		}

		return end;
	}

	@Override
	public String toString()
	{
		return name + "[" + parent + ", " + prefix + ", " + suffix + ", " + zoneName + ", " + priority + "]";
	}

}
