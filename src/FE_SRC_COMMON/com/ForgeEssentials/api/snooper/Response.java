package com.ForgeEssentials.api.snooper;

import com.ForgeEssentials.api.json.JSONException;
import com.ForgeEssentials.api.json.JSONObject;

import net.minecraft.network.rcon.RConOutputStream;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;

/**
 * If you want your own query response, extend this file and override
 * getResponceString(DatagramPacket packet)
 * @author Dries007
 */

public abstract class Response
{
	public int id;
	protected RConOutputStream	output		= new RConOutputStream(1460);
	protected MinecraftServer	server		= FMLCommonHandler.instance().getMinecraftServerInstance();
	public boolean				allowed		= true;

	public abstract JSONObject getResponce(String input) throws JSONException;

	public abstract String getName();

	public abstract void readConfig(String category, Configuration config);

	public abstract void writeConfig(String category, Configuration config);
}
