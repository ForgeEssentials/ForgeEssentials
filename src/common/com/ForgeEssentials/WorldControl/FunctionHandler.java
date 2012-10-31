package com.ForgeEssentials.WorldControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.ForgeEssentials.PlayerInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenHugeTrees;
import net.minecraft.src.WorldGenShrub;
import net.minecraft.src.WorldGenTaiga1;
import net.minecraft.src.WorldGenTaiga2;
import net.minecraft.src.WorldGenTrees;
import net.minecraft.src.WorldGenerator;

import java.util.Map;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.commands.CommandInfo;

public class FunctionHandler
{
	public static FunctionHandler instance;

	public FunctionHandler()
	{
		instance = this;
	}

	public static int worldEdits = 0;

	public static void addBackup(BackupArea back)
	{
		backup.add(back);
		worldEdits++;
	}

	public static List<BackupArea> backup = new ArrayList<BackupArea>();
	public static Map<PlayerInfo, CopyArea> cpy = new HashMap<PlayerInfo, CopyArea>();
	public static WorldGenerator trees = new WorldGenTrees(false);

	public static void undoCommand(EntityPlayer sender)
	{
		BackupArea backu = null;
		int index = -1;
		String user = sender.username;
		int worldEdit = 0;
		for (int i = 0; i < backup.size(); i++)
		{
			// TODO: get backup from PlayerInfo and redo it there.
			/*
			BackupArea back = backup.get(i);
			if (back.username.equalsIgnoreCase(user) && back.worldEdit >= worldEdit && back.hasUndone == false)
			{
				worldEdit = back.worldEdit;
				backu = back;
				index = i;
			}
			*/
		}
		if (backu != null && index >= 0)
		{
			backu.loadAreaBefore(sender.worldObj);
		}
	}

	public static void redoCommand(EntityPlayer sender)
	{
		String user = sender.username;
		for (int i = 0; i <= backup.size(); i++)
		{
			// TODO: get backup from PlayerInfo and redo it there.
			/*
			BackupArea back = backup.get(i);
			if (back.username.equalsIgnoreCase(user) && back.worldEdit >= 0 && back.hasUndone == true)
			{
				if (back != null && i >= 0)
				{
					back.loadAreaAfter(sender.worldObj);
					return;
				}
			}
			*/
		}
		// if(backu!=null&&index>=0) {
		// backu.loadAreaAfter(sender.worldObj);
		// }
	}

	/**
	 * 
	 * places block in world
	 * 
	 * @param x
	 *            X-Coord
	 * @param y
	 *            Y-Coord
	 * @param z
	 *            Z-Coord
	 * @param inf
	 *            Command Info
	 * @param sender
	 *            Player
	 * @param back
	 *            Backup
	 * @return when counter should go up
	 */
	public boolean placeBlock(int x, int y, int z, CommandInfo inf, EntityPlayer sender, BackupArea back)
	{
		int bid = sender.worldObj.getBlockId(x, y, z);
		int meta = sender.worldObj.getBlockMetadata(x, y, z);
		int blockID = 0;
		int metadata = 0;
		boolean good = true;
		if (inf.getSize() == 1)
		{
			int[] temp = inf.getInfo(0);
			blockID = temp[0];
			metadata = temp[1];
		}
		else if (inf.getSize() > 1)
		{
			good = false;
			int size = inf.getSize();
			int[] chosenInf = inf.getInfo(rand.nextInt(size));
			boolean canPlace = Block.blocksList[chosenInf[0]] != null ? Block.blocksList[chosenInf[0]].canBlockStay(sender.worldObj, x, y, z) : true;
			if (!canPlace)
				return false;
			back.addBlockBefore(x, y, z, bid, meta);
			sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, chosenInf[0], chosenInf[1]);
			back.addBlockAfter(x, y, z, chosenInf[0], chosenInf[1]);
			return true;
		}

