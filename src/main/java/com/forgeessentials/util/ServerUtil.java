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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.server.CommandMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.environment.CommandSetChecker;
import com.forgeessentials.core.environment.Environment;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.common.base.Throwables;

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

    public static double parseYLocation(ICommandSender sender, double relative, String value) throws CommandException
    {
        boolean isRelative = value.startsWith("~");
        if (isRelative && Double.isNaN(relative))
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] { Double.valueOf(relative) });
        double d1 = isRelative ? relative : 0.0D;
        if (!isRelative || value.length() > 1)
        {
            if (isRelative)
                value = value.substring(1);
            d1 += CommandBase.parseDouble(value);
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
            return new File(FMLCommonHandler.instance().getMinecraftServerInstance().getFile("saves"), FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName());
        else
            return FMLCommonHandler.instance().getMinecraftServerInstance().getFile(FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName());
    }

    /* ------------------------------------------------------------ */

    /**
     * Gets a type safe player list
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<EntityPlayerMP> getPlayerList()
    {
        MinecraftServer mc = FMLCommonHandler.instance().getMinecraftServerInstance();
        return mc == null || mc.getPlayerList() == null ? new ArrayList<>() : mc.getPlayerList().getPlayers();
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
        return FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0];
    }

    public static long getOverworldTime()
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0].getWorldInfo().getWorldTime();
    }

    public static boolean isServerRunning()
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance() != null && FMLCommonHandler.instance().getMinecraftServerInstance().isServerRunning();
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
        for (String key : new HashSet<String>(nbt.getKeySet()))
            nbt.removeTag(key);
    
        // Write new data
        for (String key : (Set<String>) data.getKeySet())
            nbt.setTag(key, data.getTag(key));
    }

    /* ------------------------------------------------------------ */

    public static String getItemName(Item item)
    {
        return Item.REGISTRY.getNameForObject(item).toString();
    }

    public static String getItemPermission(Item item)
    {
        ResourceLocation loc = (ResourceLocation) Item.REGISTRY.getNameForObject(item);
        return (loc.getResourceDomain() + '.' + loc.getResourcePath()).replace(' ', '_');
    }

    public static String getItemPermission(ItemStack stack, boolean checkMeta)
    {
        try
        {
            int dmg = stack.getItemDamage();
            if (!checkMeta || dmg == 0 || dmg == 32767)
                return getItemPermission(stack.getItem());
            else
                return getItemPermission(stack.getItem()) + "." + dmg;
        }
        catch (Exception e)
        {
            String msg;
            if (stack == ItemStack.EMPTY)
                msg = "Error getting item permission. Stack item is null. Please report this error (except for TF) and try enabling FE safe-mode.";
            else
                msg = String.format("Error getting item permission for item %s. Please report this error and try enabling FE safe-mode.", stack.getItem().getClass().getName());
            if (!ForgeEssentials.isSafeMode())
                throw new RuntimeException(msg, e);
            LoggingHandler.felog.error(msg);
            return "fe.error";
        }
    }

    public static String getItemPermission(ItemStack stack)
    {
        return getItemPermission(stack, true);
    }

    public static String getBlockName(Block block)
    {
        Object o = Block.REGISTRY.getNameForObject(block);
        if(o instanceof ResourceLocation){
            ResourceLocation rl = (ResourceLocation) o;
            return rl.getResourcePath();
        } else {
            return (String) o;
        }
    }

    public static String getBlockPermission(Block block)
    {
        ResourceLocation loc = (ResourceLocation) Block.REGISTRY.getNameForObject(block);
        return (loc.getResourceDomain() + '.' + loc.getResourcePath()).replace(' ', '_');
    }

    /* ------------------------------------------------------------ */

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
        if (newCommand instanceof ForgeEssentialsCommandBase)
            ((ForgeEssentialsCommandBase) newCommand).register();
    }

    public static void replaceCommand(ICommand oldCommand, ICommand newCommand)
    {
        try
        {
            CommandHandler commandHandler = (CommandHandler) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
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
            LoggingHandler.felog.error(String.format("Error replacing command /%s", oldCommand.getName()));
            e.printStackTrace();
        }
        if (newCommand instanceof ForgeEssentialsCommandBase)
            ((ForgeEssentialsCommandBase) newCommand).register();
    }

    public static void replaceCommand(String command, ICommand newCommand)
    {
        ICommand oldCommand = (ICommand) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().getCommands().get(command);
        if (oldCommand != null)
            replaceCommand(oldCommand, newCommand);
        else
            LoggingHandler.felog.error(String.format("Could not find command /%s to replace", command));
    }

}
