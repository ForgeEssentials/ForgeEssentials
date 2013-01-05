package com.ForgeEssentials.util;

import com.ForgeEssentials.core.misc.ItemList;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
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
		double var7 = player.prevPosX + (player.posX - player.prevPosX) * var4;
		double var9 = player.prevPosY + (player.posY - player.prevPosY) * var4 + 1.62D - player.yOffset;
		double var11 = player.prevPosZ + (player.posZ - player.prevPosZ) * var4;
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
		Vec3 var23 = var13.addVector(var18 * var21, var17 * var21, var20 * var21);
		return player.worldObj.rayTraceBlocks_do_do(var13, var23, false, !true);
	}

	public static String getZoneWorldString(World world)
	{
		return "WORLD_" + world.provider.getDimensionName() + "_" + world.provider.dimensionId;
	}

	public static WorldServer getDimension(int dimension)
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimension);
	}

	public static WorldPoint getEntityPoint(Entity entity)
	{
		return new WorldPoint(entity.worldObj, (int) Math.round(entity.posX), (int) Math.round(entity.posY), (int) Math.round(entity.posZ));
	}

	public static EntityPlayerMP getPlayerFromUsername(String username)
	{
		EntityPlayerMP target;
		List possibles = new LinkedList<EntityPlayer>();
		for (Object player : FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().playerEntityList)
		{
			if (player instanceof EntityPlayerMP)
			{
				if (((EntityPlayerMP) player).getCommandSenderName().toLowerCase().contains(username.toLowerCase()))
				{
					possibles.add((EntityPlayerMP) player);
				}
				target = (EntityPlayerMP) player;
				if (target.getCommandSenderName().equalsIgnoreCase(username))
					return target;
			}
		}
		if (possibles.size() == 1)
			return (EntityPlayerMP) possibles.toArray()[0];
		return null;
	}

	/**
	 * does NOT check if its a valid BlockID and stuff.. this may be used for items.
	 * @return never NULL. always {0, -1}. Meta by default is -1.
	 * @throws RuntimeException the message is a formatted chat string.
	 */
	public static int[] parseIdAndMetaFromString(String msg, boolean blocksOnly) throws RuntimeException
	{
		int ID;
		int meta = -1;

		// perhaps the ID:Meta format
		if (msg.contains(":"))
		{
			String[] pair = msg.split(":", 2);

			try
			{
				ID = Integer.parseInt(pair[0]);
			}
			catch (NumberFormatException e)
			{
				ID = getItemIDFromName(pair[0], blocksOnly);
			}

			try
			{
				meta = Integer.parseInt(pair[1]);
			}
			catch (NumberFormatException e)
			{
				throw new RuntimeException(Localization.format(Localization.ERROR_NAN, pair[1]));
			}
		}
		else
		{
			try
			{
				ID = Integer.parseInt(msg);
				meta = -1;
			}
			catch (NumberFormatException e)
			{
				ID = getItemIDFromName(msg, blocksOnly);
			}
		}

		// try checking if its just an ID

		return new int[] { ID, meta };
	}

	public static int getItemIDFromName(String name, boolean blockOnly)
	{
		if (blockOnly)
		{
			Block block = ItemList.instance().getBlockForName(name);
			if (block == null)
				return 0;
			else
				return block.blockID;
		}
		else
		{
			Item item = ItemList.instance().getItemForName(name);
			if (item == null)
				return 0;
			else
				return item.itemID;
		}
	}

	public static File getBaseDir()
	{
		if (FMLCommonHandler.instance().getSide().isClient())
		{
			FMLClientHandler.instance().getClient();
			return Minecraft.getMinecraftDir();
		}
		else
			return new File(".");
	}

	public static boolean isPlayerOp(String player)
	{
		return true;
		/*
		MinecraftServer server = FMLCommonHandler.instance().getSidedDelegate().getServer();

		// SP and LAN
		if (server instanceof IntegratedServer && server.getServerOwner().equalsIgnoreCase(player))
			return true;

		// SMP
		return server.getConfigurationManager().getOps().contains(player);
		*/
	}

	public static double getTPS(int dimID)
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

		long var2 = 0L;
		long[] var4 = server.worldTickTimes.get(dimID);
		int var5 = var4.length;

		for (int var6 = 0; var6 < var5; ++var6)
		{
			long var7 = var4[var6];
			var2 += var7;
		}

		double tps = (double) var2 / (double) var5 * 1.0E-6D;

		if (tps < 50)
			return 20;
		else
			return (double) (1000 / tps);
	}
}
