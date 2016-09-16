package com.forgeessentials.data.v2.types;

import java.lang.reflect.Type;

import net.minecraft.block.Block;

import com.forgeessentials.data.v2.DataManager.DataType;
import com.forgeessentials.util.Utils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import cpw.mods.fml.common.registry.GameData;

public class BlockType implements DataType<Block>
{

    @Override
    public JsonElement serialize(Block src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(Utils.getBlockName(src));
    }

    @Override
    public Block deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    {
        return GameData.getBlockRegistry().getObject(json.getAsString());
    }

    @Override
    public Class<Block> getType()
    {
        return Block.class;
    }

}
