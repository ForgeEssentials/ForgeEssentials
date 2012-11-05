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
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

/**
 * @author UnknownCoder : Max Bruce Handles the saving and loading of blueprints (schematics?)
 */
// TODO: FIX!!!
public class BlueprintArea extends AreaBase
{
	private ArrayList<BlueprintBlock> area = new ArrayList<BlueprintBlock>();
	String username;

	public BlueprintArea(String user)
	{
		super(null, null);
		username = user;
	}

	private BlueprintArea(Object[] obj)
	{
		super(new Point((Integer) obj[2], (Integer) obj[3], (Integer) obj[4]), new Point((Integer) obj[5], (Integer) obj[6], (Integer) obj[7]));
		this.area = (ArrayList<BlueprintBlock>) obj[0];
		this.username = (String) obj[1];
		this.start = new Point((Integer) obj[2], (Integer) obj[3], (Integer) obj[4]);
		this.end = new Point((Integer) obj[5], (Integer) obj[6], (Integer) obj[7]);
	}

	public void addBlock(int x, int y, int z, int blockID, int metadata, TileEntity te)
	{
		area.add(new BlueprintBlock(x, y, z, blockID, metadata, te));
	}

	public void save(String path)
	{
		try
		{
			File file = new File(WorldControlMain.blueprintDir, path + ".blp");
			// if(file.canWrite()==false)return;
			OutputStream output = new FileOutputStream(file);
			ObjectOutputStream output2 = new ObjectOutputStream(output);
			output2.writeObject(new Object[] { area, username, start.x, start.y, start.z, end.z, end.y, end.z });
			output2.close();
			output.close();
			return;
		} catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
	}

	public static BlueprintArea load(String path)
	{
		try
		{
			File dir = new File(Minecraft.getMinecraftDir().toString() + "/blueprints/");
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
		} catch (Exception e)
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
			back.addBlockBefore(new BlueprintBlock(obj.x, obj.y, obj.z, sender.worldObj.getBlockId(obj.x, obj.y, obj.z), sender.worldObj.getBlockMetadata(obj.x, obj.y, obj.z), sender.worldObj.getBlockTileEntity(obj.x, obj.y, obj.z)));
			sender.worldObj.setBlockAndMetadataWithNotify(obj.x, obj.y, obj.z, obj.blockID, obj.metadata);
			back.addBlockAfter(new BlueprintBlock(obj.x, obj.y, obj.z, obj.blockID, obj.metadata, obj.tileEntity));
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
			back.addBlockBefore(new BlueprintBlock(x, y, z, sender.worldObj.getBlockId(x, y, z), sender.worldObj.getBlockMetadata(x, y, z), sender.worldObj.getBlockTileEntity(x, y, z)));
			sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, obj.blockID, obj.metadata);
			back.addBlockAfter(new BlueprintBlock(x, y, z, obj.blockID, obj.metadata, obj.tileEntity));
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
			back.addBlockBefore(new BlueprintBlock(x, y, z, sender.worldObj.getBlockId(x, y, z), sender.worldObj.getBlockMetadata(x, y, z), sender.worldObj.getBlockTileEntity(x, y, z)));
			sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, obj.blockID, obj.metadata);
			back.addBlockAfter(new BlueprintBlock(x, y, z, obj.blockID, obj.metadata, obj.tileEntity));
		}
	}
}
