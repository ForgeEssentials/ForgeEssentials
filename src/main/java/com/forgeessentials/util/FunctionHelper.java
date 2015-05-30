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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.server.CommandMessage;
import net.minecraft.entity.item.EntityItem;
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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.environment.CommandSetChecker;
import com.forgeessentials.core.environment.Environment;
import com.forgeessentials.core.misc.Translator;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;

public final class FunctionHelper
{

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
     * Try to parse long or return defaultValue on failure
     * 
     * @param value
     * @param defaultValue
     * @return parsed long or default value
     */
    public static long parseLongDefault(String value, long defaultValue)
    {
        if (value == null)
            return defaultValue;
        try
        {
            return Long.parseLong(value);
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

    public static double parseYLocation(ICommandSender sender, double relative, String value)
    {
        boolean isRelative = value.startsWith("~");
        if (isRelative && Double.isNaN(relative))
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] { Double.valueOf(relative) });
        double d1 = isRelative ? relative : 0.0D;
        if (!isRelative || value.length() > 1)
        {
            if (isRelative)
                value = value.substring(1);
            d1 += CommandBase.parseDouble(sender, value);
        }
        return d1;
    }

    /**
     * Try to parse the string as integer or return null if failed
     * 
     * @param value
     * @return
     */
    public static Integer tryParseInt(String value)
    {
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    /**
     * Try to parse the string as long or return null if failed
     * 
     * @param value
     * @return
     */
    public static Long tryParseLong(String value)
    {
        try
        {
            return Long.parseLong(value);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    /**
     * Try to parse the string as float or return null if failed
     * 
     * @param value
     * @return
     */
    public static Float tryParseFloat(String value)
    {
        try
        {
            return Float.parseFloat(value);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    /**
     * Try to parse the string as double or return null if failed
     * 
     * @param value
     * @return
     */
    public static Double tryParseDouble(String value)
    {
        try
        {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    /* ------------------------------------------------------------ */
    /* inventory stuff */

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

    /**
     * Give player the item stack or drop it if his inventory is full
     * 
     * @param player
     * @param item
     */
    public static void givePlayerItem(EntityPlayer player, ItemStack item)
    {
        EntityItem entityitem = player.dropPlayerItemWithRandomChoice(item, false);
        entityitem.delayBeforeCanPickup = 0;
        entityitem.func_145797_a(player.getCommandSenderName());
    }

    /* ------------------------------------------------------------ */

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
        for (int i = 0; i < h; i++)
        {
            Block block = world.getBlock(x, y + i, z);
            if (block.getMaterial().isSolid() || block.getMaterial().isLiquid())
                return false;
        }
        return true;
    }

    /**
     * Returns a free spot of height h in the world at the coordinates [x,z] near y. If the blocks at [x,y,z] are free,
     * it returns the next location that is on the ground. If the blocks at [x,y,z] are not free, it goes up until it
     * finds a free spot.
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
        if (isFree(world, x, y, z, h))
        {
            while (isFree(world, x, y - 1, z, h) && y > 0)
                y--;
        }
        else
        {
            y++;
            while (y + h < world.getHeight() && !isFree(world, x, y, z, h))
                y++;
        }
        if (y == 0)
            y = world.getHeight() - h;
        return y;
    }

    /**
     * Returns a free spot of height 2 in the world at the coordinates [x,z] near y. If the blocks at [x,y,z] are free,
     * it returns the next location that is on the ground. If the blocks at [x,y,z] are not free, it goes up until it
     * finds a free spot.
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

    public static WorldPoint placeInWorld(WorldPoint p)
    {
        return p.setY(placeInWorld(p.getWorld(), p.getX(), p.getY(), p.getZ(), 2));
    }

    public static void placeInWorld(EntityPlayer player)
    {
        WorldPoint p = placeInWorld(new WorldPoint(player));
        player.setPositionAndUpdate(p.getX() + 0.5, p.getX(), p.getX() + 0.5);
    }

    /* ------------------------------------------------------------ */

    /**
     * Apply potion effects to the player
     * 
     * @param player
     * @param effectString
     *            Comma separated list of id:duration:amplifier or id:duration tuples
     */
    public static void applyPotionEffects(EntityPlayer player, String effectString)
    {
        String[] effects = effectString.replaceAll("\\s", "").split(","); // example = 9:5:0
        for (String poisonEffect : effects)
        {
            String[] effectValues = poisonEffect.split(":");
            if (effectValues.length < 2)
            {
                OutputHandler.felog.warning("Too few arguments for potion effects");
            }
            else if (effectValues.length > 3)
            {
                OutputHandler.felog.warning("Too many arguments for potion effects");
            }
            else
            {
                try
                {
                    int potionID = Integer.parseInt(effectValues[0]);
                    int effectDuration = Integer.parseInt(effectValues[1]);
                    int amplifier = 0;
                    if (effectValues.length == 3)
                        amplifier = Integer.parseInt(effectValues[2]);
                    player.addPotionEffect(new net.minecraft.potion.PotionEffect(potionID, effectDuration * 20, amplifier));
                }
                catch (NumberFormatException e)
                {
                    OutputHandler.felog.warning("Invalid potion ID:duration:amplifier data.");
                }
            }
        }
    }

    /* ------------------------------------------------------------ */

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

    // ------------------------------------------------------------

    // TODO: Move these to a class like ForgeEssentials and make them configurable

    public static final long SECOND = 1;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    public static final long WEEK = 7 * DAY;

    /**
     * Gets a nice string with only needed elements. Max time is weeks
     *
     * @param time
     * @return Time in string format
     */
    public static String formatTimeDurationReadable(long time, boolean showSeconds)
    {
        long weeks = time / WEEK;
        time -= WEEK * weeks;
        long days = time / DAY;
        time -= DAY * days;
        long hours = time / HOUR;
        time -= HOUR * hours;
        long minutes = time / MINUTE;
        time -= MINUTE * minutes;
        long seconds = time / SECOND;

        StringBuilder sb = new StringBuilder();
        if (weeks != 0)
            sb.append(String.format("%d weeks ", weeks));
        if (days != 0)
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(String.format("%d days ", days));
        }
        if (hours != 0)
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(String.format("%d hours ", hours));
        }
        if (minutes != 0 || !showSeconds)
        {
            if (sb.length() > 0)
                if (!showSeconds)
                    sb.append("and ");
                else
                    sb.append(", ");
            sb.append(String.format("%d minutes ", minutes));
        }
        if (showSeconds)
        {
            if (sb.length() > 0)
                sb.append("and ");
            sb.append(String.format("%d seconds ", seconds));
        }

        sb.setLength(sb.length() - 1);
        return sb.toString();
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
                throw new NumberFormatException(Translator.format("%s param was not recognized as number. Please try again.", pair[1]));
            }
        }
        return new ImmutablePair<String, Integer>(ID, meta);
    }

    // ------------------------------------------------------------

    public static void replaceCommand(Class<CommandMessage> clazz, ICommand newCommand)
    {
        try
        {
            CommandHandler commandHandler = (CommandHandler) MinecraftServer.getServer().getCommandManager();
            Map<String, ICommand> commandMap = ReflectionHelper.getPrivateValue(CommandHandler.class, commandHandler, "commandMap", "a", "field_71562_a");
            Set<ICommand> commandSet = ReflectionHelper.getPrivateValue(CommandHandler.class, commandHandler, CommandSetChecker.FIELDNAME);
            for (Iterator<Entry<String, ICommand>> it = commandMap.entrySet().iterator(); it.hasNext();)
            {
                Entry<String, ICommand> command = it.next();
                if (clazz.isAssignableFrom(command.getValue().getClass()))
                {
                    commandSet.remove(command.getValue());
                    commandSet.add(newCommand);
                    command.setValue(newCommand);
                }
            }
        }
        catch (Exception e)
        {
            OutputHandler.felog.severe(String.format("Error replacing command /%s", clazz.getClass().getName()));
            e.printStackTrace();
        }
        if (newCommand instanceof ForgeEssentialsCommandBase)
            ((ForgeEssentialsCommandBase) newCommand).register();
    }

    public static void replaceCommand(ICommand oldCommand, ICommand newCommand)
    {
        try
        {
            CommandHandler commandHandler = (CommandHandler) MinecraftServer.getServer().getCommandManager();
            Map<String, ICommand> commandMap = ReflectionHelper.getPrivateValue(CommandHandler.class, commandHandler, "commandMap", "a", "field_71562_a");
            Set<ICommand> commandSet = ReflectionHelper.getPrivateValue(CommandHandler.class, commandHandler, CommandSetChecker.FIELDNAME);
            for (Iterator<Entry<String, ICommand>> it = commandMap.entrySet().iterator(); it.hasNext();)
            {
                Entry<String, ICommand> command = it.next();
                if (command.getValue() == oldCommand)
                {
                    commandSet.remove(command.getValue());
                    commandSet.add(newCommand);
                    command.setValue(newCommand);
                }
            }
        }
        catch (Exception e)
        {
            OutputHandler.felog.severe(String.format("Error replacing command /%s", oldCommand.getCommandName()));
            e.printStackTrace();
        }
        if (newCommand instanceof ForgeEssentialsCommandBase)
            ((ForgeEssentialsCommandBase) newCommand).register();
    }

    public static void replaceCommand(String command, ICommand newCommand)
    {
        ICommand oldCommand = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(command);
        if (oldCommand != null)
            replaceCommand(oldCommand, newCommand);
        else
            OutputHandler.felog.severe(String.format("Could not find command /%s to replace", command));
    }

    /**
     * Gets a type safe player list
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<EntityPlayerMP> getPlayerList()
    {
        return MinecraftServer.getServer().getConfigurationManager().playerEntityList;
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
     * 
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
     * @param text
     * @param search
     * @param replacement
     * @return
     */
    public static String replaceAllIgnoreCase(String text, String search, String replacement)
    {
        Pattern p = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find())
        {
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        m.appendTail(sb);
        return sb.toString();
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

}
