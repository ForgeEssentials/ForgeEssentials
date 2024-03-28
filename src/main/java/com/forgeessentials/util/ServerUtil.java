package com.forgeessentials.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.forgeessentials.core.environment.Environment;

import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraft.world.level.block.Block;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;


public abstract class ServerUtil
{

    public static DedicatedServerSettings getServerPropProvider(DedicatedServer currentServer)
    {
        return ObfuscationReflectionHelper.getPrivateValue(DedicatedServer.class, currentServer, "f_139604_");
    }

    public static void changeFinalFieldStaticField(Field field, Object newValue) throws Exception
    {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    public static void changeFinalFieldNonStaticField(Object object, String fieldName, Object newValue)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
    {
        Field field = object.getClass().getField(fieldName);
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(object, newValue);
    }

    public static void changeFinalFieldNonStaticFieldRefMap(Object object, String fieldName, Object newValue)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
    {
        Field field = object.getClass().getField(ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, fieldName));
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(object, newValue);
    }

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

    /**
     * Get the directory where the world is saved
     * 
     * @return
     */
    public static File getWorldPath()
    {
        if (Environment.isClient())
            return new File(ServerLifecycleHooks.getCurrentServer().getFile("saves"),
                    ServerLifecycleHooks.getCurrentServer().getWorldData().getLevelName());
        else
            return new File(ServerLifecycleHooks.getCurrentServer().getServerDirectory(), "world");
    }

    /* ------------------------------------------------------------ */

    /**
     * Gets a type safe player list
     * 
     * @return
     */
    public static List<ServerPlayer> getPlayerList()
    {
        MinecraftServer mc = ServerLifecycleHooks.getCurrentServer();
        return mc == null || mc.getPlayerList() == null ? new ArrayList<>() : mc.getPlayerList().getPlayers();
    }

    /**
     * Get tps per world.
     *
     * @param World
     * @return -1 if error
     */
    public static double getWorldTPS(ResourceKey<Level> World)
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        long sum = 0L;
        long[] ticks = server.getTickTime(World);
        for (long tick : ticks) {
            sum += tick;
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

    public static ServerLevel getOverworld()
    {
        return ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
    }

    public static long getOverworldTime()
    {
        return ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getDayTime();
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

    public static void copyNbt(CompoundTag nbt, CompoundTag data)
    {
        // Clear old data
        for (String key : new HashSet<>(nbt.getAllKeys()))
            nbt.remove(key);

        // Write new data
        for (String key : (Set<String>) data.getAllKeys())
            nbt.put(key, data.get(key));
    }

    /* ------------------------------------------------------------ */

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

    public static ServerLevel getWorldFromString(String dim)
    {
        return ServerLifecycleHooks.getCurrentServer().getLevel(getWorldKeyFromString(dim));
    }

    public static ResourceKey<Level> getWorldKeyFromString(String dim)
    {
        return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dim));
    }

    /* ------------------------------------------------------------ */
    /*
     * public static void replaceCommand(Class<CommandMessage> clazz, ICommand newCommand) { try { CommandHandler commandHandler = (CommandHandler)
     * FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager(); Map<String, ICommand> commandMap = ReflectionHelper.getPrivateValue(CommandHandler.class,
     * commandHandler, "commandMap", "a", "field_71562_a"); Set<ICommand> commandSet = ReflectionHelper.getPrivateValue(CommandHandler.class, commandHandler,
     * CommandSetChecker.FIELDNAME); for (Iterator<Entry<String, ICommand>> it = commandMap.entrySet().iterator(); it.hasNext();) { Entry<String, ICommand> command = it.next(); if
     * (clazz.isAssignableFrom(command.getValue().getClass())) { commandSet.remove(command.getValue()); commandSet.add(newCommand); command.setValue(newCommand); } } } catch
     * (Exception e) { LoggingHandler.felog.error(String.format("Error replacing command /%s", clazz.getName())); e.printStackTrace(); } if (newCommand instanceof
     * ForgeEssentialsCommandBuilder) ((ForgeEssentialsCommandBuilder) newCommand).register(); }
     * 
     * public static void replaceCommand(ICommand oldCommand, ICommand newCommand) { try { CommandHandler commandHandler = (CommandHandler)
     * FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager(); for (Iterator<Entry<String, ICommand>> it = commandMap.entrySet().iterator(); it.hasNext();) {
     * Entry<String, ICommand> command = it.next(); if (command.getValue() == oldCommand) { commandSet.remove(command.getValue()); commandSet.add(newCommand);
     * command.setValue(newCommand); } } } catch (Exception e) { LoggingHandler.felog.error(String.format("Error replacing command /%s", oldCommand.getName()));
     * e.printStackTrace(); } if (newCommand instanceof ForgeEssentialsCommandBuilder) ((ForgeEssentialsCommandBuilder) newCommand).register(); }
     * 
     * public static void replaceCommand(String command, ICommand newCommand) { ICommand oldCommand = (ICommand)
     * FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager(). getCommands().get(command); if (oldCommand != null) replaceCommand(oldCommand, newCommand);
     * else LoggingHandler.felog.error(String. format("Could not find command /%s to replace", command)); }
     */
}
