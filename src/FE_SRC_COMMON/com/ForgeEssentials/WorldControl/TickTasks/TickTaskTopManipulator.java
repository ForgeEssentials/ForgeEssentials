package com.ForgeEssentials.WorldControl.TickTasks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.ForgeEssentials.WorldControl.BlockArrayBackup;
import com.ForgeEssentials.WorldControl.ConfigWorldControl;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.BlockSaveable;
import com.ForgeEssentials.util.ITickTask;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;

public class TickTaskTopManipulator extends TickTaskLoadBlocks
{
	public enum Mode
	{
		THAW, // Removes top snow/ice block. Replaces ice with water.
		FREEZE, // Replaces exposed water with ice.
		SNOW, // Adds a dusting of snow to exposed non-liquid blocks.
		TILL, // Transforms exposed grass & dirt into tilled farmland.
		UNTILL, // Replaces farmland with bare dirt.
		GREEN, // Turns dirt into grass.
	}
	private Mode			effectMode;

	public TickTaskTopManipulator(EntityPlayer player, AreaBase area, Mode mode)
	{
		super(player, area);
		effectMode = mode;
	}
	
	protected boolean placeBlock() {
		int blockID = world.getBlockId(x, y, z);
		int aboveID = world.getBlockId(x, y+1, z);
		if(Block.blocksList[aboveID]==null||Block.blocksList[aboveID].isOpaqueCube()) {
		switch (effectMode)
		{
			case THAW:
				if (blockID == Block.ice.blockID)
				{
					place(x, y, z, Block.waterMoving.blockID, 0);
				}
				else if (blockID == Block.snow.blockID)
				{
					place(x, y, z, 0, 0);
				}
				break;
			case FREEZE:
				if (blockID == Block.waterMoving.blockID || blockID == Block.waterStill.blockID)
				{
					place(x, y, z, Block.ice.blockID, 0);
				}
				break;
			case SNOW:
				if (Block.isNormalCube(world.getBlockId(x, y, z)) || (Block.blocksList[blockID] == null || Block.blocksList[blockID].isLeaves(world, x, y, z)))
				{
					place(x, y, z, Block.snow.blockID, 0);
				}
				break;
			case TILL:
				if (blockID == Block.dirt.blockID || blockID == Block.grass.blockID)
				{
					place(x, y, z, Block.tilledField.blockID, 0);
				}
				break;
			case UNTILL:
				if (blockID == Block.tilledField.blockID)
				{
					place(x, y, z, Block.dirt.blockID, 0);
				}
				break;
			case GREEN:
				if (blockID == Block.dirt.blockID)
				{
					place(x, y, z, Block.grass.blockID, 0);
				}
				break;
		}
		}
		return false;
	}

}
