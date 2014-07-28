package com.forgeessentials.api;

import com.forgeessentials.api.json.JSONArray;
import com.forgeessentials.api.json.JSONException;
import com.forgeessentials.api.json.JSONObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;

import java.util.Collection;
import java.util.Iterator;

public class TextFormatter {
    public static JSONObject toJSON(ItemStack stack, Boolean listEnch) throws JSONException
    {
        JSONObject data = new JSONObject();
        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("display") && stack.stackTagCompound.getCompoundTag("display").hasKey("Name"))
        {
            data.put("item", stack.getDisplayName().replaceAll("item.", "").replaceAll("tile.", ""));
        }
        if (stack.stackSize != 1)
        {
            data.put("amount", "" + stack.stackSize);
        }
        data.put("id", "" + stack.getUnlocalizedName());
        if (stack.getItemDamage() != 0)
        {
            data.put("dam", "" + stack.getItemDamage());
        }
        data.put("name", stack.getDisplayName());

        if (listEnch)
        {
            JSONArray tempArgs = new JSONArray();
            NBTTagList var10 = stack.getEnchantmentTagList();
            if (var10 != null)
            {
                for (int var7 = 0; var7 < var10.tagCount(); ++var7)
                {
                    short var8 = ((NBTTagCompound) var10.getCompoundTagAt(var7)).getShort("id");
                    short var9 = ((NBTTagCompound) var10.getCompoundTagAt(var7)).getShort("lvl");

                    if (Enchantment.enchantmentsList[var8] != null)
                    {
                        tempArgs.put(Enchantment.enchantmentsList[var8].getTranslatedName(var9));
                    }
                }
                data.put("ench", tempArgs);
            }
        }

        return data;
    }

    public static JSONArray toJSON(Collection<PotionEffect> collection)
    {
        JSONArray data = new JSONArray();
        Iterator<PotionEffect> i = collection.iterator();
        while (i.hasNext())
        {
            Object obj = i.next();
            PotionEffect effect = (PotionEffect) obj;
            data.put(translatePotion(effect));
        }
        return data;
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

    public static JSONArray toJSONnbtComp(NBTTagCompound nbt) throws JSONException
    {
        Object[] obj = nbt.getTagList().toArray();
        JSONArray data = new JSONArray();
        for (int i = 0; i < obj.length; i++)
        {
            data.put(toJSONnbtBase((NBTBase) obj[i]));
        }
        return data;
    }

    public static JSONObject toJSONnbtBase(NBTBase nbt) throws JSONException
    {
        String data;

        if (nbt instanceof NBTTagCompound)
        {
            data = toJSONnbtComp((NBTTagCompound) nbt).toString();
        }
        else if (nbt instanceof NBTTagByteArray)
        {
            data = toJSONnbtBtArray((NBTTagByteArray) nbt).toString();
        }
        else if (nbt instanceof NBTTagIntArray)
        {
            data = toJSONnbtIntArray((NBTTagIntArray) nbt).toString();
        }
        else if (nbt instanceof NBTTagList)
        {
            data = toJSONnbtTagList((NBTTagList) nbt).toString();
        }
        else
        {
            data = nbt.toString();
        }

        return new JSONObject().put(nbt.getId() + "~" + nbt.getName(), data);
    }

    public static JSONArray toJSONnbtTagList(NBTTagList nbt) throws JSONException
    {
        JSONArray data = new JSONArray();
        for (int i = 0; i < nbt.tagCount(); i++)
        {
            data.put(toJSONnbtBase(nbt.getCompoundTagAt(i)));
        }
        return data;
    }

    public static JSONArray toJSONnbtIntArray(NBTTagIntArray nbt)
    {
        return new JSONArray().put(nbt.func_150302_c());
    }

    public static JSONArray toJSONnbtBtArray(NBTTagByteArray nbt)
    {
        return new JSONArray().put(nbt.func_150292_c());
    }

    public static TileEntity reconstructTE(String JSON)
    {
        try
        {
            JSONObject top = new JSONObject(JSON);
            String className = JSONObject.getNames(top)[0];
            TileEntity te = (TileEntity) Class.forName(className).newInstance();
            JSONArray dataArray = top.getJSONArray(className);
            NBTTagCompound data = new NBTTagCompound();
            for (int i = 0; i < dataArray.length(); i++)
            {
                data.setTag(reconstructNBTName(dataArray.get(i).toString()), reconstructNBT(dataArray.get(i).toString()));
            }
            te.readFromNBT(data);
            return te;
        }
        catch (Exception e)
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

    public static NBTTagCompound reconstructNBTTagCompound(JSONArray dataArray)
    {
        try
        {
            NBTTagCompound comp = new NBTTagCompound();
            for (int i = 0; i < dataArray.length(); i++)
            {
                comp.setTag(reconstructNBTName(dataArray.get(i).toString()), reconstructNBT(dataArray.get(i).toString()));
            }
            return comp;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static NBTTagList reconstructNBTTagList(JSONArray dataArray)
    {
        try
        {
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < dataArray.length(); i++)
            {
                list.appendTag(reconstructNBT(dataArray.get(i).toString()));
            }
            return list;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static NBTTagByteArray reconstructNBTTagByteArray(JSONArray dataArray)
    {
        try
        {
            NBTTagByteArray list = new NBTTagByteArray(null);
            list.func_150292_c() = new byte[dataArray.length()];
            for (int i = 0; i < dataArray.length(); i++)
            {
                System.out.println(dataArray.get(i).toString());
                list.func_150292_c()[i] = (byte) dataArray.getInt(i);
            }
            return list;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static NBTTagIntArray reconstructNBTTagIntArray(JSONArray dataArray)
    {
        try
        {
            NBTTagIntArray list = new NBTTagIntArray(null);
            list.intArray = new int[dataArray.length()];
            for (int i = 0; i < dataArray.length(); i++)
            {
                System.out.println(dataArray.get(i).toString());
                list.func_150292_c()[i] = Integer.parseInt(dataArray.get(i).toString());
            }
            return list;
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

            String[] split = key.split("~", 2);
            byte type = new Byte(split[0]);
            String name = split[1];

            switch (type)
            {
            case 1:
                ((NBTTagByte) tag).data = (byte) top.getInt(key);
                break;
            case 2:
                ((NBTTagShort) tag).data = (short) top.getInt(key);
                break;
            case 3:
                ((NBTTagInt) tag).data = (int) top.getInt(key);
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
                tag = reconstructNBTTagByteArray(top.getJSONArray(key));
                tag.setName(name);
                break;
            case 8:
                ((NBTTagString) tag).data = top.getString(key);
                break;
            case 9:
                tag = reconstructNBTTagList(top.getJSONArray(key));
                tag.setName(name);
                break;
            case 10:
                tag = reconstructNBTTagCompound(top.getJSONArray(key));
                tag.setName(name);
                break;
            case 11:
                tag = reconstructNBTTagIntArray(top.getJSONArray(key));
                tag.setName(name);
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
