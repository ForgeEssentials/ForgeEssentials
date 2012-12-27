package com.ForgeEssentials.snooper;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;

import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class TextFormatter 
{
	public static String mapToJSON(HashMap<String, String> data)
	{
		if(data.isEmpty())
		{
			return "{}";
		}
		String toSend = "{";
		
        for(Entry<String, String> set : data.entrySet())
    	{
        	
        	if(set.getValue().contains("{"))
        	{
        		toSend += "\"" + set.getKey() + "\": " + set.getValue() + ", ";
        	}
        	else
        	{
        		toSend += "\"" + set.getKey() + "\": \"" + set.getValue() + "\", ";
        	}
    	}
        
        toSend = toSend.substring(0, toSend.length() - 2);
        toSend += "}";
		
		return toSend;
	}
	
	public static String pointToJSON(Point point)
	{
		HashMap<String, String> data = new HashMap();
		data.put("x", "" + point.x);
		data.put("y", "" + point.y);
		data.put("z", "" + point.z);
		if(point instanceof WorldPoint)
		{
			data.put("dim", "" + ((WorldPoint)point).dim);
		}
		if(point instanceof WarpPoint)
		{
			data.put("dim", "" + ((WarpPoint)point).dim);
			data.put("pitch", "" + ((WarpPoint)point).pitch);
			data.put("yaw", "" + ((WarpPoint)point).yaw);
		}
		return mapToJSON(data);
	}
	
	public static String itemStackToJSON(ItemStack stack)
	{
		HashMap<String, String> data = new HashMap();
		
		data.put("itemname", stack.getItemName());
		data.put("id", "" + stack.itemID);
		data.put("stacksize", "" + stack.stackSize);
		data.put("damage", "" + stack.getItemDamage());
		
		return mapToJSON(data);
	}
}
