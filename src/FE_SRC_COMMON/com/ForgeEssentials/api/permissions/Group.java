package com.ForgeEssentials.api.permissions;

import com.ForgeEssentials.api.APIRegistry;

/**
 * This class is not a format that is designed to actually be saved in any way.
 * It is simply an output format for the data that is saved in the DataBase. The
 * fields here are not fnal in order to save memory and CPU when editing and
 * sending the instance back in to update.
 * @author AbrarSyed
 */

@SuppressWarnings("rawtypes")
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
			return Integer.MIN_VALUE;

		Group g = (Group) obj;

		if (equals(g))
			return 0;

		Zone my = APIRegistry.zones.getZone(zoneName);
		Zone their = APIRegistry.zones.getZone(g.zoneName);

		int end = my.compareTo(their);

		if (end == 0)
			end = g.priority - priority;
		
		if (end == 0)
			end = name.compareTo(g.name);

		return end;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Group))
			return false;
		Group g = (Group) obj;
		
		boolean p = parent == null;
		if (p)
			p = g.parent == null;
		else
			p = parent.equals(parent);
		
		boolean pre = prefix != null;
		if (pre)
			pre = g.prefix == null;
		else
			pre = prefix.equals(prefix);
		
		boolean suff = suffix != null;
		if (suff)
			suff = g.suffix == null;
		else
			suff = suffix.equals(suffix);
		
		return name.equalsIgnoreCase(g.name) &&
				p &&
				pre &&
				suff &&
				zoneName.equalsIgnoreCase(zoneName) &&
				priority == g.priority;
	}

	@Override
	public String toString()
	{
		return name + "[" + parent + ", " + prefix + ", " + suffix + ", " + zoneName + ", " + priority + "]";
	}

}
