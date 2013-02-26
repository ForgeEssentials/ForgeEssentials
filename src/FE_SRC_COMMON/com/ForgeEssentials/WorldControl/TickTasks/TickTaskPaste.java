package com.ForgeEssentials.WorldControl.TickTasks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.ForgeEssentials.WorldControl.ConfigWorldControl;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BlockArray;
import com.ForgeEssentials.util.BlockArrayBackup;
import com.ForgeEssentials.util.BlockInfo;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

public class TickTaskPaste extends TickTaskLoadBlocks
{
	private ArrayList<AreaBase>	applicable;

	protected BlockArray back;
	protected ArrayList<BlockArray.LoadingBlock> blocksToLoad;

	public TickTaskPaste(EntityPlayer player, BlockArray back, AreaBase area)
	{
		super(player, area);
		this.back = back;
		blocksToLoad = this.back.getBlocksToLoad();
		last = blocksToLoad.size();
	}

	public TickTaskPaste(EntityPlayer player, BlockArray array, AreaBase area, ArrayList<AreaBase> applicable)
	{
		this(player, array, area);
		this.applicable = applicable;
	}
	
	protected boolean placeBlock() {
		if(back.isRelative) {
			if(isApplicable(x, y, z)) {
				BlockArray.LoadingBlock block = blocksToLoad.get(current);
				return place(x, y, z, block);
			}
			return false;
		}else{
			BlockArray.LoadingBlock block = blocksToLoad.get(current);
			return place(block.x, block.y, block.z, block);
		}
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
