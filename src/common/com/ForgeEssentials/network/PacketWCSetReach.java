package com.ForgeEssentials.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.ForgeEssentials.WorldControl.ExtendedPlayerControllerMP;
import com.ForgeEssentials.WorldControl.WorldControlMain;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.WorldClient;
import net.minecraft.src.WorldServer;

public class PacketWCSetReach extends Packet250CustomPayload
{
	// none for now.. it is the only packet sent for WorldControl
	// public static final int packetID = 0;

	public PacketWCSetReach(float reachnum)
	{
		try
		{
			ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
			DataOutputStream stream = new DataOutputStream(streambyte);

			// stream.write(packetID);

			stream.writeFloat(reachnum);

			data = streambyte.toByteArray();
			length = data.length;
			channel = WorldControlMain.CHANNEL;

			stream.close();
			streambyte.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@SideOnly(value = Side.CLIENT)
	public static void readClient(DataInputStream stream, WorldClient world, EntityPlayer player)
	{
		try
		{
			float reach = stream.readFloat();
			ExtendedPlayerControllerMP.reachDistance = reach;
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void readServer(DataInputStream stream, WorldServer world, EntityPlayer player)
	{
		// never received here...
	}
}
