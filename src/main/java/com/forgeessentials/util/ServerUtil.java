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
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.environment.Environment;

public abstract class ServerUtil
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

    public static double parseYLocation(CommandSource sender, double relative, String value) throws CommandException
    {
        boolean isRelative = value.startsWith("~");
        if (isRelative && Double.isNaN(relative))
            throw new NumberFormatException("commands.generic.num.invalid" + Double.valueOf(relative));
        double d1 = isRelative ? relative : 0.0D;
        if (!isRelative || value.length() > 1)
        {
            if (isRelative)
                value = value.substring(1);
            d1 += ForgeEssentialsCommandBuilder.parseDouble(value);
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

    /* ------------------------------------------------------------ */

    /**
     * Returns working directory or minecraft data-directory on client side. <br>
     * <b>Please use module directory instead!</b>
     */
    @SuppressWarnings("resource")
    public static File getBaseDir()
    {
        if (ServerLifecycleHooks.getCurrentServer().isSingleplayer())
            return Minecraft.getInstance().gameDirectory;
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
            return new File(ServerLifecycleHooks.getCurrentServer().getFile("saves"), ServerLifecycleHooks.getCurrentServer().getWorldData().getLevelName());
        else
            return new File(ServerLifecycleHooks.getCurrentServer().getServerDirectory(),"world");
    }

    /* ------------------------------------------------------------ */

    /**
     * Gets a type safe player list
     * 
     * @return
     */
    public static List<ServerPlayerEntity> getPlayerList()
    {
        MinecraftServer mc = ServerLifecycleHooks.getCurrentServer();
        return mc == null || mc.getPlayerList() == null ? new ArrayList<>() : mc.getPlayerList().getPlayers();
    }

    /**
     * Get tps per world.
     *
     * @param dimID
     * @return -1 if error
     */
    public static double getWorldTPS(RegistryKey<World> World)
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        long sum = 0L;
        long[] ticks = server.getTickTime(World);
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
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        final double meanTickTime = mean(server.tickTimes) * 1.0E-6D;
        return Math.min(1000 / meanTickTime, 20);// tps > 20 ? 20 : tps;
    }

    private static long mean(final long[] values)
    {
        long sum = 0;
        for (final long v : values)
        {
            sum += v;
        }
        return sum / values.length;
    }

    public static ServerWorld getOverworld()
    {
        return ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD);
    }

    public static long getOverworldTime()
    {
        return ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDayTime();
    }

    public static boolean isServerRunning()
    {
        return ServerLifecycleHooks.getCurrentServer() != null && ServerLifecycleHooks.getCurrentServer().isRunning();
    }

    public static boolean isOnlineMode()
    {
        return ServerLifecycleHooks.getCurrentServer().usesAuthentication();
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
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            return false;
        }
    }

    public static void copyNbt(CompoundNBT nbt, CompoundNBT data)
    {
        // Clear old data
        for (String key : new HashSet<String>(nbt.getAllKeys()))
            nbt.remove(key);

        // Write new data
        for (String key : (Set<String>) data.getAllKeys())
            nbt.put(key, data.get(key));
    }

    /* ------------------------------------------------------------ */

    public static String getItemName(Item item)
    {
        return ForgeRegistries.ITEMS.getKey(item).toString();
    }

    public static String getItemPermission(Item item)
    {
        ResourceLocation loc = (ResourceLocation) ForgeRegistries.ITEMS.getKey(item);
        return (loc.getNamespace() + '.' + loc.getPath()).replace(' ', '_');
    }

    public static String getBlockName(Block block)
    {
        Object o = ForgeRegistries.BLOCKS.getKey(block).toString();
        if (o instanceof ResourceLocation)
        {
            ResourceLocation rl = (ResourceLocation) o;
            return rl.getPath();
        }
        else
        {
            return (String) o;
        }
    }

    public static String getBlockPermission(Block block)
    {
        ResourceLocation loc = (ResourceLocation) ForgeRegistries.BLOCKS.getKey(block);
        return (loc.getNamespace() + '.' + loc.getPath()).replace(' ', '_');
    }

    public static ServerWorld getWorldFromString(String dim)
    {
        ServerWorld world = ServerLifecycleHooks.getCurrentServer().getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dim)));
        return world;
    }

    /* ------------------------------------------------------------ */
/*
    public static void replaceCommand(Class<CommandMessage> clazz, ICommand newCommand)
    {
        try
        {
            CommandHandler commandHandler = (CommandHandler) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
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
            LoggingHandler.felog.error(String.format("Error replacing command /%s", clazz.getName()));
            e.printStackTrace();
        }
        if (newCommand instanceof ForgeEssentialsCommandBuilder)
            ((ForgeEssentialsCommandBuilder) newCommand).register();
    }

    public static void replaceCommand(ICommand oldCommand, ICommand newCommand)
    {
        try
        {
            CommandHandler commandHandler = (CommandHandler) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
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
            LoggingHandler.felog.error(String.format("Error replacing command /%s", oldCommand.getName()));
            e.printStackTrace();
        }
        if (newCommand instanceof ForgeEssentialsCommandBuilder)
            ((ForgeEssentialsCommandBuilder) newCommand).register();
    }

    public static void replaceCommand(String command, ICommand newCommand)
    {
        ICommand oldCommand = (ICommand) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().getCommands().get(command);
        if (oldCommand != null)
            replaceCommand(oldCommand, newCommand);
        else
            LoggingHandler.felog.error(String.format("Could not find command /%s to replace", command));
    }
*/
}
