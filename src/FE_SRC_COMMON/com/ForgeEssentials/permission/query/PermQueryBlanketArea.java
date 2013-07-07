package com.ForgeEssentials.permission.query;

import java.util.ArrayList;

import com.ForgeEssentials.api.AreaSelector.AreaBase;
import com.ForgeEssentials.api.AreaSelector.WorldArea;

public class PermQueryBlanketArea extends PermQuery
{
	public ArrayList<AreaBase>	applicable;
	public final WorldArea		doneTo;
	public final boolean		allOrNothing;

	public PermQueryBlanketArea(String permission, WorldArea doneTo, boolean allOrNothing)
	{
		applicable = new ArrayList<AreaBase>();
		this.doneTo = doneTo;
		this.allOrNothing = allOrNothing;
		checkForward = false;
	}

	public PermQueryBlanketArea(String permission, WorldArea doneTo, boolean allOrNothing, boolean checkForward)
	{
		this(permission, doneTo, allOrNothing);
		this.checkForward = checkForward;
	}

	/**
	 * set DEFAULT if the applicable regions list is to be used. set DENY if the
	 * permissions is completely denied throughout the requested area. set ALLOW
	 * if the permission is completely allowed throughout the requested area.
	 * @param value
	 * The new result
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
