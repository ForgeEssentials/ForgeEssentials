package com.ForgeEssentials.WorldControl.tickTasks;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.WorldControl.BackupArea;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class TickTaskSetSelection implements ITickTask
{
	private World world;
	private int blockID;
	private int metadata;
	private BackupArea back;
	private EntityPlayer player;
	private AreaBase area; // this is better if its a clone of the Selection rather than an instance
	
	boolean completed = false;
	
	public TickTaskSetSelection(EntityPlayer player, int blockID, int metadata, BackupArea back, AreaBase area)
	{
		this.player = player;
		this.blockID = blockID;
		this.metadata = metadata;
		this.back = back;
		this.area = new Selection(area.getLowPoint(), area.getHighPoint());
		world = player.worldObj;
	}

	@Override
	public void tick()
	{
		// TODO Auto-generated method stub
		// place the blocks..
	}
	
	@Override
	public void onComplete()
	{
		// TODO Auto-generated method stub
		// chat confirmation and stuff
		// add backup to player undo thingy...
	}

	@Override
	public boolean isComplete()
	{
		return completed;
	}

	@Override
	public boolean editsBlocks()
	{
		return true;
	}

}
