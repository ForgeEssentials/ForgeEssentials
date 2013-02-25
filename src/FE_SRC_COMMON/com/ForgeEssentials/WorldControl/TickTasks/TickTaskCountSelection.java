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

public class TickTaskCountSelection extends TickTaskLoadBlocks
{
	private ArrayList<AreaBase>	applicable;

	BlockInfo from;

	public TickTaskCountSelection(EntityPlayer player, AreaBase sel, BlockInfo from)
	{
		super(player, sel);
		this.from=from;
	}

	public TickTaskCountSelection(EntityPlayer player, AreaBase sel, BlockInfo from, ArrayList<AreaBase> applicable)
	{
		this(player, sel, from);
		this.applicable = applicable;
	}
	
	protected boolean placeBlock() {
		int id = world.getBlockId(x, y, z);
		int m = world.getBlockMetadata(x, y, z);
		if (from.compare(new BlockInfo.SingularBlockInfo(Block.blocksList[id], m, null)))
		{
			if(isApplicable(x, y, z))return true;
		}
		return false;
	}
	
	protected void sendCompleteMessage() {
		OutputHandler.chatConfirmation(player, "Counted "+changed+" blocks!");
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
