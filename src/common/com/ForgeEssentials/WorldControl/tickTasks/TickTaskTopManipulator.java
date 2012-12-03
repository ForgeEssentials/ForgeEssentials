package com.ForgeEssentials.WorldControl.tickTasks;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.BlockSaveable;
import com.ForgeEssentials.WorldControl.ModuleWorldControl;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskTopManipulator.Mode;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

public class TickTaskTopManipulator implements ITickTask
{
	public enum Mode
	{
		THAW, // Removes top snow/ice block. Replaces ice with water.
		FREEZE, // Replaces exposed water with ice.
		SNOW, // Adds a dusting of snow to exposed non-liquid blocks.
		TILL, // Transforms exposed grass & dirt into tilled farmland.
		UNTILL, // Replaces farmland with bare dirt
	}

	// Data that is determined at start and does not change.
	private EntityPlayer player;
	private BackupArea backup;
	private Point effectOrigin;
	private int effectRadius;
	private Mode effectMode;
	
	// State info
	private int changed;
	private int ticks;
	private boolean isComplete;
	private Point currentPos;
	

	public TickTaskTopManipulator(EntityPlayer play, BackupArea back, Point origin, int radius, Mode mode)
	{
		this.player = play;
		this.backup = back;
		this.effectOrigin = origin;
		this.effectRadius = radius;
		this.effectMode = mode;
		
		this.ticks = 0;
		this.changed = 0;
		this.isComplete = false;
		
		this.currentPos = new Point(this.effectOrigin.x - this.effectRadius, 0, this.effectOrigin.z - this.effectRadius);
	}

	@Override
	public void tick()
	{
		this.ticks++;
		int currentBlocksChanged = 0;
		boolean continueFlag = true;
		World world = player.worldObj;
		
		// Only store the X and Z, since we're considering columns only.
		int x = this.currentPos.x;
		int z = this.currentPos.z;
		int y = 0;
		int blockID;
		
		while (continueFlag)
		{
			// Find the y coord of the first exposed, non-air block.
			y = world.getActualHeight();
			while (world.isAirBlock(x, y, z) && y >= 0)
			{
				y--;
			}
			
			// If Y goes under the world base, skip this column. (The End, I'm looking at you.)
			if (0 <= y && y <= world.getActualHeight())
			{
				blockID = world.getBlockId(x, y, z);
				
				switch (this.effectMode)
				{
					case THAW:
						if (blockID == Block.ice.blockID)
						{
							// Replace ice with water.
							backup.before.add(new BlockSaveable(world, x, y, z));
							world.setBlock(x, y, z, Block.waterMoving.blockID);
							backup.after.add(new BlockSaveable(world, x, y, z));
							currentBlocksChanged++;
						}
						else if (blockID == Block.snow.blockID)
						{
							// Remove snow.
							backup.before.add(new BlockSaveable(world, x, y, z));
							world.setBlock(x, y, z, 0);
							backup.after.add(new BlockSaveable(world, x, y, z));
							currentBlocksChanged++;
						}
						break;
					case FREEZE:
						if (blockID == Block.waterMoving.blockID || blockID == Block.waterStill.blockID)
						{
							// Both water types become ice.
							backup.before.add(new BlockSaveable(world, x, y, z));
							world.setBlock(x, y, z, Block.ice.blockID);
							backup.after.add(new BlockSaveable(world, x, y, z));
							currentBlocksChanged++;
						}
						break;
					case SNOW:
						if (Block.isNormalCube(world.getBlockId(x, y, z)))
						{
							// Add snow covering to the block above.
							backup.before.add(new BlockSaveable(world, x, y + 1, z));
							world.setBlock(x, y + 1, z, Block.snow.blockID);
							backup.after.add(new BlockSaveable(world, x, y + 1, z));
							currentBlocksChanged++;
						}
						break;
					case TILL:
						if (blockID == Block.dirt.blockID || blockID == Block.grass.blockID)
						{
							backup.before.add(new BlockSaveable(world, x, y, z));
							world.setBlock(x, y, z, Block.tilledField.blockID);
							backup.after.add(new BlockSaveable(world, x, y, z));
							currentBlocksChanged++;
						}
						break;
					case UNTILL:
						if (blockID == Block.tilledField.blockID)
						{
							backup.before.add(new BlockSaveable(world, x, y, z));
							world.setBlock(x, y, z, Block.dirt.blockID);
							backup.after.add(new BlockSaveable(world, x, y, z));
							currentBlocksChanged++;
						}
						break;
				}
			}
			
			z++;
			
			if (z > (this.effectOrigin.z + this.effectRadius))
			{
				x++;
				z = this.effectOrigin.z - this.effectRadius;
				if (x > (this.effectOrigin.x + this.effectRadius))
				{
					this.isComplete = true;
				}
			}
			
			if (this.isComplete || currentBlocksChanged >= ModuleWorldControl.WCblocksPerTick)
			{
				this.changed += currentBlocksChanged;
				this.currentPos = new Point(x, 0, z);
				continueFlag = false;
			}
		}
	}

	@Override
	public void onComplete()
	{
		PlayerInfo.getPlayerInfo(player).addUndoAction(backup);
		
		String confirmMessage = "";
		switch (this.effectMode)
		{
			case THAW:
				confirmMessage = "thaw";
				break;
			case FREEZE:
				confirmMessage = "freeze";
				break;
			case SNOW:
				confirmMessage = "snow";
				break;
			case TILL:
				confirmMessage = "till";
				break;
			case UNTILL:
				confirmMessage = "untill";
				break;
		}
		OutputHandler.chatConfirmation(player, Localization.format("message.wc." + confirmMessage + "Confirm",
				this.changed));
	}

	@Override
	public boolean isComplete()
	{
		return isComplete;
	}

	@Override
	public boolean editsBlocks()
	{
		return true;
	}

}