		if (!(bid == blockID && meta == metadata) && good)
		{
			boolean canPlace = Block.blocksList[blockID] == null ? true : Block.blocksList[blockID].canBlockStay(sender.worldObj, x, y, z);
			if (!canPlace)
				return false;
			back.addBlockBefore(x, y, z, bid, meta);
			sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, blockID, metadata);
			back.addBlockAfter(x, y, z, blockID, metadata);
			return true;
		}
		return false;
	}

	public boolean countBlock(int x, int y, int z, CommandInfo inf, EntityPlayer sender)
	{
		int bid = sender.worldObj.getBlockId(x, y, z);
		int meta = sender.worldObj.getBlockMetadata(x, y, z);
		for (int i = 0; i < inf.getSize(); i++)
		{
			int[] temp = inf.getInfo(i);
			if (bid == temp[0] && meta == temp[1])
			{
				return true;
			}
		}
		return false;
	}

	public static Random rand = new Random();

	public void setCommand(CommandInfo inf, EntityPlayer sender)
	{
		Point point1 = PlayerInfo.getPlayerPoint1(sender.username);
		Point point2 = PlayerInfo.getPlayerPoint2(sender.username);
		boolean goodX = point1.x <= point2.x;
		boolean goodY = point1.y <= point2.y;
		boolean goodZ = point1.z <= point2.z;
		BackupArea back = new BackupArea();
		int changed = 0;
		for (int x = point1.x; goodX ? x <= point2.x : x >= point2.x;)
		{
			for (int y = point1.y; goodY ? y <= point2.y : y >= point2.y;)
			{
				for (int z = point1.z; goodZ ? z <= point2.x : z >= point2.z;)
				{
					if (placeBlock(x, y, z, inf, sender, back))
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

		addBackup(back);
		sender.addChatMessage("Set " + changed + " Blocks to " + getIdString(inf));
	}

	public void delCommand(CommandInfo inf, EntityPlayer sender)
	{
		Point point1 = PlayerInfo.getPlayerPoint1(sender.username);
		Point point2 = PlayerInfo.getPlayerPoint2(sender.username);
		boolean goodX = point1.x <= point2.x;
		boolean goodY = point1.y <= point2.y;
		boolean goodZ = point1.z <= point2.z;
		BackupArea back = new BackupArea();
		int changed = 0;
		for (int x = PlayerInfo.getPlayerPoint1(sender.username).x; goodX ? x <= PlayerInfo.getPlayerPoint2(sender.username).x : x >= PlayerInfo.getPlayerPoint2(sender.username).x;)
		{
			for (int y = point1.y; goodY ? y <= point2.y : y >= point2.y;)
			{
				for (int z = point1.z; goodZ ? z <= point2.z : z >= point2.z;)
				{
					if (placeBlock(x, y, z, inf, sender, back))
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

		addBackup(back);
		sender.addChatMessage("Deleted " + changed + " Blocks");
	}

	public void cpyclearCommand(CommandInfo inf, EntityPlayer sender)
	{
		Point point1 = PlayerInfo.getPlayerPoint1(sender.username);
		Point point2 = PlayerInfo.getPlayerPoint2(sender.username);
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
					if (placeBlock(x, y, z, inf, sender, back))
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

		addBackup(back);
	}

	public void saveCommand(String name, EntityPlayer sender)
	{
		Point point1 = PlayerInfo.getPlayerPoint1(sender.username);
		Point point2 = PlayerInfo.getPlayerPoint2(sender.username);
		boolean goodX = point1.x <= point2.x;
		boolean goodY = point1.y <= point2.y;
		boolean goodZ = point1.z <= point2.z;
		BlueprintArea blue = new BlueprintArea(sender.username, worldEdits);
		int changed = 0;
		for (int x = point1.x; goodX ? x <= point2.x : x >= point2.x;)
		{
			for (int y = point1.y; goodY ? y <= point2.y : y >= point2.y;)
			{
				for (int z = point1.z; goodZ ? z <= point2.z : z >= point2.z;)
				{
					int bid = sender.worldObj.getBlockId(x, y, z);
					int meta = sender.worldObj.getBlockMetadata(x, y, z);
					if (bid != 0)
						blue.addBlock(x, y, z, bid, meta);
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
		blue.start = point1;
		blue.end = point2;
		worldEdits++;
		blue.save(sender.username + "_" + name);
		sender.addChatMessage("Saved File: " + name);
	}

	public void loadCommand(String name, EntityPlayer sender, boolean clear)
	{
		BackupArea back = new BackupArea();
		BlueprintArea.load(sender.username + "_" + name).loadArea(sender, back, clear);
		addBackup(back);
		sender.addChatMessage("Loaded File: " + name);
	}

	public void loadRelativeCommand(String name, EntityPlayer sender, boolean clear)
	{
		BackupArea back = new BackupArea();
		BlueprintArea.load(sender.username + "_" + name).loadAreaRelative(sender, back, clear);
		addBackup(back);
		sender.addChatMessage("Loaded(Relative) File: " + name);
	}

	public String getIdString(CommandInfo info)
	{
		String str = "";
		for (int i = 0; i < info.getSize(); i++)
		{
			int[] temp = info.getInfo(i);
			str = str + (temp[1] == 0 ? temp[0] : temp[0] + ":" + temp[1]) + ", ";
		}
		if (str.endsWith(", "))
			str = str.substring(0, str.length() - 2);
		return str;
	}

	public void setHollowCommand(CommandInfo inf, EntityPlayer sender, boolean clearInsides)
	{
		Point point1 = PlayerInfo.getPlayerPoint1(sender.username);
		Point point2 = PlayerInfo.getPlayerPoint2(sender.username);
		boolean goodX = point1.x <= point2.x;
		boolean goodY = point1.y <= point2.y;
		boolean goodZ = point1.z <= point2.z;
		BackupArea back = new BackupArea();
		int changed = 0;
		int changedinside = 0;
		for (int x = point1.x; goodX ? x <= point2.x : x >= point2.x;)
		{
			for (int y = point1.y; goodY ? y <= point2.y : y >= point2.y;)
			{
				for (int z = point1.z; goodZ ? z <= point2.z : z >= point2.z;)
				{
					if (x == point1.x || x == point2.x || z == point1.z || z == point2.z || y == point1.y || y == point2.y)
					{
						/*
						 * int bid=sender.worldObj.getBlockId(x, y, z); int meta=sender.worldObj.getBlockMetadata(x, y, z); if(!(bid==blockID&&meta==metadata)) { changed++; back.addBlockBefore(x, y, z, bid, meta); sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, blockID, metadata); back.addBlockAfter(x, y, z, blockID, metadata); }
						 */
						if (placeBlock(x, y, z, inf, sender, back))
						{
							changed++;
						}
					}
					else if (clearInsides)
					{
						/*
						 * int bid=sender.worldObj.getBlockId(x, y, z); int meta=sender.worldObj.getBlockMetadata(x, y, z); back.addBlockBefore(x, y, z, bid, meta); sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, 0, 0); back.addBlockAfter(x, y, z, blockID, metadata);
						 */
						CommandInfo temp = new CommandInfo();
						temp.setInfo(0, 0);
						if (placeBlock(x, y, z, temp, sender, back))
						{
							changed++;
						}
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

		addBackup(back);
		sender.addChatMessage("Set " + changed + " Blocks to " + getIdString(inf));
	}

	public void countCommand(CommandInfo inf, boolean all, EntityPlayer sender)
	{
		Point point1 = PlayerInfo.getPlayerPoint1(sender.username);
		Point point2 = PlayerInfo.getPlayerPoint2(sender.username);
		boolean goodX = point1.x <= point2.x;
		boolean goodY = point1.y <= point2.y;
		boolean goodZ = point1.z <= point2.z;
		int count = 0;
		for (int x = point1.x; goodX ? x <= point2.x : x >= point2.x;)
		{
			for (int y = point1.y; goodY ? y <= point2.y : y >= point2.y;)
			{
				for (int z = point1.z; goodZ ? z <= point2.z : z >= point2.z;)
				{
					if (all)
					{
						count++;
					}
					else
					{
						if (countBlock(x, y, z, inf, sender))
						{
							count++;
						}
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
		sender.addChatMessage("Counted " + count + "" + (all ? "" : " of type: " + getIdString(inf)));
	}

	public class DistrInfo
	{
		int id = 0;
		int meta = 0;
		int amount = 0;
	}

	public class DistrList
	{
		public List<DistrInfo> info = new ArrayList<DistrInfo>();

		public void addBlock(int id, int meta)
		{
			int indexToChange = 0;
			boolean change = false;
			DistrInfo toReplace = null;
			for (int i = 0; i < info.size(); i++)
			{
				if (info.get(i).id == id && info.get(i).meta == meta)
				{
					DistrInfo inf = info.get(i);
					inf.amount = inf.amount + 1;
					indexToChange = i;
					change = true;
					toReplace = inf;
				}
			}
			if (change)
			{
				info.remove(indexToChange);
				info.add(indexToChange, toReplace);
			}
			else
			{
				DistrInfo inf = new DistrInfo();
				inf.id = id;
				inf.meta = meta;
				inf.amount = 1;
				info.add(inf);
			}
		}
	}

	public static void log(String str)
	{
		System.out.println(str);
	}

	public void distrCommand(EntityPlayer sender)
	{
		Point point1 = PlayerInfo.getPlayerPoint1(sender.username);
		Point point2 = PlayerInfo.getPlayerPoint2(sender.username);
		boolean goodX = point1.x <= point2.x;
		boolean goodY = point1.y <= point2.y;
		boolean goodZ = point1.z <= point2.z;
		int count = 0;
		DistrList info = new DistrList();
		for (int x = point1.x; goodX ? x <= point2.x : x >= point2.x;)
		{
			for (int y = point1.y; goodY ? y <= point2.y : y >= point2.y;)
			{
				for (int z = point1.z; goodZ ? z <= point2.z : z >= point2.z;)
				{
					count++;
					int id = sender.worldObj.getBlockId(x, y, z);
					int meta = sender.worldObj.getBlockMetadata(x, y, z);
					info.addBlock(id, meta);
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
		for (int i = 0; i < info.info.size(); i++)
		{
			String name = info.info.get(i).id == 0 ? "Air" : (String) new ItemStack(Block.blocksList[info.info.get(i).id], 1, info.info.get(i).meta).func_82833_r();
			sender.addChatMessage(name + ": " + (((info.info.get(i).amount + .0) / (count + .0)) * 100) + "%");
		}
		sender.addChatMessage("Distr Complete");
	}

	public void setBelowCommand(int radius, CommandInfo inf, EntityPlayer sender)
	{
		Point point1 = PlayerInfo.getPlayerPoint1(sender.username);
		Point point2 = PlayerInfo.getPlayerPoint1(sender.username);
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		CommandInfo air = new CommandInfo();
		air.setInfo(0, 0);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY - radius; y <= plrY; y++)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					if (placeBlock(x, y, z, inf, sender, back))
					{
						changed++;
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Greened Near(" + radius + ") " + changed + " Blocks");
	}

	public void setAboveCommand(int radius, CommandInfo inf, EntityPlayer sender)
	{
		Point point1 = PlayerInfo.getPlayerPoint1(sender.username);
		Point point2 = PlayerInfo.getPlayerPoint1(sender.username);
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		CommandInfo air = new CommandInfo();
		air.setInfo(0, 0);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY; y <= plrY + radius; y++)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					if (placeBlock(x, y, z, inf, sender, back))
					{
						changed++;
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Greened Near(" + radius + ") " + changed + " Blocks");
	}

	public void replaceBelowCommand(int radius, CommandInfo before, CommandInfo after, EntityPlayer sender)
	{
		Point point1 = PlayerInfo.getPlayerPoint1(sender.username);
		Point point2 = PlayerInfo.getPlayerPoint1(sender.username);
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		CommandInfo air = new CommandInfo();
		air.setInfo(0, 0);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY - radius; y <= plrY; y++)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					if (before.isGoodInfo(sender.worldObj.getBlockId(x, y, z), sender.worldObj.getBlockMetadata(x, y, z)))
					{
						if (placeBlock(x, y, z, after, sender, back))
						{
							changed++;
						}
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Greened Near(" + radius + ") " + changed + " Blocks");
	}

	public void replaceAboveCommand(int radius, CommandInfo before, CommandInfo after, EntityPlayer sender)
	{
		Point point1 = PlayerInfo.getPlayerPoint1(sender.username);
		Point point2 = PlayerInfo.getPlayerPoint1(sender.username);
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		CommandInfo air = new CommandInfo();
		air.setInfo(0, 0);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY; y <= plrY + radius; y++)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					if (before.isGoodInfo(sender.worldObj.getBlockId(x, y, z), sender.worldObj.getBlockMetadata(x, y, z)))
					{
						if (placeBlock(x, y, z, after, sender, back))
						{
							changed++;
						}
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Greened Near(" + radius + ") " + changed + " Blocks");
	}

	public void copyCommand(int id, EntityPlayer sender)
	{
		CopyArea copy = new CopyArea(sender, PlayerInfo.getPlayerInfo(sender.username).getSelection());
		// TODO: how do I do this....
		int oX = MathHelper.floor_double(sender.posX) - point1.x;
		int oY = MathHelper.floor_double(sender.posY) - point1.y;
		int oZ = MathHelper.floor_double(sender.posZ) - point1.z;
		copy.setOffset(oX, oY, oZ);
		cpy.put(PlayerInfo.getFromPool(id, sender.username), copy);
		sender.addChatMessage("Blocks Copied");
	}

	public void cutCommand(int id, EntityPlayer sender)
	{
		Point point1 = PlayerInfo.getPlayerPoint1(sender.username);
		Point point2 = PlayerInfo.getPlayerPoint2(sender.username);
		boolean goodX = point1.x <= point2.x;
		boolean goodY = point1.y <= point2.y;
		boolean goodZ = point1.z <= point2.z;
		CopyArea copy = new CopyArea(sender.username, id, worldEdits);
		BackupArea back = new BackupArea();
		for (int x = point1.x; goodX ? x <= point2.x : x >= point2.x;)
		{
			for (int y = point1.y; goodY ? y <= point2.y : y >= point2.y;)
			{
				for (int z = point1.z; goodZ ? z <= point2.z : z >= point2.z;)
				{
					copy.addBlock(x, y, z, sender.worldObj.getBlockId(x, y, z), sender.worldObj.getBlockMetadata(x, y, z));
					CommandInfo inf = new CommandInfo();
					inf.setInfo(0, 0);
					placeBlock(x, y, z, inf, sender, back);
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
		copy.start = point1;
		copy.end = point2;
		int oX = MathHelper.floor_double(sender.posX) - point1.x;
		int oY = MathHelper.floor_double(sender.posY) - point1.y;
		int oZ = MathHelper.floor_double(sender.posZ) - point1.z;
		copy.setOffset(oX, oY, oZ);
		cpy.put(PlayerInfo.getFromPool(id, sender.username), copy);

		addBackup(back);
		sender.addChatMessage("Blocks Cut");
	}

	public void cut2Move(int id, EntityPlayer sender, BackupArea back)
	{
		Point point1 = PlayerInfo.getPlayerPoint1(sender.username);
		Point point2 = PlayerInfo.getPlayerPoint2(sender.username);
		boolean goodX = point1.x <= point2.x;
		boolean goodY = point1.y <= point2.y;
		boolean goodZ = point1.z <= point2.z;
		CopyArea copy = new CopyArea(sender.username, id, worldEdits);
		for (int x = point1.x; goodX ? x <= point2.x : x >= point2.x;)
		{
			for (int y = point1.y; goodY ? y <= point2.y : y >= point2.y;)
			{
				for (int z = point1.z; goodZ ? z <= point2.z : z >= point2.z;)
				{
					int bid = sender.worldObj.getBlockId(x, y, z);
					int meta = sender.worldObj.getBlockMetadata(x, y, z);
					copy.addBlock(x, y, z, sender.worldObj.getBlockId(x, y, z), sender.worldObj.getBlockMetadata(x, y, z));
					CommandInfo inf = new CommandInfo();
					inf.setInfo(0, 0);
					back.addBlockBefore(x, y, z, bid, meta);
					sender.worldObj.setBlockWithNotify(x, y, z, 0);
					back.addBlockAfter(x, y, z, 0, 0);
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
		copy.start = point1;
		copy.end = point2;
		int oX = MathHelper.floor_double(sender.posX) - point1.x;
		int oY = MathHelper.floor_double(sender.posY) - point1.y;
		int oZ = MathHelper.floor_double(sender.posZ) - point1.z;
		copy.setOffset(oX, oY, oZ);
		cpy.put(PlayerInfo.getFromPool(id, sender.username), copy);

		addBackup(back);
	}

	public boolean isBlockExposedToAirAndAboveBlock(World worldObj, int x, int y, int z)
	{
		if (worldObj.getBlockId(x, y, z) != 0 && worldObj.getBlockId(x, y + 1, z) == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void overlayCommand(CommandInfo end, int radius, EntityPlayer sender)
	{
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY + radius; y >= plrY - radius; y--)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					// int bid=sender.worldObj.getBlockId(x, y, z);
					// int meta=sender.worldObj.getBlockMetadata(x, y, z);
					if (isBlockExposedToAirAndAboveBlock(sender.worldObj, x, y, z))
					{
						if (placeBlock(x, y + 1, z, end, sender, back))
						{
							changed++;
						}
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Overlayed Near(" + radius + ") " + changed + " Blocks to " + getIdString(end));
	}

	public void snowCommand(int radius, EntityPlayer sender)
	{
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		CommandInfo snow = new CommandInfo();
		snow.setInfo(78, 0);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY + radius; y >= plrY - radius; y--)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					// int bid=sender.worldObj.getBlockId(x, y, z);
					// int meta=sender.worldObj.getBlockMetadata(x, y, z);
					if (isBlockExposedToAirAndAboveBlock(sender.worldObj, x, y, z))
					{
						if (placeBlock(x, y + 1, z, snow, sender, back))
						{
							changed++;
						}
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Snowed Near(" + radius + ") " + changed + " Blocks");
	}

	public void unIceCommand(int radius, EntityPlayer sender)
	{
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		CommandInfo ice = new CommandInfo();
		CommandInfo water = new CommandInfo();
		ice.setInfo(79, 0);
		water.setInfo(8, 0);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY + radius; y >= plrY - radius; y--)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					int bid = sender.worldObj.getBlockId(x, y, z);
					int meta = sender.worldObj.getBlockMetadata(x, y, z);
					if (ice.isGoodInfo(bid, meta))
					{
						if (placeBlock(x, y, z, water, sender, back))
						{
							changed++;
						}
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("UnIced Near(" + radius + ") " + changed + " Blocks");
	}

	public void iceCommand(int radius, EntityPlayer sender)
	{
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		CommandInfo ice = new CommandInfo();
		CommandInfo water = new CommandInfo();
		ice.setInfo(79, 0);
		water.setInfo(8, 0);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY + radius; y >= plrY - radius; y--)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					int bid = sender.worldObj.getBlockId(x, y, z);
					int meta = sender.worldObj.getBlockMetadata(x, y, z);
					if (water.isGoodInfo(bid, meta))
					{
						if (placeBlock(x, y, z, ice, sender, back))
						{
							changed++;
						}
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Iced Near(" + radius + ") " + changed + " Blocks");
	}

	public void raiseCommand(CommandInfo end, int radius, EntityPlayer sender)
	{ // unimplemented
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY - radius; y <= plrY + radius; y++)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					// int bid=sender.worldObj.getBlockId(x, y, z);
					// int meta=sender.worldObj.getBlockMetadata(x, y, z);
					if (isBlockExposedToAirAndAboveBlock(sender.worldObj, x, y, z))
					{
						if (placeBlock(x, y + 1, z, end, sender, back))
						{
							changed++;
						}
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Overlayed Near(" + radius + ") " + changed + " Blocks to " + getIdString(end));
	}

	public void setTopCommand(CommandInfo end, int radius, EntityPlayer sender)
	{ // unimplemented
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY - radius; y <= plrY + radius; y++)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					// int bid=sender.worldObj.getBlockId(x, y, z);
					// int meta=sender.worldObj.getBlockMetadata(x, y, z);
					if (isBlockExposedToAirAndAboveBlock(sender.worldObj, x, y, z))
					{
						if (placeBlock(x, y, z, end, sender, back))
						{
							changed++;
						}
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Overlayed Near(" + radius + ") " + changed + " Blocks to " + getIdString(end));
	}

	public void moveCommand(int oX, int oY, int oZ, EntityPlayer sender)
	{
		BackupArea back = new BackupArea();
		cut2Move(5000, sender, back);
		Point point = PlayerInfo.getPlayerPoint1(sender.username);
		pasteAt(5000, sender, true, PlayerInfo.x + oX, PlayerInfo.y + oY, PlayerInfo.z + oZ, back);
		addBackup(back);
		sender.addChatMessage("Blocks Moved");
	}

	public void pasteAt(int id, EntityPlayer sender, boolean clear, int x, int y, int z, BackupArea back)
	{
		CopyArea area = cpy.get(PlayerInfo.getFromPool(id, sender.username));
		area.loadAreaMove(sender, back, clear, x, y, z);

	}

	public void stackCommand(int id, EntityPlayer sender, int times)
	{
		if (id > cpy.size())
		{
			sender.addChatMessage("Invalid Copy ID");
			return;
		}
		CopyArea area = cpy.get(PlayerInfo.getFromPool(id, sender.username));
		BackupArea back = new BackupArea();
		int x = MathHelper.floor_double(sender.posX);
		int y = MathHelper.floor_double(sender.posY);
		int z = MathHelper.floor_double(sender.posZ);
		int dir = getDirection(sender);

		int length = 0;
		if (dir == 1 || dir == 3)
		{
			length = area.getXLength();
		}
		else if (dir == 0 || dir == 2)
		{
			length = area.getZLength();
			// }else{
			// length = area.getHeight();
		}
		System.out.println(length);
		/*
		 * if(dir==0) { z+=length; }else if(dir==1) { x-=length; }else if(dir==2) { z-=length; }else if(dir==3) { x+=length; }else if(dir==4) { y-=length; }else if(dir==5) { y+=length; }
		 */
		for (int i = 0; i < times; i++)
		{
			area.loadAreaStack(sender, back, true, x, y, z);
			if (dir == 0)
			{
				z += length;
			}
			else if (dir == 1)
			{
				x -= length;
			}
			else if (dir == 2)
			{
				z -= length;
			}
			else if (dir == 3)
			{
				x += length;
			}
			else if (dir == 4)
			{
				y -= length;
			}
			else if (dir == 5)
			{
				y += length;
			}
		}

		addBackup(back);
		sender.addChatMessage("Blocks Loaded(" + times + ")");
	}

	public void pasteCommand(int id, EntityPlayer sender, boolean clear)
	{
		if (id > cpy.size())
		{
			sender.addChatMessage("Invalid Copy ID");
			return;
		}
		CopyArea area = cpy.get(PlayerInfo.getFromPool(id, sender.username));
		BackupArea back = new BackupArea();
		area.loadArea(sender, back, clear);

		addBackup(back);
		sender.addChatMessage("Blocks Loaded");
	}

	public void replaceCommand(CommandInfo begin, CommandInfo end, EntityPlayer sender)
	{
		Point point1 = PlayerInfo.getPlayerPoint1(sender.username);
		Point point2 = PlayerInfo.getPlayerPoint2(sender.username);
		boolean goodX = point1.x <= point2.x;
		boolean goodY = point1.y <= point2.y;
		boolean goodZ = point1.z <= point2.z;
		int changed = 0;
		BackupArea back = new BackupArea();
		for (int x = point1.x; goodX ? x <= point2.x : x >= point2.x;)
		{
			for (int y = point1.y; goodY ? y <= point2.y : y >= point2.y;)
			{
				for (int z = point1.z; goodZ ? z <= point2.z : z >= point2.z;)
				{
					int bid = sender.worldObj.getBlockId(x, y, z);
					int meta = sender.worldObj.getBlockMetadata(x, y, z);
					if (begin.isGoodInfo(bid, meta))
					{
						/*
						 * back.addBlockBefore(x, y, z, bid, meta); sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, changeBlockID, changeMetadata); back.addBlockAfter(x, y, z, changeBlockID, changeMetadata);
						 */
						if (placeBlock(x, y, z, end, sender, back))
						{
							changed++;
						}
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

		addBackup(back);
		sender.addChatMessage("Replaced " + changed + " Blocks from " + getIdString(begin) + " to " + getIdString(end));
	}

	public void replaceNearCommand(CommandInfo begin, CommandInfo end, int radius, EntityPlayer sender)
	{
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY - radius; y <= plrY + radius; y++)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					int bid = sender.worldObj.getBlockId(x, y, z);
					int meta = sender.worldObj.getBlockMetadata(x, y, z);
					if (begin.isGoodInfo(bid, meta))
					{
						if (placeBlock(x, y, z, end, sender, back))
						{
							changed++;
						}
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Replaced Near(" + radius + ") " + changed + " Blocks from " + getIdString(begin) + " to " + getIdString(end));
	}

	public void extinguishCommand(int radius, EntityPlayer sender)
	{
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY - radius; y <= plrY + radius; y++)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					int bid = sender.worldObj.getBlockId(x, y, z);
					if (bid == 51)
					{
						if (placeBlock(x, y, z, new CommandInfo(0, 0), sender, back))
						{
							changed++;
						}
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Extinguished Near(" + radius + ") " + changed + " Blocks");
	}

	public void greenCommand(int radius, EntityPlayer sender)
	{
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		CommandInfo dirt = new CommandInfo();
		CommandInfo grass = new CommandInfo();
		CommandInfo snow = new CommandInfo();
		CommandInfo air = new CommandInfo();
		dirt.setInfo(net.minecraft.src.Block.dirt.blockID, 0);
		grass.setInfo(net.minecraft.src.Block.grass.blockID, 0);
		air.setInfo(0, 0);
		snow.setInfo(78, 0);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY - radius; y <= plrY + radius; y++)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					int bid = sender.worldObj.getBlockId(x, y, z);
					int meta = sender.worldObj.getBlockMetadata(x, y, z);
					if (dirt.isGoodInfo(bid, meta))
					{
						if (placeBlock(x, y, z, grass, sender, back))
						{
							changed++;
						}
					}
					if (snow.isGoodInfo(bid, meta))
					{
						if (placeBlock(x, y, z, air, sender, back))
						{
							changed++;
						}
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Greened Near(" + radius + ") " + changed + " Blocks");
	}

	public void unSnowCommand(int radius, EntityPlayer sender)
	{
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		CommandInfo snow = new CommandInfo();
		CommandInfo air = new CommandInfo();
		air.setInfo(0, 0);
		snow.setInfo(78, 0);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY - radius; y <= plrY + radius; y++)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					int bid = sender.worldObj.getBlockId(x, y, z);
					int meta = sender.worldObj.getBlockMetadata(x, y, z);
					if (snow.isGoodInfo(bid, meta))
					{
						if (placeBlock(x, y, z, air, sender, back))
						{
							changed++;
						}
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Greened Near(" + radius + ") " + changed + " Blocks");
	}

	public void drainCommand(int radius, EntityPlayer sender)
	{ // Old Code
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		CommandInfo water = new CommandInfo(8, 0);
		water.setInfo(9, 0);
		water.setInfo(10, 0);
		water.setInfo(11, 0);
		water.setInfo(79, 0);
		CommandInfo air = new CommandInfo(0, 0);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY - radius; y <= plrY + radius; y++)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					int bid = sender.worldObj.getBlockId(x, y, z);
					if (water.isGoodInfo(bid))
					{
						if (placeBlock(x, y, z, air, sender, back))
						{
							changed++;
						}
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Drained Near(" + radius + ") " + changed + " Blocks");
	}

	public void ungreenCommand(int radius, EntityPlayer sender)
	{
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		CommandInfo dirt = new CommandInfo();
		CommandInfo grass = new CommandInfo();
		dirt.setInfo(net.minecraft.src.Block.dirt.blockID, 0);
		grass.setInfo(net.minecraft.src.Block.grass.blockID, 0);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY - radius; y <= plrY + radius; y++)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					int bid = sender.worldObj.getBlockId(x, y, z);
					int meta = sender.worldObj.getBlockMetadata(x, y, z);
					if (grass.isGoodInfo(bid, meta))
					{
						if (placeBlock(x, y, z, dirt, sender, back))
						{
							changed++;
						}
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Ungreened Near(" + radius + ") " + changed + " Blocks");
	}

	public void setNearCommand(CommandInfo inf, int radius, EntityPlayer sender)
	{
		int changed = 0;
		BackupArea back = new BackupArea();
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		for (int x = plrX - radius; x <= plrX + radius; x++)
		{
			for (int y = plrY - radius; y <= plrY + radius; y++)
			{
				for (int z = plrZ - radius; z <= plrZ + radius; z++)
				{
					int bid = sender.worldObj.getBlockId(x, y, z);
					int meta = sender.worldObj.getBlockMetadata(x, y, z);
					if (placeBlock(x, y, z, inf, sender, back))
					{
						changed++;
					}
				}
			}
		}

		addBackup(back);
		sender.addChatMessage("Set Near(" + radius + ") " + changed + " Blocks to " + getIdString(inf));
	}

	public MovingObjectPosition rayTrace(EntityLiving ep)
	{
		// return ep.rayTrace(300F, mod_worldcontrol.renderPartialTicks);
		float var4 = 1.0F;
		float var5 = ep.prevRotationPitch + (ep.rotationPitch - ep.prevRotationPitch) * var4;
		float var6 = ep.prevRotationYaw + (ep.rotationYaw - ep.prevRotationYaw) * var4;
		double var7 = ep.prevPosX + (ep.posX - ep.prevPosX) * (double) var4;
		double var9 = ep.prevPosY + (ep.posY - ep.prevPosY) * (double) var4 + 1.62D - (double) ep.yOffset;
		double var11 = ep.prevPosZ + (ep.posZ - ep.prevPosZ) * (double) var4;
		Vec3 var13 = ep.worldObj.func_82732_R().getVecFromPool(var7, var9, var11);
		float var14 = MathHelper.cos(-var6 * 0.017453292F - (float) Math.PI);
		float var15 = MathHelper.sin(-var6 * 0.017453292F - (float) Math.PI);
		float var16 = -MathHelper.cos(-var5 * 0.017453292F);
		float var17 = MathHelper.sin(-var5 * 0.017453292F);
		float var18 = var15 * var16;
		float var20 = var14 * var16;
		double var21 = 5.0D;
		if (ep instanceof EntityPlayerMP)
		{
			var21 = ((EntityPlayerMP) ep).theItemInWorldManager.getBlockReachDistance();
		}
		Vec3 var23 = var13.addVector((double) var18 * var21, (double) var17 * var21, (double) var20 * var21);
		return ep.worldObj.rayTraceBlocks_do_do(var13, var23, true, false);
	}

	public int getDirection(EntityPlayer ep)
	{
		int direction = MathHelper.floor_double((double) ((ep.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		return direction;
	}

	public void treeCommand(String type, EntityPlayer sender)
	{
		MovingObjectPosition mop = rayTrace(Minecraft.getMinecraft().renderViewEntity);
		int x = mop.blockX;
		int y = mop.blockY;
		int z = mop.blockZ;
		if (type.equals("oak"))
		{
			trees = new WorldGenTrees(false, 5, 0, 0, false);
			trees.generate(sender.worldObj, rand, x, y, z);
		}
		else if (type.equals("swamp"))
		{
			trees = new WorldGenTrees(false, 5, 0, 0, true);
			trees.generate(sender.worldObj, rand, x, y, z);
		}
		else if (type.equals("birch"))
		{
			trees = new WorldGenTrees(false, 5, 2, 2, false);
			trees.generate(sender.worldObj, rand, x, y, z);
		}
		else if (type.equals("smalljungle"))
		{
			trees = new WorldGenTrees(false, 4 + rand.nextInt(7), 3, 3, true);
			trees.generate(sender.worldObj, rand, x, y, z);
		}
		else if (type.equals("bigjungle"))
		{
			trees = new WorldGenHugeTrees(false, 10 + rand.nextInt(20), 3, 3);
			trees.generate(sender.worldObj, rand, x, y, z);
		}
		else if (type.equals("shrubjungle"))
		{
			trees = new WorldGenShrub(3, 0);
			trees.generate(sender.worldObj, rand, x, y, z);
		}
		else if (type.equals("taiga1"))
		{
			trees = new WorldGenTaiga1();
			trees.generate(sender.worldObj, rand, x, y, z);
		}
		else if (type.equals("taiga2"))
		{
			trees = new WorldGenTaiga2(false);
			trees.generate(sender.worldObj, rand, x, y, z);
		}
		else
		{
			sender.addChatMessage("Invalid Tree Type Types: oak, swamp, birch, smalljungle, bigjungle, shrubjungle, taiga1, taiga2");
		}
	}

}
