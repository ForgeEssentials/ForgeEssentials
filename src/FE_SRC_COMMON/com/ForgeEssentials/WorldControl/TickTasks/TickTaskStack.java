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
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

public class TickTaskStack extends TickTaskLoadBlocks
{
	private ArrayList<AreaBase>	applicable;

	protected BlockArray back;
	protected ArrayList<BlockArray.LoadingBlock> blocksToLoad;
	FunctionHelper.Direction dir;
	protected int times = 1;
	protected int ilast = 0;

	public TickTaskStack(EntityPlayer player, BlockArray back, AreaBase area, FunctionHelper.Direction dir, int times)
	{
		super(player, area);
		this.back = back;
		this.times = times;
		blocksToLoad = this.back.getBlocksToLoad();
		last = blocksToLoad.size() * getIterations();
		ilast = blocksToLoad.size();
		this.dir = dir;
	}
	
	protected int getIterations() {
		return times;
	}
	
	protected void changeIteration() {
		int xL = sel.getXLength();
		int yL = sel.getYLength();
		int zL = sel.getZLength();
		if(dir==FunctionHelper.Direction.UP) {
			Point pt1 = sel.getHighPoint();
			Point pt2 = sel.getLowPoint();
			pt1.y+=yL;
			pt2.y+=yL;
			sel = new AreaBase(pt2, pt1);
		}else if(dir==FunctionHelper.Direction.DOWN) {
			Point pt1 = sel.getHighPoint();
			Point pt2 = sel.getLowPoint();
			pt1.y-=yL;
			pt2.y-=yL;
			sel = new AreaBase(pt2, pt1);
		}else if(dir==FunctionHelper.Direction.EAST) {
			Point pt1 = sel.getHighPoint();
			Point pt2 = sel.getLowPoint();
			pt1.x+=xL;
			pt2.x+=xL;
			sel = new AreaBase(pt2, pt1);
		}else if(dir==FunctionHelper.Direction.WEST) {
			Point pt1 = sel.getHighPoint();
			Point pt2 = sel.getLowPoint();
			pt1.x-=xL;
			pt2.x-=xL;
			sel = new AreaBase(pt2, pt1);
		}else if(dir==FunctionHelper.Direction.NORTH) {
			Point pt1 = sel.getHighPoint();
			Point pt2 = sel.getLowPoint();
			pt1.z-=zL;
			pt2.z-=zL;
			sel = new AreaBase(pt2, pt1);
		}else if(dir==FunctionHelper.Direction.SOUTH) {
			Point pt1 = sel.getHighPoint();
			Point pt2 = sel.getLowPoint();
			pt1.z+=zL;
			pt2.z+=zL;
			sel = new AreaBase(pt2, pt1);
		}
		x = sel.getLowPoint().x;
		y = sel.getLowPoint().y;
		z = sel.getLowPoint().z;
	}

	public TickTaskStack(EntityPlayer player, BlockArray array, AreaBase area, FunctionHelper.Direction dir, int times, ArrayList<AreaBase> applicable)
	{
		this(player, array, area, dir, times);
		this.applicable = applicable;
	}
	
	protected boolean placeBlock() {
		if(back.isRelative) {
			if(isApplicable(x, y, z)) {
				BlockArray.LoadingBlock block = blocksToLoad.get(currentIter);
				return place(x, y, z, block);
			}
			return false;
		}else{
			BlockArray.LoadingBlock block = blocksToLoad.get(currentIter);
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
