package com.forgeessentials.data.v2.types;

import java.lang.reflect.Type;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

import com.forgeessentials.data.v2.DataManager.DataType;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class ItemStackType implements DataType<ItemStack>
{

    private static final String DAMAGE = "damage";
    private static final String STACK_SIZE = "stackSize";
    private static final String ITEM_ID = "itemID";

    private static int getSafeJsonInt(JsonElement element, int defaultValue)
    {
        if (element == null || !element.isJsonPrimitive())
            return defaultValue;
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        try
        {
            return primitive.getAsInt();
        }
        catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.add(ITEM_ID, new JsonPrimitive(GameData.getItemRegistry().getNameForObject(src.getItem()).toString()));
        result.add(STACK_SIZE, new JsonPrimitive(src.stackSize));
        result.add(DAMAGE, new JsonPrimitive(src.getItemDamage()));
        if (src.getTagCompound() != null)
            result.add("compound", context.serialize(src.getTagCompound()));
        return result;
    }

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        try
        {
            // Load data
            JsonObject obj = json.getAsJsonObject();
            String itemID = obj.get(ITEM_ID).getAsString();
            int stackSize = getSafeJsonInt(obj.get(STACK_SIZE), 1);
            int damage = getSafeJsonInt(obj.get(DAMAGE), 0);

            // Get and check item
            Item item = GameData.getItemRegistry().getObject(new ResourceLocation(itemID));
            if (item == null)
                return null;

            // Create item-stack and parse NBT data if the is any
            ItemStack stack = new ItemStack(item, stackSize, damage);
            if (obj.has("compound"))
                stack.setTagCompound((NBTTagCompound) context.deserialize(obj.get("compound"), NBTTagCompound.class));

            return stack;
        }
        catch (Throwable e)
        {
            LoggingHandler.felog.error(String.format("Error parsing data: %s", json.toString()));
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Class<ItemStack> getType()
    {
        return ItemStack.class;
    }

}
