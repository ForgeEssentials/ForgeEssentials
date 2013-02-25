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
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

public class TickTaskDistribution extends TickTaskLoadBlocks
{
	private ArrayList<AreaBase>	applicable;

	public TickTaskDistribution(EntityPlayer player, AreaBase sel)
	{
		super(player, sel);
	}

	public TickTaskDistribution(EntityPlayer player, AreaBase sel, ArrayList<AreaBase> applicable)
	{
		this(player, sel);
		this.applicable = applicable;
	}
	
	protected static class PooledInfo {
		int id;
		int meta;
		int amt;
		static int total = 0;
		private static ArrayList<PooledInfo> pool = new ArrayList<PooledInfo>();
		private PooledInfo(int id, int meta) {
			this.id=id;
			this.meta=meta;
			this.amt = 1;
			pool.add(this);
		}
		public static PooledInfo getFromPool(int id, int meta) {
			for(PooledInfo inf : pool) {
				if(inf.id==id&&inf.meta==meta) {
					return inf;
				}
			}
			return new PooledInfo(id, meta);
		}
		
		public static void addToPool(int id, int meta) {
			for(PooledInfo inf : pool) {
				if(inf.id==id&&inf.meta==meta) {
					inf.amt++;
					total++;
					return;
				}
			}
			new PooledInfo(id, meta);
			total++;
		}
		
		public static ArrayList<PooledInfo> getAllInfo() {
			return pool;
		}
	}
	
	protected boolean placeBlock() {
		if(isApplicable(x, y, z)) {
			int id = world.getBlockId(x, y, z);
			int m = world.getBlockMetadata(x, y, z);
			PooledInfo.addToPool(id, m);
			return true;
		}
		return false;
	}
	
	protected void sendCompleteMessage() {
		int total = PooledInfo.total;
		for(PooledInfo inf : PooledInfo.getAllInfo()) {
			OutputHandler.chatConfirmation(player, FunctionHelper.getNameFromItemStack(new ItemStack(Block.blocksList[inf.id], 1, inf.meta))+": "+(((double)inf.amt)/((double)total)*100)+"%");
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
