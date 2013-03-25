package com.ForgeEssentials.protection;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.collect.HashBiMap;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;

public abstract class ItemList
{
	private static HashBiMap<String, Integer>	map	= HashBiMap.create();

	private ItemList()
	{
	}

	protected static void vanillaStep()
	{
		String name;
		for (int i = 0; i < Item.itemsList.length; i++)
		{
			Item item = Item.itemsList[i];
			if (item == null)
				continue;

			name = item.getItemName().replace("item.", "");
			name = "vanilla." + name;
			map.put(name, item.itemID);
		}
	}

	/**
	 * should be called at PostLoad.
	 */
	protected static void modStep()
	{
		NBTTagList list = new NBTTagList();
		GameData.writeItemData(list);
		ItemData data;
		String name;
		for (int i = 0; i < list.tagCount(); i++)
		{
			data = new ItemData((NBTTagCompound) list.tagAt(i));
			name = data.getItemType();
			name = name.substring(name.lastIndexOf('.'), name.length());
			name = data.getModId() + "." + name;
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

}
