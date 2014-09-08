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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.core.CoreConfig;
import com.forgeessentials.permissions.SqlHelper;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.WarpPoint;
import com.google.common.base.Joiner;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public final class FunctionHelper {
    public static Pattern groupRegex = Pattern.compile("\\{[a-zA-Z0-9._]*\\<\\:\\>[a-zA-Z0-9._]*\\}");

    public static SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel("ForgeEssentials");
    // used for niceJoin method.
    private static Joiner joiner = Joiner.on(", ").skipNulls();

    public static Item AIR = Item.getItemFromBlock(Blocks.air);

    /**
     * Get player's looking spot.
     *
     * @param player
     * @param restrict Keep max distance to 5.
     * @return The position as a MovingObjectPosition
     * Null if not existent.
     */
    public static MovingObjectPosition getPlayerLookingSpot(EntityPlayer player, boolean restrict)
    {
        return getPlayerLookingSpot(player, restrict, 500);
    }

    /**
     * Get player's looking spot.
     *
     * @param player
     * @param restrict Keep max distance to 5.
     * @return The position as a MovingObjectPosition
     * Null if not existent.
     */
    public static MovingObjectPosition getPlayerLookingSpot(EntityPlayer player, boolean restrict, double maxDistance)
    {
		if (restrict) {
			if (player instanceof EntityPlayerMP)
				maxDistance = Math.min(maxDistance, ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance());
			else
				maxDistance = Math.min(maxDistance, 5);
		}
        Vec3 lookAt = player.getLook(1);
        Vec3 pos1 = player.getPosition(1).addVector(0, player.getEyeHeight(), 0);
        Vec3 pos2 = pos1.addVector(lookAt.xCoord * maxDistance, lookAt.yCoord * maxDistance, lookAt.zCoord * maxDistance);
        return player.worldObj.rayTraceBlocks(pos1, pos2);
    }

    /**
     * Gets a nice string with only needed elements.
     * Max time is weeks
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
            uptime += (minutes < 10 ? "0" : "") + minutes + " min ";
        }

        uptime += (seconds < 10 ? "0" : "") + seconds + " sec.";

        return uptime;
    }

    /**
     * DO NOT use this for commands
     *
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public static EntityPlayerMP getPlayerForUUID(UUID id)
    {
        List<EntityPlayerMP> possibles = new LinkedList<EntityPlayerMP>();
        ArrayList<EntityPlayerMP> temp = (ArrayList<EntityPlayerMP>) FMLCommonHandler.instance().getSidedDelegate().getServer()
                .getConfigurationManager().playerEntityList;
        for (EntityPlayerMP player : temp)
        {
            if (player.getGameProfile().getId().equals(id))
            {
                return player;
            }
        }
        if (possibles.size() == 1)
        {
            return possibles.get(0);
        }
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
            return getPlayerForUUID(getPlayerID(name));
        }
    }

    public static UUID getPlayerID(String username)
    {
    	GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152655_a(username);
        return profile == null ? null : profile.getId();
    }

    public static String getPlayerName(UUID playerID)
    {
        return MinecraftServer.getServer().func_152358_ax().func_152652_a(playerID).getName();
    }

    /**
     * Make new Array with everything except the 1st string.
     *
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
     *
     * @param world
     * @return The zoneID
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
     *
     * @return never NULL. always {0, -1}. Meta by default is -1.
     * @throws NumberFormatException the message is a formatted chat string.
     */
    public static List<Object> parseIdAndMetaFromString(String msg, boolean blocksOnly) throws NumberFormatException
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
        List returned = new ArrayList<Object>();
        returned.add(0, ID);
        returned.add(1, meta);
        return returned;
    }

    /**
     * please use your module dir!
     *
     * @return
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
     * Get tps per word.
     *
     * @param dimID
     * @return -1 if error
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
        {
            return text;
        }
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
        format = replaceAllIgnoreCase(format, "%red", EnumChatFormatting.RED.toString());
        format = replaceAllIgnoreCase(format, "%yellow", EnumChatFormatting.YELLOW.toString());
        format = replaceAllIgnoreCase(format, "%black", EnumChatFormatting.BLACK.toString());
        format = replaceAllIgnoreCase(format, "%darkblue", EnumChatFormatting.DARK_BLUE.toString());
        format = replaceAllIgnoreCase(format, "%darkgreen", EnumChatFormatting.DARK_GREEN.toString());
        format = replaceAllIgnoreCase(format, "%darkaqua", EnumChatFormatting.DARK_AQUA.toString());
        format = replaceAllIgnoreCase(format, "%darkred", EnumChatFormatting.DARK_RED.toString());
        format = replaceAllIgnoreCase(format, "%purple", EnumChatFormatting.DARK_PURPLE.toString());
        format = replaceAllIgnoreCase(format, "%gold", EnumChatFormatting.GOLD.toString());
        format = replaceAllIgnoreCase(format, "%grey", EnumChatFormatting.GRAY.toString());
        format = replaceAllIgnoreCase(format, "%darkgrey", EnumChatFormatting.DARK_GRAY.toString());
        format = replaceAllIgnoreCase(format, "%indigo", EnumChatFormatting.BLUE.toString());
        format = replaceAllIgnoreCase(format, "%green", EnumChatFormatting.GREEN.toString());
        format = replaceAllIgnoreCase(format, "%aqua", EnumChatFormatting.AQUA.toString());
        format = replaceAllIgnoreCase(format, "%pink", EnumChatFormatting.LIGHT_PURPLE.toString());
        format = replaceAllIgnoreCase(format, "%white", EnumChatFormatting.WHITE.toString());

        // replace MC formating
        format = replaceAllIgnoreCase(format, "%random", EnumChatFormatting.OBFUSCATED.toString());
        format = replaceAllIgnoreCase(format, "%bold", EnumChatFormatting.BOLD.toString());
        format = replaceAllIgnoreCase(format, "%strike", EnumChatFormatting.STRIKETHROUGH.toString());
        format = replaceAllIgnoreCase(format, "%underline", EnumChatFormatting.UNDERLINE.toString());
        format = replaceAllIgnoreCase(format, "%italics", EnumChatFormatting.ITALIC.toString());
        format = replaceAllIgnoreCase(format, "%reset", EnumChatFormatting.RESET.toString());

        return format;
    }

    /**
     * instWarp a player to a point. Please use TeleportCenter!
     *
     * @param player
     * @param p
     */
    public static void setPlayer(EntityPlayerMP player, WarpPoint p)
    {
        if (player.dimension != p.getDimension())
        {
            MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(player, p.getDimension());
        }
        player.playerNetServerHandler.setPlayerLocation(p.xd, p.yd, p.zd, p.yaw, p.pitch);
        player.prevPosX = player.posX = p.xd;
        player.prevPosY = player.posY = p.yd;
        player.prevPosZ = player.posZ = p.zd;
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

    /**
     * Join string[] to print to users. "str1, str2, str3, ..., strn"
     *
     * @return
     */
    public static String niceJoin(Object[] array)
    {
        return joiner.join(array);
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

                temp.append(g.getName());
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
            for (Group group : set)
            {
                if (group.getPrefix().trim().isEmpty())
                {
                    continue;
                }

                if (temp.length() == 0)
                {
                    temp.append(group.getPrefix());
                }
                else
                {
                    temp.insert(0, group.getPrefix() + "&r");
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
            for (Group group : set)
            {
                if (group.getSuffix().trim().isEmpty())
                {
                    continue;
                }

                temp.append("&r").append(group.getSuffix());
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

            // TODO: OLEE, fix getGroupsForChat (add groupPrefix to PlayerInfo)
            
//            set = SqlHelper.getGroupsForChat(p[0], p[1], username);
//            if (set != null)
//            {
//                list.add(set);
//            }
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
            String name = player.getDisplayName();
            if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).hasKey("nickname"))
            {
                name = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getString("nickname");
            }
            name = getGroupRankString(player.getDisplayName()) + ":" + name;
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
                        FileWriter fstream = new FileWriter(savefile);
                        BufferedWriter out = new BufferedWriter(fstream);
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
                        out.close();
                        fstream.close();
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
                    FileInputStream stream = new FileInputStream(file);
                    InputStreamReader streamReader = new InputStreamReader(stream);
                    BufferedReader reader = new BufferedReader(streamReader);
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
                    streamReader.close();
                    stream.close();
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
                    FileInputStream stream = new FileInputStream(file);
                    InputStreamReader streamReader = new InputStreamReader(stream);
                    BufferedReader reader = new BufferedReader(streamReader);
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
                    streamReader.close();
                    stream.close();
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
                    FileInputStream stream = new FileInputStream(file);
                    InputStreamReader streamReader = new InputStreamReader(stream);
                    BufferedReader reader = new BufferedReader(streamReader);
                    String line = reader.readLine();
                    while (line != null)
                    {
                        lines.add(line);
                        line = reader.readLine();
                    }
                    reader.close();
                    streamReader.close();
                    stream.close();

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
                    FileInputStream stream = new FileInputStream(file);
                    InputStreamReader streamReader = new InputStreamReader(stream);
                    BufferedReader reader = new BufferedReader(streamReader);
                    String line = reader.readLine();
                    while (line != null)
                    {
                        lines.add(line);
                        line = reader.readLine();
                    }
                    reader.close();
                    streamReader.close();
                    stream.close();

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
                        FileInputStream stream = new FileInputStream(file);
                        InputStreamReader streamReader = new InputStreamReader(stream);
                        BufferedReader reader = new BufferedReader(streamReader);
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
                        streamReader.close();
                        stream.close();
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
                        FileInputStream stream = new FileInputStream(file);
                        InputStreamReader streamReader = new InputStreamReader(stream);
                        BufferedReader reader = new BufferedReader(streamReader);
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
                        streamReader.close();
                        stream.close();
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
}
