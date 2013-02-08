package com.ForgeEssentials.util;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
import net.minecraftforge.common.DimensionManager;

import com.ForgeEssentials.core.misc.ItemList;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

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
	
	public static String[] dropFirstString(String[] par0ArrayOfStr)
    {
        String[] var1 = new String[par0ArrayOfStr.length - 1];

        for (int var2 = 1; var2 < par0ArrayOfStr.length; ++var2)
        {
            var1[var2 - 1] = par0ArrayOfStr[var2];
        }

        return var1;
    }

	public static String getZoneWorldString(World world)
	{
		return "WORLD_" + world.provider.getDimensionName().replace(' ', '_') + "_" + world.provider.dimensionId;
	}

	public static WorldServer getDimension(int dimension)
	{
		return DimensionManager.getWorld(dimension);
	}

	/**
	 * Use WorldPoint(Entity)
	 * @param entity
	 * @return
	 */
	@Deprecated
	public static WorldPoint getEntityPoint(Entity entity)
	{
		return new WorldPoint(entity);
	}

	public static EntityPlayerMP getPlayerFromPartialName(String username)
	{
		EntityPlayerMP target;
		List possibles = new LinkedList<EntityPlayer>();
		ArrayList<EntityPlayerMP> temp = (ArrayList<EntityPlayerMP>) FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().playerEntityList;
		for (EntityPlayerMP player : temp)
		{
			if (player.username.equalsIgnoreCase(username))
				return player;

			if (player.username.toLowerCase().contains(username.toLowerCase()))
				possibles.add(player);
		}
		if (possibles.size() == 1)
		{
			return (EntityPlayerMP) possibles.toArray()[0];
		}
		return null;
	}

	/**
	 * does NOT check if its a valid BlockID and stuff.. this may be used for items.
	 * 
	 * @return never NULL. always {0, -1}. Meta by default is -1.
	 * @throws RuntimeException
	 * the message is a formatted chat string.
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
			{
				return 0;
			}
			else
			{
				return block.blockID;
			}
		}
		else
		{
			Item item = ItemList.instance().getItemForName(name);
			if (item == null)
			{
				return 0;
			}
			else
			{
				return item.itemID;
			}
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
		{
			return new File(".");
		}
	}

	public static boolean isPlayerOp(String player)
	{
		MinecraftServer server = FMLCommonHandler.instance().getSidedDelegate().getServer();

		// SP and LAN
		if (server.isSinglePlayer())
		{
			if (server instanceof IntegratedServer && server.getServerOwner().equalsIgnoreCase(player))
			{
				return true;
			}
		}

		// SMP
		return server.getConfigurationManager().getOps().contains(player);
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
		{
			return 20;
		}
		else
		{
			return 1000 / tps;
		}
	}

	/**
	 * 
	 * @param text
	 * @param search
	 * @param replacement
	 * @return
	 */
	public static String replaceAllIgnoreCase(String text, String search, String replacement)
	{
		if (search.equals(replacement))
			return text;
		StringBuilder buffer = new StringBuilder(text);
		String lowerSearch = search.toLowerCase();
		int i = 0;
		int prev = 0;
		while ((i = buffer.toString().toLowerCase().indexOf(lowerSearch, prev)) > -1)
		{
			buffer.replace(i, i + search.length(), replacement);
			prev = i + replacement.length();
		}
		return buffer.toString();
	}
	
	/**
	 * Uses & as identifier
	 * @param message
	 * @return
	 */
	public static String formatColors(String message)
	{
		char[] b = message.toCharArray();
		for (int i = 0; i < b.length - 1; i++)
		{
			if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1)
			{
				b[i] = '\u00a7';
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}
		return new String(b);
	}
	
	/**
	 * Uses the % char as identifier
	 * @param format
	 * @return
	 */
	public static String format(String format)
	{
		format = replaceAllIgnoreCase(format, "%smile", "\u263A");
		format = replaceAllIgnoreCase(format, "%copyrighted", "\u00A9");
		format = replaceAllIgnoreCase(format, "%registered", "\u00AE");
		format = replaceAllIgnoreCase(format, "%diamond", "\u2662");
		format = replaceAllIgnoreCase(format, "%spade", "\u2664");
		format = replaceAllIgnoreCase(format, "%club", "\u2667");
		format = replaceAllIgnoreCase(format, "%heart", "\u2661");
		format = replaceAllIgnoreCase(format, "%female", "\u2640");
		format = replaceAllIgnoreCase(format, "%male", "\u2642");
		
		// replace colors
		format = replaceAllIgnoreCase(format, "%red", FEChatFormatCodes.RED.toString());
		format = replaceAllIgnoreCase(format, "%yellow", FEChatFormatCodes.YELLOW.toString());
		format = replaceAllIgnoreCase(format, "%black", FEChatFormatCodes.BLACK.toString());
		format = replaceAllIgnoreCase(format, "%darkblue", FEChatFormatCodes.DARKBLUE.toString());
		format = replaceAllIgnoreCase(format, "%darkgreen", FEChatFormatCodes.DARKGREEN.toString());
		format = replaceAllIgnoreCase(format, "%darkaqua", FEChatFormatCodes.DARKAQUA.toString());
		format = replaceAllIgnoreCase(format, "%darkred", FEChatFormatCodes.DARKRED.toString());
		format = replaceAllIgnoreCase(format, "%purple", FEChatFormatCodes.PURPLE.toString());
		format = replaceAllIgnoreCase(format, "%gold", FEChatFormatCodes.GOLD.toString());
		format = replaceAllIgnoreCase(format, "%grey", FEChatFormatCodes.GREY.toString());
		format = replaceAllIgnoreCase(format, "%darkgrey", FEChatFormatCodes.DARKGREY.toString());
		format = replaceAllIgnoreCase(format, "%indigo", FEChatFormatCodes.INDIGO.toString());
		format = replaceAllIgnoreCase(format, "%green", FEChatFormatCodes.GREEN.toString());
		format = replaceAllIgnoreCase(format, "%aqua", FEChatFormatCodes.AQUA.toString());
		format = replaceAllIgnoreCase(format, "%pink", FEChatFormatCodes.PINK.toString());
		format = replaceAllIgnoreCase(format, "%white", FEChatFormatCodes.WHITE.toString());

		// replace MC formating
		format = replaceAllIgnoreCase(format, "%random", FEChatFormatCodes.RANDOM.toString());
		format = replaceAllIgnoreCase(format, "%bold", FEChatFormatCodes.BOLD.toString());
		format = replaceAllIgnoreCase(format, "%strike", FEChatFormatCodes.STRIKE.toString());
		format = replaceAllIgnoreCase(format, "%underline", FEChatFormatCodes.UNDERLINE.toString());
		format = replaceAllIgnoreCase(format, "%italics", FEChatFormatCodes.ITALICS.toString());
		format = replaceAllIgnoreCase(format, "%reset", FEChatFormatCodes.RESET.toString());
		
		return format;
	}
	
	public static void setPlayer(EntityPlayerMP player, WarpPoint p)
	{
		player.playerNetServerHandler.setPlayerLocation(p.xd, p.yd, p.zd, p.yaw, p.pitch);
	}

}
