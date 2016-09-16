package com.forgeessentials.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import com.forgeessentials.core.environment.Environment;
import com.google.common.base.Throwables;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameData;

public abstract class Utils
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

    /* ------------------------------------------------------------ */

    /**
     * Drops the first element of the array
     *
     * @param array
     * @return
     */
    public static <T> T[] dropFirst(T[] array)
    {
        return Arrays.copyOfRange(array, 1, array.length);
    }

    /**
     * Returns working directory or minecraft data-directory on client side. <br>
     * <b>Please use module directory instead!</b>
     */
    public static File getBaseDir()
    {
        if (FMLCommonHandler.instance().getSide().isClient())
            return Minecraft.getMinecraft().mcDataDir;
        else
            return new File(".");
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
     * Gets a type safe player list
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<EntityPlayerMP> getPlayerList()
    {
        MinecraftServer mc = MinecraftServer.getServer();
        return mc == null || mc.getConfigurationManager() == null ? new ArrayList<>() : mc.getConfigurationManager().playerEntityList;
    }

    /**
     * Get tps per world.
     *
     * @param dimID
     * @return -1 if error
     */
    public static double getWorldTPS(int dimID)
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
            return 20;
        else
            return 1000 / tps;
    }

    /**
     * Server's ticks per second
     *
     * @return
     */
    public static double getTPS()
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        double tickSum = 0;
        for (int i = 0; i < server.tickTimeArray.length; ++i)
            tickSum += server.tickTimeArray[i];
        tickSum /= server.tickTimeArray.length;
        double tps = 1000000000 / tickSum;
        return tps; // tps > 20 ? 20 : tps;
    }

    public static WorldServer getOverworld()
    {
        return MinecraftServer.getServer().worldServers[0];
    }

    public static long getOverworldTime()
    {
        return MinecraftServer.getServer().worldServers[0].getWorldInfo().getWorldTime();
    }

    public static boolean isServerRunning()
    {
        return MinecraftServer.getServer() != null && MinecraftServer.getServer().isServerRunning();
    }

    public static boolean isOnlineMode()
    {
        return FMLCommonHandler.instance().getSidedDelegate().getServer().isServerInOnlineMode();
    }

    public static boolean getMojangServerStatus()
    {
        final String MC_SERVER = "http://session.minecraft.net/game/checkserver.jsp";
        final String ONLINE = "NOT YET";
        try
        {
            URL url = new URL(MC_SERVER);
            try (BufferedReader stream = new BufferedReader(new InputStreamReader(url.openStream())))
            {
                String input = stream.readLine();
                return ONLINE.equals(input);
            }
        }
        catch (MalformedURLException e)
        {
            Throwables.propagate(e);
            return false;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static void copyNbt(NBTTagCompound nbt, NBTTagCompound data)
    {
        // Clear old data
        for (String key : new HashSet<String>(nbt.func_150296_c()))
            nbt.removeTag(key);

        // Write new data
        for (String key : (Set<String>) data.func_150296_c())
            nbt.setTag(key, data.getTag(key));
    }

    public static String getItemName(Item item)
    {
        return GameData.getItemRegistry().getNameForObject(item).toString();
    }

    public static String getBlockName(Block block)
    {
        return GameData.getBlockRegistry().getNameForObject(block);
    }

    public static String getItemPermission(Item item)
    {
        return GameData.getItemRegistry().getNameForObject(item).replace(':', '.').replace(' ', '_');
    }

    public static String getBlockPermission(Block block)
    {
        return GameData.getBlockRegistry().getNameForObject(block).replace(':', '.').replace(' ', '_');
    }
}
