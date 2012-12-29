package com.ForgeEssentials.permission.query;

import java.util.ArrayList;

import net.minecraft.world.World;

import com.ForgeEssentials.util.AreaSelector.AreaBase;

public class PermQueryBlanketArea extends PermQuery
{
	public ArrayList<AreaBase>	applicable;
	public final AreaBase		doneTo;
	public final boolean		allOrNothing;

	public PermQueryBlanketArea(String permission, AreaBase doneTo, World world, boolean allOrNothing)
	{
		applicable = new ArrayList<AreaBase>();
		this.doneTo = doneTo;
		this.allOrNothing = allOrNothing;
	}

	/**
	 * set DEFAULT if the applicable regions list is to be used.
	 * set DENY if the permissions is completely denied throughout the requested area.
	 * set ALLOW if the permission is completely allowed throughout the requested area.
	 * 
	 * @param value The new result
	 */
	@Override
	public void setResult(PermResult value)
	{
		if (value.equals(PermResult.ALLOW))
		{
			applicable.clear();
			applicable.add(doneTo);
		}
		else if (value.equals(PermResult.DENY))
		{
			applicable.clear();
		}
		super.setResult(value);
	}
}
