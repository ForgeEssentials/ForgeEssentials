package com.ForgeEssentials.network;

import java.io.DataInputStream;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.WorldClient;
import net.minecraft.src.WorldServer;

public abstract class ForgeEssentialsPacketBase extends Packet250CustomPayload
{
	
	public abstract void readServer(DataInputStream stream, WorldServer world, EntityPlayer player);
	
	@SideOnly(value = Side.CLIENT)
	// DON't FORGET THE SIDEONLY!!!!
	public abstract void readClient(DataInputStream stream, WorldClient world, EntityPlayer player);
}
