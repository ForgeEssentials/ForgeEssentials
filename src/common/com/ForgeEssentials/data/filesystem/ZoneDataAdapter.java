package com.ForgeEssentials.data.filesystem;

import java.io.File;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.permission.Zone;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

public class ZoneDataAdapter extends FileSystemDataAdapter<Zone, String>
{
	private String dataDir;
	
	public ZoneDataAdapter()
	{
		this.dataDir = ((FileSystemDataDriver)DataDriver.getInstance()).getBaseBath() + "Permissions/Zones/";
		
		File f = new File(this.dataDir);
		
		// Ensure the directory exists before we start trying to save things.
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
	public boolean saveData(Zone object)
	{
		boolean flag = true;
		String  filename = this.dataDir + object.getZoneID();
		File f = new File(filename);
		
		// Remove the old save
		if (f.exists())
		{
			f.delete();
		}
		
		Configuration config = new Configuration(f, false);
				
		// priority
		config.get("base", "priority", object.priority);
		// name
		config.get("base", "zoneID", object.getZoneID());
		// parent
		config.get("base", "parentID", object.parent);
		// world name
		config.get("base", "world", object.getWorldString());
		// area low point
		Point p = object.getLowPoint();
		config.get("p1", "x", p.x);
		config.get("p1", "y", p.y);
		config.get("p1", "z", p.z);
		// area high point
		p = object.getHighPoint();
		config.get("p2", "x", p.x);
		config.get("p2", "y", p.y);
		config.get("p2", "z", p.z);
		// all children names
		String[] children = object.getChildren();
		config.get("base", "children", children);
		
		config.save();
		
		return flag;
	}

	@Override
	public boolean loadData(String zoneID, Zone object)
	{
		boolean flag = true;
		File f = new File(this.dataDir + zoneID);
		
		if (f.exists())
		{
			Configuration c = new Configuration(f, false);
			
			// Pull all the data out from the configs 
			int priority = c.get("base", "priority", 0).getInt();
			String zoneName = c.get("base", "zoneID", " ").value;
			String parent = c.get("base", "parentID", " ").value;
			String world = c.get("base", "world", " ").value;
			String[] children = c.get("base", "children", (String[])null).valueList;
			int x, y, z = 0;
			x = c.get("p1", "x", 0).getInt();
			y = c.get("p1", "y", 0).getInt();
			z = c.get("p1", "z", 0).getInt();
			Point low = new Point(x, y, z);
			x = c.get("p2", "x", 0).getInt();
			y = c.get("p2", "y", 0).getInt();
			z = c.get("p2", "z", 0).getInt();
			Point high = new Point(x, y, z);
			Zone.load(zoneID, parent, world, priority, new Selection(low, high), children);
		}
		else
		{
			flag = false;
		}
		
		return flag;
	}

	@Override
	public boolean deleteData(String zoneID)
	{
		boolean flag = true;
		File f = new File(this.dataDir + zoneID);
		
		if (f.exists())
		{
			f.delete();
		}
		
		return flag;
	}

}
