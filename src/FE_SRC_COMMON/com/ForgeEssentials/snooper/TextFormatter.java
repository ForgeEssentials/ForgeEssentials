package com.ForgeEssentials.snooper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;

import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class TextFormatter 
{
	public static String toJSON(HashMap<String, String> data)
	{
		if(data.isEmpty())
		{
			return "{}";
		}
		String toSend = "{";
		
        for(Entry<String, String> set : data.entrySet())
    	{
        	if(set.getValue().startsWith("{") || set.getValue().startsWith("["))
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
	
	public static String toJSON(String[] data)
	{
		if(data.length == 0)
		{
			return "[\"\"]";
		}
		String toSend = "[";
		
        for(String value : data)
    	{
        	if(value.startsWith("{") || value.startsWith("["))
        	{
        		toSend += value + ",";
        	}
        	else
        	{
        		toSend += "\"" + value + "\",";
        	}
    	}
        
        toSend = toSend.substring(0, toSend.length() - 1);
        toSend += "]";
		
		return toSend;
	}
	
	public static String toJSON(Point point)
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
		return toJSON(data);
	}
	
	public static String toJSON(ItemStack stack, Boolean listEnch)
	{
		HashMap<String, String> data = new HashMap();
		if(stack.stackTagCompound != null && stack.stackTagCompound.hasKey("display") && stack.stackTagCompound.getCompoundTag("display").hasKey("Name")) data.put("item", stack.getItemName().replaceAll("item.", "").replaceAll("tile.", "")); 
		if(stack.stackSize != 1) data.put("amount", "" + stack.stackSize);
		data.put("id", "" + stack.itemID);
		if(stack.getItemDamage() != 0) data.put("dam", "" + stack.getItemDamage());
		data.put("name", stack.getDisplayName());
		
		if(listEnch)
		{
			ArrayList<String> tempArgs = new ArrayList();
			NBTTagList var10 = stack.getEnchantmentTagList();
			if (var10 != null)
			{ 
				for (int var7 = 0; var7 < var10.tagCount(); ++var7)
				{
					short var8 = ((NBTTagCompound)var10.tagAt(var7)).getShort("id");
					short var9 = ((NBTTagCompound)var10.tagAt(var7)).getShort("lvl");
					
					if (Enchantment.enchantmentsList[var8] != null)
					{
						tempArgs.add(Enchantment.enchantmentsList[var8].getTranslatedName(var9));
					}
				}
				data.put("ench", toJSON(tempArgs));
			}
		}
		
		return toJSON(data);
	}
	
	public static String toJSON(Collection collection)
	{
		String[] data = new String[collection.size()];
		Iterator i = collection.iterator();
		int id = 0;
		while(i.hasNext())
		{
			Object obj = i.next();
			PotionEffect effect = ((PotionEffect) obj);
			data[id] = translatePotion(effect);
			id ++;
		}
		return toJSON(data);
	}
	
    public static String translatePotion(PotionEffect effect)
    {
        Potion potion = Potion.potionTypes[effect.getPotionID()];
    	String name = StatCollector.translateToLocal(potion.getName());

        if (effect.getAmplifier() == 1) name = name + " II";
        else if (effect.getAmplifier() == 2) name = name + " III";
        else if (effect.getAmplifier() == 3) name = name + " IV";
        return name;
    }

	public static String toJSON(List<String> data) 
	{
		if(data.size() == 0)
		{
			return "[]";
		}
		String toSend = "[";
		
		for(String value : data)
		{
			if(value.startsWith("{") || value.startsWith("["))
			{
				toSend += value + ",";
			}
			else
			{
				toSend += "\"" + value + "\",";
			}
		}
        
        toSend = toSend.substring(0, toSend.length() - 1);
        toSend += "]";
		
		return toSend;
	}
}
