package com.ForgeEssentials.snooper.responce;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;

import com.ForgeEssentials.snooper.TextFormatter;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.network.rcon.RConOutputStream;
import net.minecraft.server.MinecraftServer;

public class Response 
{
	RConOutputStream output;
	MinecraftServer server;
	String dataString = "";
	boolean allowed = true;
	
	public Response(DatagramPacket packet)
	{
		server = FMLCommonHandler.instance().getMinecraftServerInstance();
		output = new RConOutputStream(1460);
	}
	
	public byte[] getResponce(byte[] bs) throws IOException
	{
		output.reset();
		output.writeInt(0);
		output.writeByteArray(bs);
		if(allowed) output.writeString(dataString);
		else output.writeString("_NOT_ALLOWED_");
		return output.toByteArray();
	}
}
