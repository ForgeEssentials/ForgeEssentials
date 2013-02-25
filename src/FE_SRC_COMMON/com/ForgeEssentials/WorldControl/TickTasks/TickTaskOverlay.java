package com.ForgeEssentials.WorldControl.TickTasks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.ForgeEssentials.WorldControl.ConfigWorldControl;
import com.ForgeEssentials.WorldControl.TickTasks.TickTaskDistribution.PooledInfo;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BlockArrayBackup;
import com.ForgeEssentials.util.BlockInfo;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

public class TickTaskOverlay extends TickTaskLoadBlocks
{
	private ArrayList<AreaBase>	applicable;

	BlockInfo to;

	public TickTaskOverlay(EntityPlayer player, AreaBase sel, BlockInfo to)
	{
		super(player, sel);
		this.to=to;
	}

	public TickTaskOverlay(EntityPlayer player, AreaBase sel, BlockInfo to, ArrayList<AreaBase> applicable)
	{
		this(player, sel, to);
		this.applicable = applicable;
	}
	
	protected static class PooledInfo {
		int id;
		int id2;
		int x;
		int y;
		int z;
		private static ArrayList<PooledInfo> pool = new ArrayList<PooledInfo>();
		private PooledInfo(int id, int id2, int x, int y, int z) {
			this.id=id;
			this.id2=id2;
			this.x = x;
			this.y = y;
			this.z = z;
			pool.add(this);
		}
		public static PooledInfo getFromPool(World world, int x, int y, int z) {
			for(PooledInfo inf : pool) {
				if(inf.x==x&&inf.y==y&&inf.z==z) {
					return inf;
				}
			}
			return new PooledInfo(world.getBlockId(x, y, z), world.getBlockId(x, y-1, z), x, y, z);
		}
		
		public static ArrayList<PooledInfo> getAllInfo() {
			return pool;
		}
		
		public static void clear() {
			pool.clear();
		}
	}
	
	protected int getIterations() {
		return 2;
	}
	
	protected void changeIteration() {
		if(iter==1)changed=0;
	}
	
	protected void onCompleted() {
		PooledInfo.clear();
	}
	
	protected boolean placeBlock() {
		PooledInfo inf = PooledInfo.getFromPool(world, x, y, z);
		if(iter==1) {
			if((Block.blocksList[inf.id]==null || Block.blocksList[inf.id].isBlockReplaceable(world, x, y, z)) && isApplicable(x, y, z) && inf.id2!=0) {
				return place(x, y, z, to.randomBlock());
			}
		}
		return true;
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
