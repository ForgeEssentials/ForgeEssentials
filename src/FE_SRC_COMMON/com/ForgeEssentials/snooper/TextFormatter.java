package com.ForgeEssentials.snooper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
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
	public static String mapToJSON(HashMap<String, String> data)
	{
		if(data.isEmpty())
		{
			return "{}";
		}
		String toSend = "{";
		
        for(Entry<String, String> set : data.entrySet())
    	{
        	
        	if(set.getValue().startsWith("{"))
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
	
	public static String arrayToJSON(String[] data)
	{
		if(data.length == 0)
		{
			return "[]";
		}
		String toSend = "[";
		
        for(String value : data)
    	{
        	if(value.startsWith("{"))
        	{
        		toSend += value + ", ";
        	}
        	else
        	{
        		toSend += "'" + value + "', ";
        	}
    	}
        
        toSend = toSend.substring(0, toSend.length() - 2);
        toSend += "]";
		
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
	
	public static String itemStackToJSON(ItemStack stack, boolean listEnch)
	{
		HashMap<String, String> data = new HashMap();
		if(stack.stackTagCompound.getCompoundTag("display").hasKey("Name")) data.put("name", "" + stack.getItemName().replaceAll("item.", "").replaceAll("tile.", "")); 
		data.put("id", "" + stack.itemID);
		data.put("amount", "" + stack.stackSize);
		data.put("damage", "" + stack.getItemDamage());
		
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
                	if(listEnch) tempArgs.add(Enchantment.enchantmentsList[var8].getTranslatedName(var9));
                	else tempArgs.add("[" + var8 + ", " + var9 + "]"); 
                }
            }
            data.put("ench", listToJSON(tempArgs));
        }
		
		return mapToJSON(data);
	}
	
	public static String potionsToJSON(Collection collection)
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
		return arrayToJSON(data);
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

	public static String listToJSON(ArrayList<String> data) 
	{
		if(data.size() == 0)
		{
			return "[]";
		}
		String toSend = "[";
		
        for(String value : data)
    	{
        	if(value.startsWith("{"))
        	{
        		toSend += value + ", ";
        	}
        	else
        	{
        		toSend += "'" + value + "', ";
        	}
    	}
        
        toSend = toSend.substring(0, toSend.length() - 2);
        toSend += "]";
		
		return toSend;
	}
}
