package com.forgeessentials.worldcontrol.TickTasks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.util.BackupArea;
import com.forgeessentials.util.BlockSaveable;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.AreaSelector.Point;
import com.forgeessentials.util.tasks.ITickTask;
import com.forgeessentials.worldcontrol.ConfigWorldControl;

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
	private EntityPlayer	player;
	private BackupArea		backup;
	private Point			effectOrigin;
	private int				effectRadius;
	private Mode			effectMode;

	// State info
	private int				changed;
	private boolean			isComplete;
	private Point			currentPos;

	public TickTaskTopManipulator(EntityPlayer play, BackupArea back, Point origin, int radius, Mode mode)
	{
		player = play;
		backup = back;
		effectOrigin = origin;
		effectRadius = radius;
		effectMode = mode;

		changed = 0;
		isComplete = false;

		currentPos = new Point(effectOrigin.x - effectRadius, 0, effectOrigin.z - effectRadius);
	}

	@Override
	public void tick()
	{
		int currentBlocksChanged = 0;
		boolean continueFlag = true;
		World world = player.worldObj;

		// Only store the X and Z, since we're considering columns only.
		int x = currentPos.x;
		int z = currentPos.z;
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

			// If Y goes under the world base, skip this column. (The End, I'm
			// looking at you.)
			if (0 <= y && y <= world.getActualHeight())
			{
				blockID = world.getBlockId(x, y, z);

				switch (effectMode)
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
							if (Block.isNormalCube(world.getBlockId(x, y, z)) || Block.blocksList[blockID].isLeaves(world, x, y, z))
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

			if (z > effectOrigin.z + effectRadius)
			{
				x++;
				z = effectOrigin.z - effectRadius;
				if (x > effectOrigin.x + effectRadius)
				{
					isComplete = true;
				}
			}

			if (isComplete || currentBlocksChanged >= ConfigWorldControl.blocksPerTick)
			{
				changed += currentBlocksChanged;
				currentPos = new Point(x, 0, z);
				continueFlag = false;
			}
		}
	}

	@Override
	public void onComplete()
	{
		PlayerInfo.getPlayerInfo(player.username).addUndoAction(backup);

		String confirmMessage = "";
		switch (effectMode)
			{
				case THAW:
					confirmMessage = "thawed.";
					break;
				case FREEZE:
					confirmMessage = "frozen.";
					break;
				case SNOW:
					confirmMessage = "dusted with snow.";
					break;
				case TILL:
					confirmMessage = "tilled.";
					break;
				case UNTILL:
					confirmMessage = "untilled.";
					break;
			}
		OutputHandler.chatConfirmation(player, String.format("%d blocks have been " + confirmMessage, changed));
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
