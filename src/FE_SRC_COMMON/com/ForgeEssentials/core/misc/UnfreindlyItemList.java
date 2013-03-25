package com.ForgeEssentials.core.misc;

import java.util.HashMap;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.ForgeEssentials.util.OutputHandler;
import com.google.common.collect.HashBiMap;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;

public abstract class UnfreindlyItemList
{
	private static HashBiMap<String, Integer>	map	= HashBiMap.create();

	private UnfreindlyItemList()
	{
	}

	public static void vanillaStep()
	{
		HashMap<String, Integer> duplicates = new HashMap();
		
		String name = "";
		Integer num;
		for (int i = 0; i < Item.itemsList.length; i++)
		{
			Item item = Item.itemsList[i];
			if (item == null)
				continue;

			name = item.getItemName();
			if (name == null)
				continue;

			name = name.replace("item.", "").replace("tile.", "");

			name = "vanilla." + name;
			
			num = duplicates.get(name);
			if (num == null)
			{
				duplicates.put(name, 0);
			}
			else
			{
				num++;
				duplicates.put(name, num);
				name+=num;
			}
			
			map.put(name, item.itemID);
			OutputHandler.debug("VANILLA-ITEM REGISTERRED>> "+name+" : "+item.itemID);
		}
	}

	/**
	 * should be called at PostLoad.
	 */
	public static void modStep()
	{
		NBTTagList list = new NBTTagList();
		GameData.writeItemData(list);
		ItemData data;
		String name;
		for (int i = 0; i < list.tagCount(); i++)
		{
			data = new ItemData((NBTTagCompound) list.tagAt(i));
			name = data.getItemType();
			
			if (name == null || data.getModId().equalsIgnoreCase("Minecraft"))
				continue;
			
			if (name.contains("."))
				name = name.substring(name.lastIndexOf('.')+1, name.length());
			
			name = data.getModId()+"." + name;
			OutputHandler.debug("MOD-ITEM REGISTERRED>> "+name+" : "+data.getItemId());
			map.forcePut(name, data.getItemId());
		}
	}

	/**
	 * @param name name of the block.
	 * @return -1 if the name does not exist.
	 */
	public static int getId(String name)
	{
		Integer id = map.get(name);
		return id == null ? -1 : id;
	}

	/**
	 * @Param ID
	 * @return null if the ID does not exist
	 */
	public static String getName(int id)
	{
		return map.inverse().get(id);
	}

	public static Set<String> getNameSet()
	{
		return map.keySet();
	}

}
