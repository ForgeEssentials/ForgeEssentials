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

public class TickTaskCopy extends TickTaskLoadBlocks
{
	private ArrayList<AreaBase>	applicable;
	BlockArray blocks;
	String name = "";
	BlockInfo fill;

	public TickTaskCopy(EntityPlayer player, AreaBase sel, String name, BlockInfo fill)
	{
		super(player, sel);
		if(info.copies.containsKey(name)) {
			info.copies.remove(name);
		}
		int offX = sel.getLowPoint().x - (int)player.posX;
		int offY = sel.getLowPoint().y - (int)player.posY;
		int offZ = sel.getLowPoint().z - (int)player.posZ;
		info.copies.put(name, new BlockArray(offX, offY, offZ, true, sel.getXLength()-1, sel.getYLength()-1, sel.getZLength()-1));
		blocks = info.copies.get(name);
		this.fill = fill;
		this.name = name;
	}
	
	protected void onCompleted() {
		blocks.finishAdding();
		info.copies.put(name, blocks);
	}

	public TickTaskCopy(EntityPlayer player, AreaBase sel, String name, BlockInfo fill, ArrayList<AreaBase> applicable)
	{
		this(player, sel, name, fill);
		this.applicable = applicable;
	}
	
	protected boolean placeBlock() {
		if(isApplicable(x, y, z)) {
			blocks.addBlock(world, x, y, z, false);
			if(fill!=null)place(x, y, z, fill);
			return true;
		}else{
			blocks.addBlock(world, x, y, z, (short)0, (byte)0);
			return true;
		}
	}
	
	protected void sendCompleteMessage() {
		OutputHandler.chatConfirmation(player, "" + changed + " blocks copied in "+(double)ticks/20D+" seconds to "+name);
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
