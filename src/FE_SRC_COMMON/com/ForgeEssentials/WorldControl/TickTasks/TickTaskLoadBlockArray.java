package com.ForgeEssentials.WorldControl.TickTasks;

//Depreciated
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import com.ForgeEssentials.WorldControl.BlockArray;
import com.ForgeEssentials.WorldControl.ConfigWorldControl;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.BlockSaveable;
import com.ForgeEssentials.util.ITickTask;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;

public class TickTaskLoadBlockArray extends TickTaskLoadBlocks
{

	private BlockArray back;
	private ArrayList<BlockArray.LoadingBlock> blocksToLoad;

	public TickTaskLoadBlockArray(EntityPlayer player, BlockArray back)
	{
		super(player, new AreaBase(new Point(back.offX, back.offY, back.offZ), new Point(back.offX + back.sizeX, back.offY + back.sizeY, back.offZ + back.sizeZ)));
		this.back = back;
		blocksToLoad = this.back.getBlocksToLoad();
		last = blocksToLoad.size();
	}
	
	protected boolean usesCoordsSystem() {
		return back.isRelative;
	}
	
	protected boolean requiresBackup() {
		return back.isRelative;
	}

	protected boolean placeBlock() {
		if(back.isRelative) {
			
		}else{
			BlockArray.LoadingBlock block = blocksToLoad.get(current);
			boolean bool = place(block.x, block.y, block.z, block);
			return bool;
		}
		return false;
	}
	
}
