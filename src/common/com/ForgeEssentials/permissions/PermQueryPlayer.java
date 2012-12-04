package com.ForgeEssentials.permissions;

import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.Event.*;

/**
 * Reuslts are: default, allow, deny.
 * @author AbrarSyed
 *
 */
@HasResult
public class PermQueryPlayer extends PermQueryBase
{
	/**
	 * Assumes the Players position as the "doneTo" point.
	 * @param player
	 * @param permission
	 */
	public PermQueryPlayer(EntityPlayer player, String permission)
	{
		super(player, permission);
	}
	
	public Point getDoerPoint()
	{
		return new Point((int)Math.round(doer.posX), (int)Math.round(doer.posY), (int)Math.round(doer.posZ));
	}
}

