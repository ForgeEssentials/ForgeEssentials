package com.ForgeEssentials.core.misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.ForgeEssentials.util.FunctionHelper;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;

public abstract class UnfreindlyItemList
{
	private static HashBiMap<String, Integer>	map	= HashBiMap.create();

	private UnfreindlyItemList()
	{
	}

	/**
	 * should be called at PostLoad.
	 */
	public static void modStep()
	{
		HashMap<Integer, String> gameMap = new HashMap<Integer, String>();
		map.clear();

		// populate from GameData
		{
			NBTTagList list = new NBTTagList();
			GameData.writeItemData(list);
			ItemData data;
			String modid;

			for (int i = 0; i < list.tagCount(); i++)
			{
				data = new ItemData((NBTTagCompound) list.tagAt(i));
				modid = "vanilla";

				if (!data.getModId().equalsIgnoreCase("Minecraft"))
				{
					modid = data.getModId();
				}

				gameMap.put(data.getItemId(), modid);
			}
		}

		// now iterrate through ItemList.
		HashMap<String, Integer> duplicates = new HashMap<String, Integer>();

		String name;
		Integer num;
		String tempName;
		for (int i = 0; i < Item.itemsList.length; i++)
		{
			Item item = Item.itemsList[i];

			if (item == null)
			{
				continue;
			}
			
			// get the name..
			name = item.getUnlocalizedName();
			if (name == null)
			{
				if (item instanceof ItemBlock)
					name = "block.";
				else
					name = "item.";

				name = name + item.getClass().getSimpleName();
			}

			// split items and blocks
			name = name.replace("tile.", "block.");

			// get source.
			tempName = gameMap.get(item.itemID);
			if (Strings.isNullOrEmpty(tempName))
			{
				name = "unknownSource." + name;
			}
			else
			{
				name = tempName + "." + name;
			}

			// add numbers to the end of duplicates
			num = duplicates.get(name);
			if (num == null)
			{
				duplicates.put(name, 0);
			}
			else
			{
				num++;
				duplicates.put(name, num);
				name += num;
			}
			
			name = name.replace(' ', '_');

			// save
			map.put(name, item.itemID);
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

	public static void output(File output)
	{
		try
		{
			output.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));

			writer.write("#// ------------ Item-ID-List ------------ \\\\#");
			writer.newLine();
			writer.write("#// --------------- " + FunctionHelper.getCurrentDateString() + " --------------- \\\\#");
			writer.newLine();
			writer.write("#// ------------------------------------------ \\\\#");
			writer.newLine();
			writer.newLine();

			TreeSet<Integer> ids = new TreeSet<Integer>();
			BiMap<Integer, String> inverse = map.inverse();

			// order ids.
			for (Integer id : map.inverse().keySet())
			{
				ids.add(id);
			}

			String str;
			for (Integer id : ids)
			{
				str = String.format("%-7s", id);
				str = str + "  ==  " + inverse.get(id);
				writer.write(str);
				writer.newLine();
			}

			writer.close();
		}
		catch (Exception e)
		{

		}
	}

}
