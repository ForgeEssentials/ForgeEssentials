package com.ForgeEssentials.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
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

import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.core.CoreConfig;
import com.ForgeEssentials.core.misc.FriendlyItemList;
import com.ForgeEssentials.permission.SqlHelper;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;
import com.google.common.base.Joiner;

import cpw.mods.fml.common.FMLCommonHandler;

public final class FunctionHelper
{
    public static Pattern   groupRegex              = Pattern.compile("\\{[a-zA-Z0-9._]*\\<\\:\\>[a-zA-Z0-9._]*\\}");
    
	/**
	 * Get player's looking spot.
	 * @param player
	 * @param restrict
	 * Keep max distance to 5.
	 * @return
	 * The position as a MovingObjectPosition
	 * Null if not existent.
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

	/**
	 * Gets a nice string with only needed elements.
	 * Max time is weeks
	 * @param timeInSec
	 * @return Time in string format
	 */
	public static String parseTime(int timeInSec)
	{
		String uptime = "";
		int weeks = timeInSec / (86400 * 7);
		int remainder = timeInSec % (86400 * 7);
		int days = remainder / 86400;
		remainder = timeInSec % 86400;
		int hours = remainder / 3600;
		remainder = timeInSec % 3600;
		int minutes = remainder / 60;
		int seconds = remainder % 60;

		if (weeks != 0)
		{
			uptime += weeks + " weeks ";
		}

		if (days != 0)
		{
			uptime += (days < 10 ? "0" : "") + days + " days ";
		}

		if (hours != 0)
		{
			uptime += (hours < 10 ? "0" : "") + hours + " h ";
		}

		if (minutes != 0)
		{
			uptime += (minutes < 10 ? "0" : "") + minutes + " min ";
		}

		uptime += (seconds < 10 ? "0" : "") + seconds + " sec.";

		return uptime;
	}

	/**
	 * DO NOT use this for commands
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static EntityPlayerMP getPlayerForName(String name)
	{	
		// tru exact match first.
		{
			EntityPlayerMP tempPlayer = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(name);
			if (tempPlayer != null)
				return tempPlayer;
		}

		// now try getting others
		List<EntityPlayerMP> possibles = new LinkedList<EntityPlayerMP>();
		ArrayList<EntityPlayerMP> temp = (ArrayList<EntityPlayerMP>) FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().playerEntityList;
		for (EntityPlayerMP player : temp)
		{
			if (player.username.equalsIgnoreCase(name))
				return player;

			if (player.username.toLowerCase().contains(name.toLowerCase()))
			{
				possibles.add(player);
			}
		}
		if (possibles.size() == 1)
			return possibles.get(0);
		return null;
	}
	
	public static EntityPlayerMP getPlayerForName(ICommandSender sender, String name)
	{
        EntityPlayerMP var2 = PlayerSelector.matchOnePlayer(sender, name);

        if (var2 != null)
        {
            return var2;
        }
        else
        {
        	return getPlayerForName(name);
        }
	}

	/**
	 * Make new Array with everything except the 1st string.
	 * @param par0ArrayOfStr Old array
	 * @return New array
	 */
	public static String[] dropFirstString(String[] par0ArrayOfStr)
	{
		String[] var1 = new String[par0ArrayOfStr.length - 1];

		for (int var2 = 1; var2 < par0ArrayOfStr.length; ++var2)
		{
			var1[var2 - 1] = par0ArrayOfStr[var2];
		}

		return var1;
	}

	/**
	 * Gets the zoneID for a world.
	 * @param world
	 * @return
	 * The zoneID
	 */
	public static String getZoneWorldString(World world)
	{
		return "WORLD_" + world.provider.getDimensionName().replace(' ', '_') + "_" + world.provider.dimensionId;
	}

	public static WorldServer getDimension(int dimension)
	{
		return DimensionManager.getWorld(dimension);
	}

