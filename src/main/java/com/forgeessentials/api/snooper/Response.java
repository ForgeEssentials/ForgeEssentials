package com.forgeessentials.api.snooper;

import com.forgeessentials.api.json.JSONException;
import com.forgeessentials.api.json.JSONObject;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;

/**
 * If you want your own query response, extend this file and override
 * getResponceString(DatagramPacket packet)
 *
 * @author Dries007
 */

public abstract class Response {
    public int id;
    protected MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
    public boolean allowed = true;

    public abstract JSONObject getResponce(JSONObject input) throws JSONException;

    public abstract String getName();

    public abstract void readConfig(String category, Configuration config);

    public abstract void writeConfig(String category, Configuration config);
}
