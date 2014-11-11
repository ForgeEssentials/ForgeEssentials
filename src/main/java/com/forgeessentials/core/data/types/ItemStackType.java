package com.forgeessentials.core.data.types;

import java.lang.reflect.Type;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import cpw.mods.fml.common.registry.GameData;

public class ItemStackType implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    private static final String DAMAGE = "damage";
    private static final String STACK_SIZE = "stackSize";
    private static final String ITEM_ID = "itemID";

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.add(ITEM_ID, new JsonPrimitive(GameData.getItemRegistry().getNameForObject(src.getItem())));
        result.add(STACK_SIZE, new JsonPrimitive(src.stackSize));
        result.add(DAMAGE, new JsonPrimitive(src.getItemDamage()));
        if (src.getTagCompound() != null)
            result.add("compound", context.serialize(src.getTagCompound()));
        return result;
    }

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject obj = json.getAsJsonObject();

        String itemID = obj.get(ITEM_ID).getAsString();
        int stackSize = obj.has(STACK_SIZE) ? obj.get(STACK_SIZE).getAsInt() : 1;
        int damage = obj.has(DAMAGE) ? obj.get(DAMAGE).getAsInt() : 0;

        Item item = GameData.getItemRegistry().getObject(itemID);
        ItemStack stack = new ItemStack(item, stackSize, damage);
        if (obj.has("compound"))
            stack.setTagCompound((NBTTagCompound) context.deserialize(obj.get("compound"), NBTTagCompound.class));
        
        return stack;
    }

}
