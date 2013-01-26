package com.ForgeEssentials.api.permissions;


public class Group implements Comparable
{

	public String name;
	public String parent;
	public String prefix;
	public String suffix;
	public String zoneName;
	public int priority; // lowest priority is 0

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
		return name+"["+parent+", "+prefix+", "+suffix+", "+zoneName+", "+priority+"]";
	}

}
