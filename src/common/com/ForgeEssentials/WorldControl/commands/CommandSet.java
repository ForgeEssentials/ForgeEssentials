package com.ForgeEssentials.WorldControl.commands;

import java.util.HashMap;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.PlayerInfo;

public class CommandSet extends WorldControlCommandBase
{

	private HashMap<String, Point> playerLastSet = new HashMap<String, Point>();

	@Override
	public String getName()
	{
		return "set";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		if (playerLastSet.containsKey(player))
		{
			OutputHandler.chatError(player, "Your last set is still being processed, stop causing lag!");
			return;
		}
		playerLastSet.put(player.username, PlayerInfo.getPlayerInfo(player).getPoint1());
		completeCommand();
		PlayerInfo info = PlayerInfo.getPlayerInfo(player);
		Point point1 = PlayerInfo.getPlayerInfo(player.username).getPoint1();
		Point point2 = PlayerInfo.getPlayerInfo(player.username).getPoint2();
		boolean goodX = point1.x <= point2.x;
		boolean goodY = point1.y <= point2.y;
		boolean goodZ = point1.z <= point2.z;
		BackupArea back = new BackupArea();
		int changed = 0;
		for (int x = point1.x; goodX ? x <= point2.x : x >= point2.x;)
		{
			for (int y = point1.y; goodY ? y <= point2.y : y >= point2.y;)
			{
				for (int z = point1.z; goodZ ? z <= point2.z : z >= point2.z;)
				{
					if (setBlock(x, y, z, inf, sender, back))
					{
						changed++;
					}
					if (goodZ)
						z++;
					else
						z--;
				}
				if (goodY)
					y++;
				else
					y--;
			}
			if (goodX)
				x++;
			else
				x--;
		}

		addBackup(sender.username, back);
		sender.addChatMessage("Set " + changed + " Blocks to " + getIdString(inf));
	}

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
