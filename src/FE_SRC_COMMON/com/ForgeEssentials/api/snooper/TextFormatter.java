package com.ForgeEssentials.api.snooper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;

import com.ForgeEssentials.api.json.JSONArray;
import com.ForgeEssentials.api.json.JSONException;
import com.ForgeEssentials.api.json.JSONObject;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class TextFormatter
{
	public static String toJSON(HashMap<String, String> data)
	{
		if (data.isEmpty())
		{
			return "{}";
		}
		String toSend = "{";

		for (Entry<String, String> set : data.entrySet())
		{
			if (set.getValue().startsWith("{") || set.getValue().startsWith("["))
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
		if (data.length == 0)
		{
			return "[\"\"]";
		}
		String toSend = "[";

		for (String value : data)
		{
			if (value.startsWith("{") || value.startsWith("["))
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
	
	public static String toJSON(int[] data)
	{
		if (data.length == 0)
		{
			return "[\"\"]";
		}
		String toSend = "[";

		for (int value : data)
		{
			toSend += "\"" + value + "\",";
		}

		toSend = toSend.substring(0, toSend.length() - 1);
		toSend += "]";

		return toSend;
	}
	
	public static String toJSON(byte[] data)
	{
		if (data.length == 0)
		{
			return "[\"\"]";
		}
		String toSend = "[";

		for (int value : data)
		{
			toSend += "\"" + value + "\",";
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
		if (point instanceof WorldPoint)
		{
			data.put("dim", "" + ((WorldPoint) point).dim);
		}
		if (point instanceof WarpPoint)
		{
			data.put("dim", "" + ((WarpPoint) point).dim);
			data.put("pitch", "" + ((WarpPoint) point).pitch);
			data.put("yaw", "" + ((WarpPoint) point).yaw);
		}
		return toJSON(data);
	}

	public static String toJSON(ItemStack stack, Boolean listEnch)
	{
		HashMap<String, String> data = new HashMap();
		if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("display") && stack.stackTagCompound.getCompoundTag("display").hasKey("Name"))
		{
			data.put("item", stack.getItemName().replaceAll("item.", "").replaceAll("tile.", ""));
		}
		if (stack.stackSize != 1)
		{
			data.put("amount", "" + stack.stackSize);
		}
		data.put("id", "" + stack.itemID);
		if (stack.getItemDamage() != 0)
		{
			data.put("dam", "" + stack.getItemDamage());
		}
		data.put("name", stack.getDisplayName());

		if (listEnch)
		{
			ArrayList<String> tempArgs = new ArrayList();
			NBTTagList var10 = stack.getEnchantmentTagList();
			if (var10 != null)
			{
				for (int var7 = 0; var7 < var10.tagCount(); ++var7)
				{
					short var8 = ((NBTTagCompound) var10.tagAt(var7)).getShort("id");
					short var9 = ((NBTTagCompound) var10.tagAt(var7)).getShort("lvl");

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

	public static String toJSON(Collection<PotionEffect> collection)
	{
		String[] data = new String[collection.size()];
		Iterator i = collection.iterator();
		int id = 0;
		while (i.hasNext())
		{
			Object obj = i.next();
			PotionEffect effect = ((PotionEffect) obj);
			data[id] = translatePotion(effect);
			id++;
		}
		return toJSON(data);
	}

	public static String translatePotion(PotionEffect effect)
	{
		Potion potion = Potion.potionTypes[effect.getPotionID()];
		String name = StatCollector.translateToLocal(potion.getName());

		if (effect.getAmplifier() == 1)
		{
			name = name + " II";
		}
		else if (effect.getAmplifier() == 2)
		{
			name = name + " III";
		}
		else if (effect.getAmplifier() == 3)
		{
			name = name + " IV";
		}
		return name;
	}

	public static String toJSON(List<String> data)
	{
		if (data.size() == 0)
		{
			return "[]";
		}
		String toSend = "[";

		for (String value : data)
		{
			if (value.startsWith("{") || value.startsWith("["))
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
	
	public static String toJSONnbtComp(NBTTagCompound nbt)
	{
		Object[] obj = nbt.getTags().toArray();
		String[] data = new String[nbt.getTags().size()];
		for(int i = 0; i < obj.length; i++)
		{
			data[i] = toJSONnbtBase((NBTBase) obj[i]);
		}
		return toJSON(data);
	}
	
	public static String toJSONnbtBase(NBTBase nbt)
	{
		HashMap<String, String> map = new HashMap();
		String data;
		
		if (nbt instanceof NBTTagCompound) data = toJSONnbtComp((NBTTagCompound) nbt);
		else if (nbt instanceof NBTTagByteArray) data = toJSONnbtBtArray((NBTTagByteArray) nbt);
		else if (nbt instanceof NBTTagIntArray) data = toJSONnbtIntArray((NBTTagIntArray) nbt);
		else if (nbt instanceof NBTTagList) data = toJSONnbtTagList((NBTTagList) nbt);
		else data = nbt.toString();
		
		map.put(nbt.getId() + "~" + nbt.getName(), data);
		return toJSON(map);
	}
	
	public static String toJSONnbtTagList(NBTTagList nbt)
	{
		String[] data = new String[nbt.tagCount()];
		for(int i = 0; i < nbt.tagCount(); i ++)
		{
			data[i] = toJSONnbtBase(nbt.tagAt(i));
		}
		return toJSON(data);
	}
	
	public static String toJSONnbtIntArray(NBTTagIntArray nbt)
	{
		return toJSON(nbt.intArray);
	}

	public static String toJSONnbtBtArray(NBTTagByteArray nbt)
	{
		return toJSON(nbt.byteArray);
	}
	
	public static TileEntity reconstructTE(String JSON)
	{
		try
		{
			JSONObject top = new JSONObject(JSON);
			String className = top.getNames(top)[0]; 
			OutputHandler.debug("RECONSTRUCT: " + className);
			TileEntity te = (TileEntity) Class.forName(className).newInstance();
			JSONArray dataArray = top.getJSONArray(className);
			NBTTagCompound data = new NBTTagCompound();
			for(int i = 0; i < dataArray.length(); i++)
			{
				//OutputHandler.debug("SETTAG: " + reconstructNBTName(dataArray.get(i).toString()) + " DATA: " + reconstructNBT(dataArray.get(i).toString()));
				data.setTag(reconstructNBTName(dataArray.get(i).toString()), reconstructNBT(dataArray.get(i).toString()));
			}
			return null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}	
	}
	
	public static String reconstructNBTName(String JSON)
	{
		try
		{
			JSONObject top = new JSONObject(JSON);
			return ((String) top.keySet().toArray()[0]).split("~", 2)[1];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static NBTBase reconstructNBT(String JSON)
	{
		try
		{
			JSONObject top = new JSONObject(JSON);
			Object keyObj = top.keySet().toArray()[0];
			
			String key = keyObj.toString();
				
			//OutputHandler.debug("KEY: " + key + " DATA: " + top.get(key));
				
			String[] split = key.split("~", 2);
			byte type = new Byte(split[0]);
			String name = split[1];
				
			NBTBase tag = NBTBase.newTag(type, name);
			switch(type)
			{
			case 1:
				((NBTTagByte) tag).data = (byte) top.getInt(key);
				break;
			case 2:
				((NBTTagShort) tag).data = (short) top.getInt(key);
				break;
			case 3:
				((NBTTagInt) tag).data = top.getInt(key);
				break;
			case 4:
				((NBTTagLong) tag).data = top.getLong(key);
				break;
			case 5:
				((NBTTagFloat) tag).data = (float) top.getDouble(key);
				break;
			case 6:
				((NBTTagDouble) tag).data = top.getDouble(key);
				break;
			case 7:
				//SPECIAL CASE NBTTagByteArray
				System.out.println("SPECIAL CASE NBTTagByteArray");
				break;
			case 8:
				((NBTTagString) tag).data = top.getString(key);
				break;
			case 9:
				//SPECIAL CASE NBTTagList
				System.out.println("SPECIAL CASE NBTTagList");
				break;
			case 10:
				//SPECIAL CASE NBTTagCompound
				System.out.println("SPECIAL CASE NBTTagCompound");
				break;
			case 11:
				//SPECIAL CASE NBTTagIntArray
				System.out.println("SPECIAL CASE NBTTagIntArray");
				break;
			}
			return tag;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
