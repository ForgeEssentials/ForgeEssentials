package com.ForgeEssentials.core.network;

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.WorldClient;
import net.minecraft.src.WorldServer;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public abstract interface IForgeEssentialsPacket
{
	public static final String FECHANNEL = "ForgeEssentials";
	
	public abstract Packet250CustomPayload getPayload();
}
