package com.ForgeEssentials.WorldControl.commands;

import java.util.HashMap;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.BlockSaveable;
import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.PlayerInfo;

public class CommandSet extends WorldControlCommandBase
{

	@Override
	public String getName()
	{
		return "set";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		int ID = 0;
		int metadata = 0;
		
		PlayerInfo info = PlayerInfo.getPlayerInfo(player);
		World world = player.worldObj;
		Selection sel = info.getSelection();
		BackupArea back = new BackupArea();
		int changed = 0;
		
		for (int x = sel.getLowPoint().x; x < sel.getHighPoint().x; x++)
			for (int y = sel.getLowPoint().y; y < sel.getHighPoint().y; y++)
				for (int z = sel.getLowPoint().z; z < sel.getHighPoint().z; z++)
				{
					if (metadata == -1)
					{
						if (ID == world.getBlockId(x, y, z))
							continue;

						back.before.add(new BlockSaveable(world, x, y, z));
						world.setBlock(x, y, z, ID);
						back.after.add(new BlockSaveable(world, x, y, z));

						changed++;
					}
					else
					{
						if (ID == world.getBlockId(x, y, z) && metadata == world.getBlockMetadata(x, y, z))
							continue;

						back.before.add(new BlockSaveable(world, x, y, z));
						world.setBlockAndMetadata(x, y, z, ID, metadata);
						back.after.add(new BlockSaveable(world, x, y, z));
						changed++;
					}
				}

		info.addUndoAction(back);
		OutputHandler.chatConfirmation(player, "Set " + changed + " Blocks to " + new ItemStack(ID, 1, metadata));
	}

	/*
	@Override
	public void completeCommand()
	{
		int targetID = inventoryStacks[0].itemID;
		int targetMetadata = inventoryStacks[0].getItemDamage();
		int blocksSearched = 0;
		for(int y = lastSearch.y; y >= -yCoord; y--) {
			for(int x = Math.abs(lastSearch.x); x <= range; x++) {
				for(int z = Math.abs(lastSearch.z); z <= range; z++) {
					for(int i = lastSearch.x >= 0 ? 0 : 1; i < 2; i++) {
						for(int j = lastSearch.z >= 0 ? 0 : 1; j < 2; j++) {
							int xRel = i == 0 ? x : -x;
							int zRel = j == 0 ? z : -z;
							if(worldObj.getBlockId(xCoord + xRel, yCoord + y, zCoord + zRel) == targetID && worldObj.getBlockMetadata(xCoord + xRel, yCoord + y, zCoord + zRel) == targetMetadata)
								detectedBlocks.add(new Point(xCoord + xRel, yCoord + y, zCoord + zRel));
							if(++blocksSearched == 20) {
								lastSearch.set(xRel, y, zRel);
								return;
							}
						}
					}
				}
				lastSearch.z = 0;
			}
			lastSearch.x = 0;
		}
		lastSearch.y = 0;
	}
	*/

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		// TODO: check permissions.
		return true;
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/" + getCommandName() + " [id:metadata]";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Set the your selection to a certain id and metadata";
	}
}
