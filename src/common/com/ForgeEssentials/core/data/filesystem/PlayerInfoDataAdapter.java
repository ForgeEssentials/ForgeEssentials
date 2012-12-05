package com.ForgeEssentials.core.data.filesystem;

import java.io.File;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.data.DataAdapter;
import com.ForgeEssentials.core.data.DataDriver;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

/**
 * This DataAdapter is responsible for saving PlayerInfo objects to a flat-file data backing.
 * 
 * @author MysteriousAges
 *
 */
public class PlayerInfoDataAdapter extends FileSystemDataAdapter<PlayerInfo, String>
{
	private static String dataDir;
	
	public PlayerInfoDataAdapter()
	{
		this.dataDir = ((FileSystemDataDriver)DataDriver.getInstance()).baseFilePath + "PlayerInfo/";
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
		
		// buffer to hold point data
		int[] pnt;
		
		if (object.getPoint1() != null)
		{
			pnt = pointToIntArray(object.getPoint1());
		
			data.get("selection", "x1", pnt[0]);
			data.get("selection", "y1", pnt[1]);
			data.get("selection", "z1", pnt[2]);
		}
		else
		{
			data.get("selection", "1", false);
		}
		if (object.getPoint2() != null)
		{
			pnt = pointToIntArray(object.getPoint2());
			
			data.get("selection", "x2", pnt[0]);
			data.get("selection", "y2", pnt[1]);
			data.get("selection", "z2", pnt[2]);
		}
		else
		{
			data.get("selection", "2", false);
		}
		if (object.home != null)
		{
			pnt = worldPointToIntArray(object.home);		
			
			data.get("home", "x", pnt[0]);
			data.get("home", "y", pnt[1]);
			data.get("home", "z", pnt[2]);
			data.get("home", "dim", pnt[3]);
		}
		else
		{
			data.get("home", "none", true);
		}
		if (object.lastDeath != null)
		{
			pnt = worldPointToIntArray(object.lastDeath);
			
			data.get("death", "x", pnt[0]);
			data.get("death", "y", pnt[1]);
			data.get("death", "z", pnt[2]);
			data.get("death", "dim", pnt[3]);
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
