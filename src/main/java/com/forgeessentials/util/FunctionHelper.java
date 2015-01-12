package com.forgeessentials.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fe.server.CommandHandlerForge;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.environment.Environment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public final class FunctionHelper {

	public static SimpleNetworkWrapper netHandler;

    public static final EventBus FE_INTERNAL_EVENTBUS = APIRegistry.getFEEventBus();

	/**
	 * Try to parse integer or return defaultValue on failure
	 * 
	 * @param value
	 * @param defaultValue
	 * @return parsed integer or default value
	 */
	public static int parseIntDefault(String value, int defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Try to parse double or return defaultValue on failure
	 * 
	 * @param value
	 * @param defaultValue
	 * @return parsed double or default value
	 */
	public static double parseDoubleDefault(String value, double defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			return Double.parseDouble(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	// ------------------------------------------------------------
	
    /**
     * Swaps the player's inventory with the one provided and returns the old.
     * 
     * @param player
     * @param newItems
     * @return
     */
    public static List<ItemStack> swapInventory(EntityPlayerMP player, List<ItemStack> newItems)
    {
        List<ItemStack> oldItems = new ArrayList<>();
        for (int slotIdx = 0; slotIdx < player.inventory.getSizeInventory(); slotIdx++)
        {
            oldItems.add(player.inventory.getStackInSlot(slotIdx));
            if (newItems != null && slotIdx < newItems.size())
                player.inventory.setInventorySlotContents(slotIdx, newItems.get(slotIdx));
            else
                player.inventory.setInventorySlotContents(slotIdx, null);
        }
        return oldItems;
    }

    // ------------------------------------------------------------

	/**
	 * Checks if the blocks from [x,y,z] to [x,y+h-1,z] are either air or replacable
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param h
	 * @return y value
	 */
	public static boolean isFree(World world, int x, int y, int z, int h)
	{
		for (int i = 0; i < h; i++) {
			Block block = world.getBlock(x, y + i, z);
			if (block.getMaterial().isSolid() || block.getMaterial().isLiquid())
				return false;
		}
		return true;
	}

	/**
	 * Returns a free spot of height h in the world at the coordinates [x,z] near y.
	 * If the blocks at [x,y,z] are free, it returns the next location that is on the ground.
	 * If the blocks at [x,y,z] are not free, it goes up until it finds a free spot.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param h
	 * @return y value
	 */
	public static int placeInWorld(World world, int x, int y, int z, int h)
	{
		if (isFree(world, x, y, z, h)) {
			while (isFree(world, x, y - 1, z, h) && y > 0)
				y--;
		} else {
			y++;
			while (y + h < world.getHeight() && !isFree(world, x, y, z, h))
				y++;
		}
		if (y == 0)
			y = world.getHeight() - h;
		return y;
	}
	
    /**
     * Returns a free spot of height 2 in the world at the coordinates [x,z] near y.
     * If the blocks at [x,y,z] are free, it returns the next location that is on the ground.
     * If the blocks at [x,y,z] are not free, it goes up until it finds a free spot.
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @return y value
     */
    public static int placeInWorld(World world, int x, int y, int z)
    {
        return placeInWorld(world, x, y, z, 2);
    }

    /**
     * Get player's looking-at spot.
     *
     * @param player
     * @return The position as a MovingObjectPosition Null if not existent.
     */
    public static MovingObjectPosition getPlayerLookingSpot(EntityPlayer player)
    {
        if (player instanceof EntityPlayerMP)
            return getPlayerLookingSpot(player, ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance());
        else
            return getPlayerLookingSpot(player, 5);
    }

    /**
     * Get player's looking spot.
     *
     * @param player
     * @param maxDistance
     *            Keep max distance to 5.
     * @return The position as a MovingObjectPosition Null if not existent.
     */
    public static MovingObjectPosition getPlayerLookingSpot(EntityPlayer player, double maxDistance)
    {
        Vec3 lookAt = player.getLook(1);
        Vec3 playerPos = Vec3.createVectorHelper(player.posX, player.posY + (player.getEyeHeight() - player.getDefaultEyeHeight()), player.posZ);
        Vec3 pos1 = playerPos.addVector(0, player.getEyeHeight(), 0);
        Vec3 pos2 = pos1.addVector(lookAt.xCoord * maxDistance, lookAt.yCoord * maxDistance, lookAt.zCoord * maxDistance);
        return player.worldObj.rayTraceBlocks(pos1, pos2);
    }

	/**
	 * Gets a nice string with only needed elements. Max time is weeks
	 *
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
			uptime += (minutes < 10 ? "0" : "") + minutes + " minutes ";
		}

		uptime += (seconds < 10 ? "0" : "") + seconds + " seconds";

		return uptime;
	}

	/**
	 * Make new Array with everything except the 1st string.
	 *
	 * @param par0ArrayOfStr
	 *            Old array
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
	 * Does NOT check if its a valid BlockID and stuff.. this may be used for items.
	 *
	 * @return never NULL. always {0, -1}. Meta by default is -1.
	 * @throws NumberFormatException
	 *             the message is a formatted chat string.
	 */
	public static Pair<String, Integer> parseIdAndMetaFromString(String msg, boolean blocksOnly) throws NumberFormatException
	{
		String ID = null;
		int meta = -1;

		// must be the ID:Meta format
		if (msg.contains(":"))
		{
			String[] pair = msg.split(":", 3);

			ID = pair[0] + ":" + pair[1];

			try
			{
				meta = Integer.parseInt(pair[2]);
			}
			catch (NumberFormatException e)
			{
				throw new NumberFormatException(String.format("%s param was not recognized as number. Please try again.", pair[1]));
			}
		}
		return new ImmutablePair<String, Integer>(ID, meta);
	}

	/**
	 * Returns working directory or minecraft data-directory on client side. <br>
	 * <b>Please use module directory instead!</b>
	 */
	public static File getBaseDir()
	{
		if (FMLCommonHandler.instance().getSide().isClient())
		{
			return Minecraft.getMinecraft().mcDataDir;
		}
		else
		{
			return new File(".");
		}
	}

    /**
     * Get's the directory where the world is saved
     * @return
     */
    public static File getWorldPath()
    {
        if (Environment.isClient())
            return new File(MinecraftServer.getServer().getFile("saves"), MinecraftServer.getServer().getFolderName());
        else
            return MinecraftServer.getServer().getFile(MinecraftServer.getServer().getFolderName());
    }
    
	/**
	 * Get tps per world.
	 *
	 * @param dimID
	 * @return -1 if error
	 */
	@SuppressWarnings("unused")
    private static double getTPS(int dimID)
	{
		try
		{
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

			long sum = 0L;
			long[] ticks = server.worldTickTimes.get(dimID);
			for (int i = 0; i < ticks.length; ++i)
			{
				sum += ticks[i];
			}

			double tps = (double) sum / (double) ticks.length * 1.0E-6D;

			if (tps < 50)
			{
				return 20;
			}
			else
			{
				return 1000 / tps;
			}
		}
		catch (Exception e)
		{
			return -1;
		}
	}

	/**
	 * Get tps.
	 *
	 * @return -1 if error
	 */
	public static double getTPS()
	{
		try
		{
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

			long tickSum = 0L;
			long[] ticks = server.tickTimeArray;
			for (int i = 0; i < ticks.length; ++i)
			{
				long var7 = ticks[i];
				tickSum += var7;
			}

			double tps = (double) tickSum / (double) ticks.length * 1.0E-6D;

			if (tps < 50)
			{
				return 20;
			}
			else
			{
				return 1000 / tps;
			}
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

        return String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
	}

	/**
	 * @param text
	 * @param search
	 * @param replacement
	 * @return
	 */
	public static String replaceAllIgnoreCase(String text, String search, String replacement)
	{
		return Pattern.compile("(?i)" + Pattern.quote(search)).matcher(text).quoteReplacement(replacement);
	}

	/**
	 * Uses & as identifier
	 *
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
	 *
	 * @param message
	 * @return
	 */
	public static String format(String message)
	{
		message = replaceAllIgnoreCase(message, "%smile", "\u263A");
		message = replaceAllIgnoreCase(message, "%copyrighted", "\u00A9");
		message = replaceAllIgnoreCase(message, "%registered", "\u00AE");
		message = replaceAllIgnoreCase(message, "%diamond", "\u2662");
		message = replaceAllIgnoreCase(message, "%spade", "\u2664");
		message = replaceAllIgnoreCase(message, "%club", "\u2667");
		message = replaceAllIgnoreCase(message, "%heart", "\u2661");
		message = replaceAllIgnoreCase(message, "%female", "\u2640");
		message = replaceAllIgnoreCase(message, "%male", "\u2642");

		// replace colors
		message = replaceAllIgnoreCase(message, "%red", EnumChatFormatting.RED.toString());
		message = replaceAllIgnoreCase(message, "%yellow", EnumChatFormatting.YELLOW.toString());
		message = replaceAllIgnoreCase(message, "%black", EnumChatFormatting.BLACK.toString());
		message = replaceAllIgnoreCase(message, "%darkblue", EnumChatFormatting.DARK_BLUE.toString());
		message = replaceAllIgnoreCase(message, "%darkgreen", EnumChatFormatting.DARK_GREEN.toString());
		message = replaceAllIgnoreCase(message, "%darkaqua", EnumChatFormatting.DARK_AQUA.toString());
		message = replaceAllIgnoreCase(message, "%darkred", EnumChatFormatting.DARK_RED.toString());
		message = replaceAllIgnoreCase(message, "%purple", EnumChatFormatting.DARK_PURPLE.toString());
		message = replaceAllIgnoreCase(message, "%gold", EnumChatFormatting.GOLD.toString());
		message = replaceAllIgnoreCase(message, "%grey", EnumChatFormatting.GRAY.toString());
		message = replaceAllIgnoreCase(message, "%darkgrey", EnumChatFormatting.DARK_GRAY.toString());
		message = replaceAllIgnoreCase(message, "%indigo", EnumChatFormatting.BLUE.toString());
		message = replaceAllIgnoreCase(message, "%green", EnumChatFormatting.GREEN.toString());
		message = replaceAllIgnoreCase(message, "%aqua", EnumChatFormatting.AQUA.toString());
		message = replaceAllIgnoreCase(message, "%pink", EnumChatFormatting.LIGHT_PURPLE.toString());
		message = replaceAllIgnoreCase(message, "%white", EnumChatFormatting.WHITE.toString());

		// replace MC formating
		message = replaceAllIgnoreCase(message, "%random", EnumChatFormatting.OBFUSCATED.toString());
		message = replaceAllIgnoreCase(message, "%bold", EnumChatFormatting.BOLD.toString());
		message = replaceAllIgnoreCase(message, "%strike", EnumChatFormatting.STRIKETHROUGH.toString());
		message = replaceAllIgnoreCase(message, "%underline", EnumChatFormatting.UNDERLINE.toString());
		message = replaceAllIgnoreCase(message, "%italics", EnumChatFormatting.ITALIC.toString());
		message = replaceAllIgnoreCase(message, "%reset", EnumChatFormatting.RESET.toString());

		return message;
	}

	/**
	 * instWarp a player to a point. Please use TeleportCenter!
	 *
	 * @param player
	 * @param p
	 */
	public static void teleportPlayer(EntityPlayerMP player, WarpPoint p)
	{
		if (player.dimension != p.getDimension())
		{
			MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(player, p.getDimension());
		}
		player.playerNetServerHandler.setPlayerLocation(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch());
		player.prevPosX = player.posX = p.getX();
		player.prevPosY = player.posY = p.getY();
		player.prevPosZ = player.posZ = p.getZ();
	}

	/**
	 * instWarp a player to a point. Please use TeleportCenter!
	 *
	 * @param player
	 * @param world
	 */
	public static void setPlayer(EntityPlayerMP player, Point point, World world)
	{
		if (player.dimension != world.provider.dimensionId)
		{
			MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(player, world.provider.dimensionId);
		}
		double x = point.getX(), y = point.getY(), z = point.getZ();
		x = x < 0 ? x - 0.5 : x + 0.5;
		z = z < 0 ? z - 0.5 : z + 0.5;
		player.playerNetServerHandler.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
	}

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

	public static String getPlayerPrefixSuffix(UserIdent player, boolean isSuffix)
	{
		String fix = APIRegistry.perms.getServerZone().getPlayerPermission(player, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
		if (fix == null)
			return "";
		return fix;
	}

    public static String getPlayerGroupPrefixSuffix(UserIdent player, boolean isSuffix)
    {
        for (GroupEntry group : APIRegistry.perms.getPlayerGroups(player))
        {
            String s = APIRegistry.perms.getServerZone().getGroupPermission(group.getGroup(), isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
            if (s != null)
                return s;
        }
        return "";
    }

	public static String getFormattedPlayersOnline()
	{
		StringBuilder sb = new StringBuilder();
		for (Object fakePlayer : MinecraftServer.getServer().getConfigurationManager().playerEntityList)
		{
			EntityPlayer player = (EntityPlayer) fakePlayer;
			String name = player.getDisplayName();
			if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).hasKey("nickname"))
			{
				name = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getString("nickname");
			}
			// name = getGroupRankString(player.getDisplayName()) + ":" + name;
			sb.append(name + ", ");
		}
		if (sb.length() == 0)
			return "";
		return sb.toString().substring(0, sb.length() - 2);
	}

	public static void saveBookToFile(ItemStack book, File savefolder)
	{
		NBTTagList pages;
		String filename = "";
		if (book != null)
		{
			if (book.hasTagCompound())
			{
				if (book.getTagCompound().hasKey("title") && book.getTagCompound().hasKey("pages"))
				{
					filename = book.getTagCompound().getString("title") + ".txt";
					pages = (NBTTagList) book.getTagCompound().getTag("pages");
					File savefile = new File(savefolder, filename);
					if (savefile.exists())
					{
						savefile.delete();
					}
					try
					{
						savefile.createNewFile();
						try (BufferedWriter out = new BufferedWriter(new FileWriter(savefile)))
                        {
    						for (int c = 0; c < pages.tagCount(); c++)
    						{
    							String line = pages.getCompoundTagAt(c).toString();
    							while (line.contains("\n"))
    							{
    								out.write(line.substring(0, line.indexOf("\n")));
    								out.newLine();
    								line = line.substring(line.indexOf("\n") + 1);
    							}
    							if (line.length() > 0)
    							{
    								out.write(line);
    							}
    						}
                        }
					}
					catch (Exception e)
					{
						OutputHandler.felog.info("Something went wrong...");
					}
				}
			}
		}
	}

	public static void getBookFromFile(EntityPlayer player, File file)
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList pages = new NBTTagList();

		HashMap<String, String> map = new HashMap<String, String>();
		if (file.isFile())
		{
			if (file.getName().contains(".txt"))
			{
				List<String> lines = new ArrayList<String>();
				try
				{
					lines.add(EnumChatFormatting.GREEN + "START" + EnumChatFormatting.BLACK);
					lines.add("");
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file))))
					{
    					String line = reader.readLine();
    					while (line != null)
    					{
    						while (line.length() > 21)
    						{
    							lines.add(line.substring(0, 20));
    							line = line.substring(20);
    						}
    						lines.add(line);
    						line = reader.readLine();
    					}
    					reader.close();
					}
					lines.add("");
					lines.add(EnumChatFormatting.RED + "END" + EnumChatFormatting.BLACK);

				}
				catch (Exception e)
				{
					OutputHandler.felog.warning("Error reading script: " + file.getName());
				}
				int part = 0;
				int parts = lines.size() / 10 + 1;
				String filename = file.getName().replaceAll(".txt", "");
				if (filename.length() > 13)
				{
					filename = filename.substring(0, 10) + "...";
				}
				while (lines.size() != 0)
				{
					part++;
					String temp = "";
					for (int i = 0; i < 10 && lines.size() > 0; i++)
					{
						temp += lines.get(0) + "\n";
						lines.remove(0);
					}
					map.put(EnumChatFormatting.GOLD + " File: " + EnumChatFormatting.GRAY + filename + EnumChatFormatting.DARK_GRAY + "\nPart " + part + " of "
							+ parts + EnumChatFormatting.BLACK + "\n\n", temp);
				}
			}
		}

		SortedSet<String> keys = new TreeSet<String>(map.keySet());
		for (String name : keys)
		{
			pages.appendTag(new NBTTagString(name + map.get(name)));
		}

		tag.setString("author", "ForgeEssentials");
		tag.setString("title", file.getName().replace(".txt", ""));
		tag.setTag("pages", pages);

		ItemStack is = new ItemStack(Items.written_book);
		is.setTagCompound(tag);
		player.inventory.addItemStackToInventory(is);
	}

	public static void getBookFromFile(EntityPlayer player, File file, String title)
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList pages = new NBTTagList();

		HashMap<String, String> map = new HashMap<String, String>();
		if (file.isFile())
		{
			if (file.getName().contains(".txt"))
			{
				List<String> lines = new ArrayList<String>();
				try
				{
					lines.add(EnumChatFormatting.GREEN + "START" + EnumChatFormatting.BLACK);
					lines.add("");
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file))))
                    {
    					String line = reader.readLine();
    					while (line != null)
    					{
    						while (line.length() > 21)
    						{
    							lines.add(line.substring(0, 20));
    							line = line.substring(20);
    						}
    						lines.add(line);
    						line = reader.readLine();
    					}
                    }
					lines.add("");
					lines.add(EnumChatFormatting.RED + "END" + EnumChatFormatting.BLACK);

				}
				catch (Exception e)
				{
					OutputHandler.felog.warning("Error reading script: " + file.getName());
				}
				int part = 0;
				int parts = lines.size() / 10 + 1;
				String filename = file.getName().replaceAll(".txt", "");
				if (filename.length() > 13)
				{
					filename = filename.substring(0, 10) + "...";
				}
				while (lines.size() != 0)
				{
					part++;
					String temp = "";
					for (int i = 0; i < 10 && lines.size() > 0; i++)
					{
						temp += lines.get(0) + "\n";
						lines.remove(0);
					}
					map.put(EnumChatFormatting.GOLD + " File: " + EnumChatFormatting.GRAY + filename + EnumChatFormatting.DARK_GRAY + "\nPart " + part + " of "
							+ parts + EnumChatFormatting.BLACK + "\n\n", temp);
				}
			}
		}

		SortedSet<String> keys = new TreeSet<String>(map.keySet());
		for (String name : keys)
		{
			pages.appendTag(new NBTTagString(name + map.get(name)));
		}

		tag.setString("author", "ForgeEssentials");
		tag.setString("title", title);
		tag.setTag("pages", pages);

		ItemStack is = new ItemStack(Items.written_book);
		is.setTagCompound(tag);
		player.inventory.addItemStackToInventory(is);
	}

	public static void getBookFromFileUnformatted(EntityPlayer player, File file)
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList pages = new NBTTagList();

		HashMap<String, String> map = new HashMap<String, String>();
		if (file.isFile())
		{
			if (file.getName().contains(".txt"))
			{
				List<String> lines = new ArrayList<String>();
				try
				{
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file))))
                    {
                        String line = reader.readLine();
                        while (line != null)
                        {
                            lines.add(line);
                            line = reader.readLine();
                        }
                    }
				}
				catch (Exception e)
				{
					OutputHandler.felog.warning("Error reading book: " + file.getName());
				}
				while (lines.size() != 0)
				{
					String temp = "";
					for (int i = 0; i < 10 && lines.size() > 0; i++)
					{
						temp += lines.get(0) + "\n";
						lines.remove(0);
					}
					map.put("", temp);
				}
			}
		}

		SortedSet<String> keys = new TreeSet<String>(map.keySet());
		for (String name : keys)
		{
			pages.appendTag(new NBTTagString(name + map.get(name)));
		}

		tag.setString("author", "ForgeEssentials");
		tag.setString("title", file.getName().replace(".txt", ""));
		tag.setTag("pages", pages);

		ItemStack is = new ItemStack(Items.written_book);
		is.setTagCompound(tag);
		player.inventory.addItemStackToInventory(is);
	}

	public static void getBookFromFileUnformatted(EntityPlayer player, File file, String title)
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList pages = new NBTTagList();

		HashMap<String, String> map = new HashMap<String, String>();
		if (file.isFile())
		{
			if (file.getName().contains(".txt"))
			{
				List<String> lines = new ArrayList<String>();
				try
				{
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file))))
                    {
                        String line = reader.readLine();
                        while (line != null)
                        {
                            lines.add(line);
                            line = reader.readLine();
                        }
                    }
				}
				catch (Exception e)
				{
					OutputHandler.felog.warning("Error reading book: " + file.getName());
				}
				while (lines.size() != 0)
				{
					String temp = "";
					for (int i = 0; i < 10 && lines.size() > 0; i++)
					{
						temp += lines.get(0) + "\n";
						lines.remove(0);
					}
					map.put("", temp);
				}
			}
		}

		SortedSet<String> keys = new TreeSet<String>(map.keySet());
		for (String name : keys)
		{
			pages.appendTag(new NBTTagString(name + map.get(name)));
		}

		tag.setString("author", "ForgeEssentials");
		tag.setString("title", title);
		tag.setTag("pages", pages);

		ItemStack is = new ItemStack(Items.written_book);
		is.setTagCompound(tag);
		player.inventory.addItemStackToInventory(is);
	}

	public static void getBookFromFolder(EntityPlayer player, File folder)
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList pages = new NBTTagList();

		HashMap<String, String> map = new HashMap<String, String>();

		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles)
		{
			if (file.isFile())
			{
				if (file.getName().contains(".txt"))
				{
					List<String> lines = new ArrayList<String>();
					try
					{
						lines.add(EnumChatFormatting.GREEN + "START" + EnumChatFormatting.BLACK);
						lines.add("");
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file))))
                        {
                            String line = reader.readLine();
                            while (line != null)
                            {
                                while (line.length() > 21)
                                {
                                    lines.add(line.substring(0, 20));
                                    line = line.substring(20);
                                }
                                lines.add(line);
                                line = reader.readLine();
                            }
                        }
						lines.add("");
						lines.add(EnumChatFormatting.RED + "END" + EnumChatFormatting.BLACK);

					}
					catch (Exception e)
					{
						OutputHandler.felog.warning("Error reading script: " + file.getName());
					}
					int part = 0;
					int parts = lines.size() / 10 + 1;
					String filename = file.getName().replaceAll(".txt", "");
					if (filename.length() > 13)
					{
						filename = filename.substring(0, 10) + "...";
					}
					while (lines.size() != 0)
					{
						part++;
						String temp = "";
						for (int i = 0; i < 10 && lines.size() > 0; i++)
						{
							temp += lines.get(0) + "\n";
							lines.remove(0);
						}
						map.put(EnumChatFormatting.GOLD + " File: " + EnumChatFormatting.GRAY + filename + EnumChatFormatting.DARK_GRAY + "\nPart " + part
								+ " of " + parts + EnumChatFormatting.BLACK + "\n\n", temp);
					}
				}
			}
		}

		SortedSet<String> keys = new TreeSet<String>(map.keySet());
		for (String name : keys)
		{
			pages.appendTag(new NBTTagString(name + map.get(name)));
		}

		tag.setString("author", "ForgeEssentials");
		tag.setString("title", folder.getName());
		tag.setTag("pages", pages);

		ItemStack is = new ItemStack(Items.written_book);
		is.setTagCompound(tag);
		player.inventory.addItemStackToInventory(is);
	}

	public static void getBookFromFolder(EntityPlayer player, File folder, String title)
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList pages = new NBTTagList();

		HashMap<String, String> map = new HashMap<String, String>();

		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles)
		{
			if (file.isFile())
			{
				if (file.getName().contains(".txt"))
				{
					List<String> lines = new ArrayList<String>();
					try
					{
						lines.add(EnumChatFormatting.GREEN + "START" + EnumChatFormatting.BLACK);
						lines.add("");
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file))))
                        {
                            String line = reader.readLine();
                            while (line != null)
                            {
                                while (line.length() > 21)
                                {
                                    lines.add(line.substring(0, 20));
                                    line = line.substring(20);
                                }
                                lines.add(line);
                                line = reader.readLine();
                            }
                        }
						lines.add("");
						lines.add(EnumChatFormatting.RED + "END" + EnumChatFormatting.BLACK);

					}
					catch (Exception e)
					{
						OutputHandler.felog.warning("Error reading script: " + file.getName());
					}
					int part = 0;
					int parts = lines.size() / 10 + 1;
					String filename = file.getName().replaceAll(".txt", "");
					if (filename.length() > 13)
					{
						filename = filename.substring(0, 10) + "...";
					}
					while (lines.size() != 0)
					{
						part++;
						String temp = "";
						for (int i = 0; i < 10 && lines.size() > 0; i++)
						{
							temp += lines.get(0) + "\n";
							lines.remove(0);
						}
						map.put(EnumChatFormatting.GOLD + " File: " + EnumChatFormatting.GRAY + filename + EnumChatFormatting.DARK_GRAY + "\nPart " + part
								+ " of " + parts + EnumChatFormatting.BLACK + "\n\n", temp);
					}
				}
			}
		}

		SortedSet<String> keys = new TreeSet<String>(map.keySet());
		for (String name : keys)
		{
			pages.appendTag(new NBTTagString(name + map.get(name)));
		}

		tag.setString("author", "ForgeEssentials");
		tag.setString("title", title);
		tag.setTag("pages", pages);

		ItemStack is = new ItemStack(Items.written_book);
		is.setTagCompound(tag);
		player.inventory.addItemStackToInventory(is);
	}

	public static JsonObject toJSON(ItemStack stack, Boolean listEnch) throws JsonParseException
	{
		JsonObject data = new JsonObject();
		if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("display") && stack.stackTagCompound.getCompoundTag("display").hasKey("Name"))
		{
			data.add("item", new JsonPrimitive(stack.getDisplayName().replaceAll("item.", "").replaceAll("tile.", "")));
		}
		if (stack.stackSize != 1)
		{
			data.add("amount", new JsonPrimitive("" + stack.stackSize));
		}
		data.add("id", new JsonPrimitive("" + stack.getUnlocalizedName()));
		if (stack.getItemDamage() != 0)
		{
			data.add("dam", new JsonPrimitive("" + stack.getItemDamage()));
		}
		data.add("name", new JsonPrimitive(stack.getDisplayName()));

		if (listEnch)
		{
			JsonArray tempArgs = new JsonArray();
			NBTTagList var10 = stack.getEnchantmentTagList();
			if (var10 != null)
			{
				for (int var7 = 0; var7 < var10.tagCount(); ++var7)
				{
					short var8 = (var10.getCompoundTagAt(var7)).getShort("id");
					short var9 = (var10.getCompoundTagAt(var7)).getShort("lvl");

					if (Enchantment.enchantmentsList[var8] != null)
					{
						tempArgs.add(new JsonPrimitive(Enchantment.enchantmentsList[var8].getTranslatedName(var9)));
					}
				}
				data.add("ench", tempArgs);
			}
		}

		return data;
	}

    public static void registerServerCommand(ForgeEssentialsCommandBase command)
    {
        if (command.getPermissionNode() != null && command.getDefaultPermission() != null)
        {
            CommandHandlerForge.registerCommand(command, command.getPermissionNode(), command.getDefaultPermission());
        }
        else
        {
            ((CommandHandler) MinecraftServer.getServer().getCommandManager()).registerCommand(command);
        }
    }

}
