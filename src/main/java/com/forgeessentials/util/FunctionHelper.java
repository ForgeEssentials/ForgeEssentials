package com.forgeessentials.util;

import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.core.CoreConfig;
import com.forgeessentials.permissions.SqlHelper;
import com.forgeessentials.util.AreaSelector.Point;
import com.forgeessentials.util.AreaSelector.WarpPoint;
import com.google.common.base.Joiner;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
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
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        float var4 = 1.0F;
        float var5 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * var4;
        float var6 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * var4;
        double var7 = player.prevPosX + (player.posX - player.prevPosX) * var4;
        double var9 = player.prevPosY + (player.posY - player.prevPosY) * var4 + 1.62D - player.yOffset;
        double var11 = player.prevPosZ + (player.posZ - player.prevPosZ) * var4;
        Vec3 var13 = Vec3.createVectorHelper(var7, var9, var11);
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
        return player.worldObj.rayTraceBlocks(var13, var23, false);

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
        return MinecraftServer.getServer().func_152358_ax().func_152655_a(username).getId();
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
                throw new NumberFormatException(String.format("%s param was not recognized as number. Please try again.", pair[1]));
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
     *
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
     *
     * @param par0ArrayOfObj
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
            String name = player.getDisplayName();
            if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).hasKey("nickname"))
            {
                name = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getString("nickname");
            }
            name = getGroupRankString(player.getDisplayName()) + ":" + name;
            sb.append(name + ", ");
        }
        return sb.toString().substring(0, sb.toString().lastIndexOf(","));
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
}
