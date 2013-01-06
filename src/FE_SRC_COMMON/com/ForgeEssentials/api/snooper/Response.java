package com.ForgeEssentials.api.snooper;

import java.io.IOException;
import java.net.DatagramPacket;

import net.minecraft.network.rcon.RConOutputStream;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;

/**
 * If you want your own query response, extend this file and override getResponceString(DatagramPacket packet)
 * @author Dries007
 *
 */

public abstract class Response 
{
	protected RConOutputStream output = new RConOutputStream(1460);
	protected MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
	protected String dataString = "";
	public boolean allowed = true;
	
	public abstract String getResponceString(DatagramPacket packet);
	
	public byte[] getResponceByte(byte[] bs, DatagramPacket packet) throws IOException
	{
		output.reset();
		output.writeInt(0);
		output.writeByteArray(bs);
		if(allowed) output.writeString(getResponceString(packet));
		else output.writeString("_NOT_ALLOWED_");
		return output.toByteArray();
	}

	public abstract String getName();

	public abstract void readConfig(String category, Configuration config);
	
	public abstract void writeConfig(String category, Configuration config);
}