	/**
	 * does NOT check if its a valid BlockID and stuff.. this may be used for
	 * items.
	 * @return never NULL. always {0, -1}. Meta by default is -1.
	 * @throws NumberFormatException
	 * the message is a formatted chat string.
	 */
	public static int[] parseIdAndMetaFromString(String msg, boolean blocksOnly) throws NumberFormatException
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
				throw new NumberFormatException(Localization.format(Localization.ERROR_NAN, pair[1]));
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

		return new int[]
		{ ID, meta };
	}

	public static int getItemIDFromName(String name, boolean blockOnly)
	{
		if (blockOnly)
		{
			Block block = FriendlyItemList.instance().getBlockForName(name);
			if (block == null)
				return 0;
			else
				return block.blockID;
		}
		else
		{
			Item item = FriendlyItemList.instance().getItemForName(name);
			if (item == null)
				return 0;
			else
				return item.itemID;
		}
	}

	/**
	 * please use your module dir!
	 * @return
	 */
	public static File getBaseDir()
	{
		if (FMLCommonHandler.instance().getSide().isClient())
			return Minecraft.getMinecraftDir();
		else
			return new File(".");
	}

	/**
	 * OP detection
	 * @param player
	 * @return
	 */
	public static boolean isPlayerOp(String player)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return true;

		MinecraftServer server = FMLCommonHandler.instance().getSidedDelegate().getServer();

		// SP and LAN
		if (server.isSinglePlayer())
		{
			if (server instanceof IntegratedServer && server.getServerOwner().equalsIgnoreCase(player))
				return true;
		}

		// SMP
		return server.getConfigurationManager().getOps().contains(player);
	}

	/**
	 * Get tps per word.
	 * @param dimID
	 * @return
	 * -1 if error
	 */
	public static double getTPS(int dimID)
	{
		try
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
				return 1000 / tps;
		}
		catch (Exception e)
		{
			return -1;
		}
	}

	/**
	 * Get tps.
	 * @return
	 * -1 if error
	 */
	public static double getTPS()
	{
		try
		{
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

			long var2 = 0L;
			long[] var4 = server.tickTimeArray;
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
				return 1000 / tps;
		}
		catch (Exception e)
		{
			return -1;
		}
	}

	/**
	 * @return The current date in form YYYY-MM-DD
	 */
	public static String getCurrentDateString()
	{
		Calendar c = Calendar.getInstance();

		StringBuilder builder = new StringBuilder();
		builder.append(c.get(Calendar.YEAR));
		builder.append('-');
		builder.append(c.get(Calendar.MONTH) + 1);
		builder.append('-');
		builder.append(c.get(Calendar.DAY_OF_MONTH));

		return builder.toString();
	}

	/**
	 * @return the current Time in HH:mm format. 24hr clock.
	 */
	public static String getCurrentTimeString()
	{
		Calendar c = Calendar.getInstance();

		StringBuilder builder = new StringBuilder();
		builder.append(c.get(Calendar.HOUR_OF_DAY));
		builder.append(':');
		builder.append(c.get(Calendar.MINUTE));

		return builder.toString();
	}

	/**
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

	/**
	 * instWarp a player to a point. Please use TeleportCenter!
	 * @param player
	 * @param p
	 */
	public static void setPlayer(EntityPlayerMP player, WarpPoint p)
	{
		if (player.dimension != p.dim)
		{
			MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(player, p.dim);
		}
		player.playerNetServerHandler.setPlayerLocation(p.xd, p.yd, p.zd, p.yaw, p.pitch);
		player.prevPosX = player.posX = p.xd;
		player.prevPosY = player.posY = p.yd;
		player.prevPosZ = player.posZ = p.zd;
	}

	/**
	 * instWarp a player to a point. Please use TeleportCenter!
	 * @param player
	 * @param world
	 * @param p
	 */
	public static void setPlayer(EntityPlayerMP player, Point point, World world)
	{
		if (player.dimension != world.provider.dimensionId)
		{
			MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(player, world.provider.dimensionId);
		}
		double x = point.x, y = point.y, z = point.z;
		x = x < 0 ? x - 0.5 : x + 0.5;
		z = z < 0 ? z - 0.5 : z + 0.5;
		player.playerNetServerHandler.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
	}

	/**
	 * Join string[] to print to users. "str1, str2, str3, ..., strn"
	 * @param par0ArrayOfObj
	 * @return
	 */
	public static String niceJoin(Object[] array)
	{
		return joiner.join(array);
	}

	// used for niceJoin method.
	private static Joiner	joiner	= Joiner.on(", ").skipNulls();

	public static boolean isNumeric(String string)
	{
		try
		{
			Integer.parseInt(string);
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}
	
	public static String getGroupRankString(String username)
    {
        Matcher match = groupRegex.matcher(CoreConfig.groupRankFormat);
        ArrayList<TreeSet<Group>> list = getGroupsList(match, username);

        String end = "";

        StringBuilder temp = new StringBuilder();
        for (TreeSet<Group> set : list)
        {
            for (Group g : set)
            {
                if (temp.length() != 0)
                {
                    temp.append("&r");
                }

                temp.append(g.name);
            }

            end = match.replaceFirst(temp.toString());
            temp = new StringBuilder();
        }

        return end;
    }

	public static String getGroupPrefixString(String username)
    {
        Matcher match = groupRegex.matcher(CoreConfig.groupPrefixFormat);

        ArrayList<TreeSet<Group>> list = getGroupsList(match, username);

        String end = "";

        StringBuilder temp = new StringBuilder();
        for (TreeSet<Group> set : list)
        {
            for (Group g : set)
            {
                if (g.prefix.trim().isEmpty())
                {
                    continue;
                }

                if (temp.length() == 0)
                {
                    temp.append(g.prefix);
                }
                else
                {
                    temp.insert(0, g.prefix + "&r");
                }
            }

            end = match.replaceFirst(temp.toString());
            temp = new StringBuilder();
        }

        return end;
    }

	public static String getGroupSuffixString(String username)
    {
        Matcher match = groupRegex.matcher(CoreConfig.groupSuffixFormat);

        ArrayList<TreeSet<Group>> list = getGroupsList(match, username);

        String end = "";

        StringBuilder temp = new StringBuilder();
        for (TreeSet<Group> set : list)
        {
            for (Group g : set)
            {
                if (g.suffix.trim().isEmpty())
                {
                    continue;
                }

                temp.append("&r").append(g.suffix);
            }

            end = match.replaceFirst(temp.toString());
            temp = new StringBuilder();
        }

        return end;
    }

    private static ArrayList<TreeSet<Group>> getGroupsList(Matcher match, String username)
    {
        ArrayList<TreeSet<Group>> list = new ArrayList<TreeSet<Group>>();

        String whole;
        String[] p;
        TreeSet<Group> set;
        while (match.find())
        {
            whole = match.group();
            whole = whole.replaceAll("\\{", "").replaceAll("\\}", "");
            p = whole.split("\\<\\:\\>", 2);
            if (p[0].equalsIgnoreCase("..."))
            {
                p[0] = null;
            }
            if (p[1].equalsIgnoreCase("..."))
            {
                p[1] = null;
            }

            set = SqlHelper.getGroupsForChat(p[0], p[1], username);
            if (set != null)
            {
                list.add(set);
            }
        }

        list = removeDuplicates(list);
        return list;
    }

    private static ArrayList<TreeSet<Group>> removeDuplicates(ArrayList<TreeSet<Group>> list)
    {
        HashSet<Group> used = new HashSet<Group>();

        for (TreeSet<Group> set : list)
        {
            for (Group g : used)
            {
                set.remove(g);
            }

            // add all the remaining...
            used.addAll(set);
        }

        return list;
    }

    public static String getFormattedPlayersOnline()
    {
        StringBuilder sb = new StringBuilder();
        for (Object fakePlayer : MinecraftServer.getServer().getConfigurationManager().playerEntityList)
        {
            EntityPlayer player = (EntityPlayer) fakePlayer;
            String name = player.username;
            if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).hasKey("nickname"))
            {
                name = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getString("nickname");
            }
            name = getGroupRankString(player.username) + ":" + name;
            sb.append(name + ", ");
        }
        return sb.toString().substring(0, sb.toString().lastIndexOf(","));
    }
}
