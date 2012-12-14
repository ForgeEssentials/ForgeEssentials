package com.ForgeEssentials.util;

import com.ForgeEssentials.util.AreaSelector.Point;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;
import net.minecraft.src.WorldServer;
import cpw.mods.fml.common.FMLCommonHandler;

public final class FunctionHelper
{
	/**
	 * stolen from Item class
	 */
	public static MovingObjectPosition getPlayerLookingSpot(EntityPlayer player, boolean restrict)
	{
		float var4 = 1.0F;
		float var5 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * var4;
		float var6 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * var4;
		double var7 = player.prevPosX + (player.posX - player.prevPosX) * (double) var4;
		double var9 = player.prevPosY + (player.posY - player.prevPosY) * (double) var4 + 1.62D - (double) player.yOffset;
		double var11 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) var4;
		Vec3 var13 = player.worldObj.getWorldVec3Pool().getVecFromPool(var7, var9, var11);
		float var14 = MathHelper.cos(-var6 * 0.017453292F - (float) Math.PI);
		float var15 = MathHelper.sin(-var6 * 0.017453292F - (float) Math.PI);
		float var16 = -MathHelper.cos(-var5 * 0.017453292F);
		float var17 = MathHelper.sin(-var5 * 0.017453292F);
		float var18 = var15 * var16;
		float var20 = var14 * var16;
		double var21 = 500D;
		if (player instanceof EntityPlayerMP && restrict)
		{
			var21 = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
		}
		Vec3 var23 = var13.addVector((double) var18 * var21, (double) var17 * var21, (double) var20 * var21);
		return player.worldObj.rayTraceBlocks_do_do(var13, var23, false, !true);
	}

	@Deprecated
	/**
	 * use DimensionIDs instead
	 */
	public static String getWorldString(World world)
	{
		return getDimension(0).getChunkSaveLocation() + "_" + world.getWorldInfo().getDimension();
	}

	public static String getZoneWorldString(World world)
	{
		return "WORLD_" + world.provider.getDimensionName() + "_" +world.provider.dimensionId;
	}
	
	@Deprecated
	/**
	 * use DimensionIDs instead
	 */
	public static WorldServer getWorldFromWorldString(String worldString)
	{
		int dimensionID = Integer.parseInt(worldString.substring(worldString.lastIndexOf('_'))); 
		return FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimensionID);
	}

	public static WorldServer getDimension(int dimension)
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimension);
	}
	
	public static Point getEntityPoint(Entity entity)
	{
		return new Point((int)Math.round(entity.posX), (int)Math.round(entity.posY), (int)Math.round(entity.posZ));
	}
}
