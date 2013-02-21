package com.ForgeEssentials.WorldControl.TickTasks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.ForgeEssentials.WorldControl.BlockArrayBackup;
import com.ForgeEssentials.WorldControl.BlockInfo;
import com.ForgeEssentials.WorldControl.ConfigWorldControl;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

public class TickTaskReplaceSelection extends TickTaskLoadBlocks
{
	private ArrayList<AreaBase>	applicable;

	BlockInfo from;
	BlockInfo to;

	private Point current;

	public TickTaskReplaceSelection(EntityPlayer player, AreaBase sel, BlockInfo from, BlockInfo to)
	{
		super(player, sel);
		this.from=from;
		this.to=to;
		current = sel.getLowPoint();
	}

	public TickTaskReplaceSelection(EntityPlayer player, AreaBase sel, BlockInfo from, BlockInfo to, ArrayList<AreaBase> applicable)
	{
		this(player, sel, from, to);
		this.applicable = applicable;
	}
	
	protected void runTick() {
		x = current.x;
		y = current.y;
		z = current.z;
	}
	
	private int x;
	private int y;
	private int z;
	
	protected boolean placeBlock() {
		int id = world.getBlockId(x, y, z);
		int m = world.getBlockMetadata(x, y, z);
		boolean ret = false;
		if (from.compare(new BlockInfo.SingularBlockInfo(Block.blocksList[id], m, null)) && isApplicable(x, y, z))
		{
			ret = place(x, y, z, to.randomBlock());
		}
		x++;
		if (x > sel.getHighPoint().x)
		{
			// Reset y, increment z.
			x = sel.getLowPoint().x;
			z++;
			if (z > sel.getHighPoint().z)
			{
				// Reset z, increment x.
				z = sel.getLowPoint().z;
				y++;
				// Check stop condition
				if (y > sel.getHighPoint().y)
				{
					return ret;
				}
			}
		}
		return ret;
	}
	
	protected void endTick() {
		current = new Point(x, y, z);
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
