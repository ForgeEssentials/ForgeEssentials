package com.ForgeEssentials.WorldControl.TickTasks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.ForgeEssentials.WorldControl.ConfigWorldControl;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BlockArrayBackup;
import com.ForgeEssentials.util.BlockInfo;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

public class TickTaskWalls extends TickTaskLoadBlocks
{
	private ArrayList<AreaBase>	applicable;

	BlockInfo to;

	public TickTaskWalls(EntityPlayer player, AreaBase sel, BlockInfo to)
	{
		super(player, sel);
		this.to=to;
	}

	public TickTaskWalls(EntityPlayer player, AreaBase sel, BlockInfo to, ArrayList<AreaBase> applicable)
	{
		this(player, sel, to);
		this.applicable = applicable;
	}
	
	protected boolean placeBlock() {
		if(x==sel.getLowPoint().x||x==sel.getHighPoint().x||z==sel.getLowPoint().z||z==sel.getHighPoint().z) {
			if(isApplicable(x, y, z))return place(x, y, z, to);
		}
		return false;
	}

	private boolean isApplicable(int x, int y, int z)
	{
		Point p = new Point(x, y, z);
		if (applicable == null)
		{
			return true;
		}

		boolean contains = false;

		for (AreaBase area : applicable)
		{
			contains = area.contains(p);
			if (contains)
			{
				return true;
			}
		}

		return contains;
	}

}
