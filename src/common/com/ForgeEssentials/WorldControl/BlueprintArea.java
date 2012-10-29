package com.ForgeEssentials.WorldControl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.commands.CommandInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;

/**
 * @author UnknownCoder : Max Bruce Handles the saving and loading of blueprints (schematics?)
 */

public class BlueprintArea extends AreaBase
{
	private List<BlueprintBlock> area = new ArrayList<BlueprintBlock>();

	public BlueprintArea(String user, int worldEdit)
	{
		username = user;
		this.worldEdit = worldEdit;
	}

	private BlueprintArea(Object[] obj)
	{
		this.area = (List<BlueprintBlock>) obj[0];
		this.username = (String) obj[1];
		this.worldEdit = (Integer) obj[2];
		this.start = new Point((Integer) obj[3], (Integer) obj[4], (Integer) obj[5]);
		this.end = new Point((Integer) obj[6], (Integer) obj[7], (Integer) obj[8]);
	}

	public void addBlock(int x, int y, int z, int blockID, int metadata)
	{
		area.add(new BlueprintBlock(x, y, z, blockID, metadata));
	}

	public void save(String path)
	{
		try
		{
			File dir = new File(Minecraft.getMinecraftDir().toString() + "/blueprints/");
			if (!dir.exists())
				dir.mkdir();
			File file = new File(Minecraft.getMinecraftDir().toString() + "/blueprints/" + path + ".blp");
			// if(file.canWrite()==false)return;
			OutputStream output = new FileOutputStream(file);
			ObjectOutputStream output2 = new ObjectOutputStream(output);
			output2.writeObject(new Object[] { area, username, worldEdit, start.x, start.y, start.z, end.z, end.y, end.z });
			output2.close();
			output.close();
			return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
	}

	public static BlueprintArea load(String path)
	{
		try
		{
			File dir = new File(Minecraft.getMinecraftDir().toString() + "/ForgeEssentials/blueprints/");
			if (!dir.exists())
				dir.mkdir();
			File file = new File(Minecraft.getMinecraftDir().toString() + "/ForgeEssentials/blueprints/" + path + ".blp");
			// if(file.canRead()==false)return null;
			InputStream input = new FileInputStream(file);
			ObjectInputStream input2 = new ObjectInputStream(input);
			Object[] obj = (Object[]) input2.readObject();
			input2.close();
			input.close();
			return new BlueprintArea(obj);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public void loadArea(EntityPlayer sender, BackupArea back, boolean clear)
	{
		if (clear)
		{
			CommandInfo inf = (new CommandInfo());
			inf.setInfo(0, 0);
			FunctionHandler.instance.cpyclearCommand(inf, sender);
		}
		for (int i = 0; i < area.size(); i++)
		{
			BlueprintBlock obj = area.get(i);
			int bid = sender.worldObj.getBlockId(obj.x, obj.y, obj.z);
			int meta = sender.worldObj.getBlockMetadata(obj.x, obj.y, obj.z);
			back.addBlockBefore(obj.x, obj.y, obj.z, bid, meta);
			sender.worldObj.setBlockAndMetadataWithNotify(obj.x, obj.y, obj.z, obj.blockID, obj.metadata);
			back.addBlockAfter(obj.x, obj.y, obj.z, obj.blockID, obj.metadata);
		}
	}

	public void loadAreaRelative(EntityPlayer sender, BackupArea back, boolean clear)
	{
		if (clear)
		{
			CommandInfo inf = (new CommandInfo());
			inf.setInfo(0, 0);
			FunctionHandler.instance.cpyclearCommand(inf, sender);
		}
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		for (int i = 0; i < area.size(); i++)
		{
			BlueprintBlock obj = area.get(i);
			int offX = Math.abs(obj.x - start.x);
			int offY = Math.abs(obj.y - start.y);
			int offZ = Math.abs(obj.z - start.z);
			int x = plrX + offX;
			int y = plrY + offY;
			int z = plrZ + offZ;
			int bid = sender.worldObj.getBlockId(x, y, z);
			int meta = sender.worldObj.getBlockMetadata(x, y, z);
			back.addBlockBefore(x, y, z, bid, meta);
			sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, obj.blockID, obj.metadata);
			back.addBlockAfter(x, y, z, obj.blockID, obj.metadata);
		}
	}

	public void loadAreaAt(EntityPlayer sender, BackupArea back, int tX, int tY, int tZ)
	{
		CommandInfo inf = (new CommandInfo());
		inf.setInfo(0, 0);
		FunctionHandler.instance.cpyclearCommand(inf, sender);
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		for (int i = 0; i < area.size(); i++)
		{
			BlueprintBlock obj = area.get(i);
			int offX = Math.abs(obj.x - start.x);
			int offY = Math.abs(obj.y - start.y);
			int offZ = Math.abs(obj.z - start.z);
			int x = tX + offX;
			int y = tY + offY;
			int z = tZ + offZ;
			int bid = sender.worldObj.getBlockId(x, y, z);
			int meta = sender.worldObj.getBlockMetadata(x, y, z);
			back.addBlockBefore(x, y, z, bid, meta);
			sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, obj.blockID, obj.metadata);
			back.addBlockAfter(x, y, z, obj.blockID, obj.metadata);
		}
	}
}
