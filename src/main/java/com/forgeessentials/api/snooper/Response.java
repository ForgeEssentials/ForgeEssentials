package com.forgeessentials.api.snooper;

import java.lang.reflect.Type;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * If you want your own query response, extend this file and override
 * getResponceString(DatagramPacket packet)
 *
 * @author Dries007
 */
public abstract class Response
{
    protected final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
    protected static final Gson GSON = (new GsonBuilder())
            .registerTypeHierarchyAdapter(NBTBase.class, new NBTBaseAdapter())
            .setPrettyPrinting()
            .create();
    
    public int id;
    public boolean allowed = true;

    public abstract JsonElement getResponse(JsonObject jsonElement) throws JsonParseException;

    public abstract String getName();

    public abstract void readConfig(String category, Configuration config);

    public abstract void writeConfig(String category, Configuration config);
    
    private static class NBTBaseAdapter implements JsonDeserializer<NBTBase>, JsonSerializer<NBTBase> 
    {

        public NBTBaseAdapter() {}
        

        @Override
        public JsonElement serialize(NBTBase src, Type typeOfSrc, JsonSerializationContext context)
        {
            return (new JsonParser()).parse(src.toString());
        }

        @Override
        public NBTBase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)throws JsonParseException
        {
            try
            {
                return parseAsJson(GSON.toJson(json));
            }
            catch (NBTException e)
            {
                e.printStackTrace();
                return null;
            }
        }

        private NBTBase parseAsJson(String string) throws NBTException
        {
         return JsonToNBT.func_150315_a(string)   ;
        }
    }
}
