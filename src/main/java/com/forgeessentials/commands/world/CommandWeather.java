package com.forgeessentials.commands.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.CommandParserArgs;

public class CommandWeather extends ParserCommandBase implements ConfigurableCommand
{

    public static enum WeatherType
    {
        RAIN, THUNDER;

        public static WeatherType fromString(String name) throws CommandException
        {
            name = name.toLowerCase();
            switch (name)
            {
            case "rain":
                return WeatherType.RAIN;
            case "thunder":
            case "storm":
                return WeatherType.THUNDER;
            default:
                throw new TranslatedCommandException("Unknown weather type %s", name);
            }
        }

    }

    public static enum WeatherState
    {
        FORCE, ENABLED, DISABLED, START, STOP;

        public static WeatherState fromString(String name) throws CommandException
        {
            name = name.toLowerCase();
            switch (name)
            {
            case "enable":
                return WeatherState.ENABLED;
            case "disable":
                return WeatherState.DISABLED;
            case "force":
                return WeatherState.FORCE;
            case "on":
            case "start":
                return WeatherState.START;
            case "off":
            case "stop":
                return WeatherState.STOP;
            default:
                throw new TranslatedCommandException("Unknown weather state %s", name);
            }
        }

    }

    public static class WeatherData extends HashMap<WeatherType, WeatherState>
    {
        public WeatherData()
        {
            super(WeatherType.values().length);
        }
    }

    protected static Map<Integer, WeatherData> weatherStates = new HashMap<>();

    public CommandWeather()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getCommandName()
    {
        return "feweather";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "weather" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/weather rain|storm [enable|disable|force]: Weather manipulation";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".weather";
    }

    public static WeatherState getWeatherState(int dim, WeatherType type)
    {
        Map<WeatherType, WeatherState> worldData = weatherStates.get(dim);
        if (worldData != null)
        {
            WeatherState state = worldData.get(type);
            if (state != null)
                return state;
        }
        return WeatherState.ENABLED;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/weather rain|storm enable|disable|force");
            return;
        }
        if (arguments.senderPlayer == null)
        {
            arguments.error(FEPermissions.MSG_NO_CONSOLE_COMMAND);
            return;
        }

        World world = arguments.senderPlayer.worldObj;
        int dim = world.provider.getDimensionId();

        arguments.tabComplete("rain", "thunder");
        WeatherType type = WeatherType.fromString(arguments.remove());
        String typeName = type.toString().toLowerCase();

        if (arguments.isEmpty())
        {
            WeatherState state = getWeatherState(dim, type);
            arguments.confirm(Translator.format("%s is %s in world %d", StringUtils.capitalize(typeName), state.toString().toLowerCase(), dim));
            return;
        }

        arguments.tabComplete("enable", "disable", "force", "start", "stop");
        WeatherState state = WeatherState.fromString(arguments.remove());

        if (arguments.isTabCompletion)
            return;

        WorldInfo wi = world.getWorldInfo();
        switch (state)
        {
        case START:
            if (type == WeatherType.RAIN)
                wi.setRaining(true);
            else
            {
                wi.setRaining(true);
                wi.setThundering(true);
            }
            arguments.confirm("Started %s in world %d", typeName, dim);
            break;
        case STOP:
            if (type == WeatherType.RAIN)
                wi.setRaining(false);
            else
                wi.setThundering(false);
            arguments.confirm("Stopped %s in world %d", typeName, dim);
            break;
        default:
            WeatherData worldData = weatherStates.get(dim);
            if (worldData == null)
            {
                worldData = new WeatherData();
                weatherStates.put(dim, worldData);
            }

            worldData.put(type, state);
            save();
            arguments.confirm(Translator.format("%s %s in world %d", StringUtils.capitalize(state.toString().toLowerCase()), typeName, dim));
            updateWorld(world);
            break;
        }
    }

    @SubscribeEvent
    public void doWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == Phase.START)
            return;
        World world = event.world;
        WorldInfo wi = world.getWorldInfo();
        if (wi.getWorldTotalTime() % 60 == 0)
            updateWorld(world);
    }

    public static void updateWorld(World world)
    {
        int dim = world.provider.getDimensionId();
        Map<WeatherType, WeatherState> worldData = weatherStates.get(dim);
        if (worldData == null)
            return;

        WorldInfo wi = world.getWorldInfo();

        WeatherState rainState = worldData.get(WeatherType.RAIN);
        if (rainState != null)
        {
            switch (rainState)
            {
            case FORCE:
                wi.setRainTime(20 * 70);
                wi.setRaining(true);
                break;
            case DISABLED:
                wi.setRainTime(20 * 70);
                wi.setRaining(false);
                break;
            default:
                break;
            }
        }

        WeatherState thunderState = worldData.get(WeatherType.THUNDER);
        if (thunderState != null)
        {
            switch (thunderState)
            {
            case FORCE:
                wi.setRainTime(20 * 70);
                wi.setRaining(true);
                wi.setThunderTime(20 * 70);
                wi.setThundering(true);
                break;
            case DISABLED:
                wi.setThunderTime(20 * 70);
                wi.setThundering(false);
                break;
            default:
                break;
            }
        }
    }

    public static void save()
    {
        DataManager.getInstance().deleteAll(WeatherData.class);
        for (Entry<Integer, WeatherData> state : weatherStates.entrySet())
        {
            DataManager.getInstance().save(state.getValue(), state.getKey().toString());
        }
    }

    @Override
    public void loadData()
    {
        Map<String, WeatherData> states = DataManager.getInstance().loadAll(WeatherData.class);
        weatherStates.clear();
        for (Entry<String, WeatherData> state : states.entrySet())
        {
            if (state.getValue() == null)
                continue;
            try
            {
                weatherStates.put(Integer.parseInt(state.getKey()), state.getValue());
            }
            catch (NumberFormatException e)
            {
                /* do nothing or log message */
            }
        }
    }

    @Override
    public void loadConfig(Configuration config, String category)
    {
    }

}
