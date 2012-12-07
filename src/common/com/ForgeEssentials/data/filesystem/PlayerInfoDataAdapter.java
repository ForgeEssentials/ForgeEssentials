package com.ForgeEssentials.data.filesystem;

import java.io.File;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.data.IDataAdapter;
import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

/**
 * This IDataAdapter is responsible for saving PlayerInfo objects to a flat-file data backing.
 * 
 * @author MysteriousAges
 *
 */
public class PlayerInfoDataAdapter extends FileSystemDataAdapter<PlayerInfo, String>
{
	private String dataDir;
	
	public PlayerInfoDataAdapter()
	{
		this.dataDir = ((FileSystemDataDriver)DataDriver.getInstance()).getBaseBath() + "PlayerInfo/";
		File f = new File(dataDir);
		
		// Ensure the PlayerInfo directory exists.
		if (!f.exists())
		{
			try
			{
				// Attempt to create this and any parent folders.
				f.mkdirs();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean saveData(PlayerInfo object)
	{
		boolean flag = true;
		String file = this.dataDir + object.getUsername().toLowerCase();
		Configuration data = new Configuration(new File(file), false);
		
		data.get("wand", "id", object.wandID);
		data.get("wand", "meta", object.wandDmg);
		data.get("wand", "enabled", object.wandEnabled);
		Point p;
		WorldPoint w;
		
		if ((p = object.getPoint1()) != null)
		{
			data.get("selection", "x1", p.x);
			data.get("selection", "y1", p.y);
			data.get("selection", "z1", p.z);
		}
		else
		{
			data.get("selection", "1", false);
		}
		if ((p = object.getPoint2()) != null)
		{
			data.get("selection", "x2", p.x);
			data.get("selection", "y2", p.y);
			data.get("selection", "z2", p.z);
		}
		else
		{
			data.get("selection", "2", false);
		}
		if ((w = object.home) != null)
		{
			data.get("home", "x", w.x);
			data.get("home", "y", w.y);
			data.get("home", "z", w.z);
			data.get("home", "dim", w.dim);
		}
		else
		{
			data.get("home", "none", true);
		}
		if ((w = object.lastDeath) != null)
		{
			data.get("death", "x", w.x);
			data.get("death", "y", w.y);
			data.get("death", "z", w.z);
			data.get("death", "dim", w.dim);
		}
		else
		{
			data.get("death", "none", true);
		}
		data.get("spawn", "type", object.spawnType);
		
		data.save();
		
		return flag;
	}

	@Override
	public boolean loadData(String username, PlayerInfo object)
	{
		boolean flag = true;
		File file = new File(this.dataDir + username.toLowerCase());
		
		if (file.exists())
		{
			Configuration data = new Configuration(file, false);
			
			data.load();
			
			object.wandID = data.get("wand", "id", 0).getInt();
			object.wandDmg = data.get("wand", "meta", 0).getInt();
			object.wandEnabled = data.get("wand", "enabled", false).getBoolean(false);
			
			// buffer to hold point data
			int[] pnt = new int[4];
			
			if (!data.hasKey("selection", "1"))
			{
				pnt[0] = data.get("selection", "x1", 0).getInt();
				pnt[1] = data.get("selection", "y1", 0).getInt();
				pnt[2] = data.get("selection", "z1", 0).getInt();
				object.setPoint1(new Point(pnt[0], pnt[1], pnt[2]));
			}

			if (!data.hasKey("selection", "2"))
			{
				pnt[0] = data.get("selection", "x2", 0).getInt();
				pnt[1] = data.get("selection", "y2", 0).getInt();
				pnt[2] = data.get("selection", "z2", 0).getInt();
				object.setPoint2(new Point(pnt[0], pnt[1], pnt[2]));
			}

			if (!data.hasKey("home", "none"))
			{				
				pnt[0] = data.get("home", "x", 0).getInt();
				pnt[1] = data.get("home", "y", pnt[1]).getInt();
				pnt[2] = data.get("home", "z", pnt[2]).getInt();
				pnt[3] = data.get("home", "dim", pnt[3]).getInt();
				object.home = new WorldPoint(pnt[0], pnt[1], pnt[2], pnt[3]);
			}
			if (!data.hasKey("death", "none"))
			{			
				pnt[0] = data.get("death", "x", pnt[0]).getInt();
				pnt[1] = data.get("death", "y", pnt[1]).getInt();
				pnt[2] = data.get("death", "z", pnt[2]).getInt();
				pnt[3] = data.get("death", "dim", pnt[3]).getInt();
				object.lastDeath = new WorldPoint(pnt[0], pnt[1], pnt[2], pnt[3]);
			}
			object.spawnType = data.get("spawn", "type", 0).getInt();
		}
		else
		{
			flag = false;
		}
		
		return flag;
	}

	@Override
	public boolean deleteData(String uniqueObjectKey)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
