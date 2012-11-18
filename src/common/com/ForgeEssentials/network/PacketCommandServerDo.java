package com.ForgeEssentials.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.ForgeEssentials.core.OutputHandler;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.WorldClient;
import net.minecraft.src.WorldServer;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class PacketCommandServerDo extends ForgeEssentialsPacketBase
{
	
	public static final byte	packetID = 1;
	
	public PacketCommandServerDo(EntityPlayer player, String[] args)
	{
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytestream);
		
		try
		{
			stream.write(packetID);
			
			stream.writeBytes(player.username);
			
			stream.writeInt(args.length);
			for (int i = 0; i < args.length; ++i)
			{
				stream.writeBytes(args[i]);
			}
		
			stream.close();
			bytestream.close();
			
			this.channel = FECHANNEL;
			this.data = bytestream.toByteArray();
			this.length = bytestream.size();
			
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Error creating packet >> " +
					this.getClass() + "\n" + e.getMessage());
		}
	}

	@Override
	public void readServer(DataInputStream stream, WorldServer world, EntityPlayer player) throws IOException
	{
		OutputHandler.SOP("hit ReadServer()!");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readClient(DataInputStream stream, WorldClient world, EntityPlayer player) throws IOException
	{
		// Packets are sent from the client to server; do nothing here.
	}

}
