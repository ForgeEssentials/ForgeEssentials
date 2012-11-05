package com.ForgeEssentials.WorldControl;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.PlayerControllerMP;

public class ExtendedPlayerControllerMP extends PlayerControllerMP
{

	public ExtendedPlayerControllerMP(Minecraft par1Minecraft, NetClientHandler par2NetClientHandler, boolean isInCreative)
	{
		super(par1Minecraft, par2NetClientHandler);
		super.setGameType(isInCreative ? EnumGameType.CREATIVE : EnumGameType.SURVIVAL);
		reachDistance = this.isInCreativeMode() ? 5F : 4.5F;
	}

	public static float reachDistance = 5F;

	/**
	 * player reach distance = 4F
	 */
	public float getBlockReachDistance()
	{
		return reachDistance;
	}
	
	public static void resetReach()
	{
		reachDistance = FMLClientHandler.instance().getClient().playerController.isInCreativeMode() ? 5F : 4.5F;
	}

}
