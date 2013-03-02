package com.ForgeEssentials.api.permissions.query;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Event.HasResult;

import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WorldArea;

/**
 * Reuslts are: default, allow, deny.
 * @author AbrarSyed
 */
@HasResult
public class PermQueryPlayerArea extends PermQueryPlayer
{
	public ArrayList<AreaBase>	applicable;
	public final WorldArea		doneTo;
	public final boolean		allOrNothing;

	public PermQueryPlayerArea(EntityPlayer player, String permission, AreaBase doneTo, boolean allOrNothing)
	{
		super(player, permission);
		applicable = new ArrayList<AreaBase>();
		this.doneTo = new WorldArea(player.worldObj, doneTo);
		this.allOrNothing = allOrNothing;
		checkForward = false;
	}

	public PermQueryPlayerArea(EntityPlayer player, String permission, Point doneTo)
	{
		super(player, permission);
		applicable = new ArrayList<AreaBase>();
		this.doneTo = new WorldArea(player.worldObj, doneTo, doneTo);
		allOrNothing = true;
		checkForward = false;
	}

	public PermQueryPlayerArea(EntityPlayer player, String permission, AreaBase doneTo, boolean allOrNothing, boolean checkForward)
	{
		super(player, permission);
		applicable = new ArrayList<AreaBase>();
		this.doneTo = new WorldArea(player.worldObj, doneTo);
		this.allOrNothing = allOrNothing;
		this.checkForward = checkForward;
	}

	public PermQueryPlayerArea(EntityPlayer player, String permission, Point doneTo, boolean checkForward)
	{
		super(player, permission);
		applicable = new ArrayList<AreaBase>();
		this.doneTo = new WorldArea(player.worldObj, doneTo, doneTo);
		allOrNothing = true;
		this.checkForward = checkForward;
	}

	/**
	 * set DEFAULT if the applicable regions list is to be used. set DENY if the
	 * permissions is completely denied throughout the requested area. set ALLOW
	 * if the permission is completely allowed throughout the requested area.
	 * @param value
	 *            The new result
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
