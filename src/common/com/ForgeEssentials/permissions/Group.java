package com.ForgeEssentials.permissions;

public class Group
{
	private String promote;
	private String demote;
	private String parent;
	public String prefix;
	public String suffix;
	public final String name;
	
	public Group(String name)
	{
		this.name = name;
	}
	
	public Group(String name, String above)
	{
		this(name);
		this.setLadderAbove(GroupManager.groups.get(above));
	}
	
	/**
	 * Sets what group is the promotion of this one.
	 * @param above Group to promote to when /promote command is used
	 */
	public void setLadderAbove(Group  above)
	{
		if (above == null)
		{
			promote = "";
			return;
		}
		
		promote = above.name;
		above.demote = name;
	}
	
	/**
	 * Sets what group is the parent of this one
	 * @param parent The group from which this group inherits functionality
	 */
	public void setParent(String parent)
	{
		this.parent = parent;
	}
	
	/**
	 * Sets what group is the parent of this one
	 * @param parent The group from which this group inherits functionality
	 */
	public String getParent()
	{
		return parent;
	}
	
	public boolean hasParent()
	{
		return parent == null || parent.isEmpty(); 
	}
	
}
