package com.forgeessentials.api.permissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import net.minecraftforge.permissions.api.IGroup;

/**
 * This class is not a format that is designed to actually be saved in any way. It is simply an output format for the data that is saved in the DataBase. The
 * fields here are not fnal in order to save memory and CPU when editing and sending the instance back in to update.
 *
 * @author AbrarSyed
 */

@SuppressWarnings("rawtypes")
public class Group implements Comparable, IGroup {

	public String name;
	public String parent;
	public String prefix;
	public String suffix;
	public int priority; // lowest priority is 0

	private int id;

	public Group(String name, String prefix, String suffix, String parent, int priority)
	{
		super();
		this.parent = parent;
		this.prefix = prefix;
		this.suffix = suffix;
		this.name = name;
		this.priority = priority;
	}

	public Group(String name, String prefix, String suffix, String parent, int priority, int id)
	{
		this(name, prefix, suffix, parent, priority);
		this.id = id;
	}
	

	@Override
	public int compareTo(Object obj)
	{
		if (!(obj instanceof Group))
		{
			return Integer.MIN_VALUE;
		}
		int cmp = ((Group) obj).priority - priority;
		if (cmp == 0)
			cmp = name.compareTo(((Group) obj).name);
		return cmp;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Group))
		{
			return false;
		}
		Group g = (Group) obj;

		boolean p = parent == null;
		if (p)
		{
			p = g.parent == null;
		}
		else
		{
			p = parent.equals(parent);
		}

		boolean pre = prefix != null;
		if (pre)
		{
			pre = g.prefix == null;
		}
		else
		{
			pre = prefix.equals(prefix);
		}

		boolean suff = suffix != null;
		if (suff)
		{
			suff = g.suffix == null;
		}
		else
		{
			suff = suffix.equals(suffix);
		}

		return name.equalsIgnoreCase(g.name) && p && pre && suff && priority == g.priority;
	}

	@Override
	public String toString()
	{
		return name + "[" + parent + ", " + prefix + ", " + suffix + ", " + priority + "]";
	}

	@Override
	public Collection<UUID> getAllPlayers()
	{
		ArrayList<UUID> returned = new ArrayList<UUID>();
		throw new RuntimeException("Not yet implemented!");
//		for (String pname : APIRegistry.perms.getPlayersByGroup(name))
//		{
//			returned.add(UUID.fromString(pname));
//		}
	}

	@Override
	public boolean isMember(UUID playerID)
	{
		throw new RuntimeException("Not yet implemented!");
		//return APIRegistry.perms.getPlayersByGroup(name).contains(playerID.toString());
	}

	@Override
	public String getName()
	{
		return name;
	}

	public int getId()
	{
		return id;
	}

	public String getParent()
	{
		return parent;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public String getSuffix()
	{
		return suffix;
	}

	public int getPriority()
	{
		return priority;
	}

}
