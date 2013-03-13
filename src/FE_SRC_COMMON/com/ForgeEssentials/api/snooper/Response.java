package com.ForgeEssentials.api.snooper;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.json.JSONException;
import com.ForgeEssentials.api.json.JSONObject;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * If you want your own query response, extend this file and override
 * getResponceString(DatagramPacket packet)
 * @author Dries007
 */

public abstract class Response
{
	public int					id;
	protected MinecraftServer	server		= FMLCommonHandler.instance().getMinecraftServerInstance();
	public boolean				allowed		= true;
	
	public abstract JSONObject getResponce(JSONObject input) throws JSONException;

	public abstract String getName();

	public abstract void readConfig(String category, Configuration config);

	public abstract void writeConfig(String category, Configuration config);
}
