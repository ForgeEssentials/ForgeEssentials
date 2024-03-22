package com.forgeessentials.commands.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.commands.registration.FECommandManager.ConfigurableCommand;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandWeather extends ForgeEssentialsCommandBuilder implements ConfigurableCommand
{

    public CommandWeather(boolean enabled)
    {
        super(enabled);
    }

    public static enum WeatherType
    {
        RAIN, THUNDER;

        public static WeatherType fromString(CommandSourceStack source, String name) throws CommandRuntimeException
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
                ChatOutputHandler.chatError(source, "Unknown weather type %s", name);
                return null;
            }
        }

    }

    public static enum WeatherState
    {
        FORCE, ENABLED, DISABLED, START, STOP;

        public static WeatherState fromString(CommandSourceStack source, String name) throws CommandRuntimeException
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
                ChatOutputHandler.chatError(source, "Unknown weather state %s", name);
                return null;
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

    protected static Map<String, WeatherData> weatherStates = new HashMap<>();

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "weather";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    public static WeatherState getWeatherState(String dim, WeatherType type)
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.literal("rain")
                .then(Commands.literal("enable").executes(CommandContext -> execute(CommandContext, "rain-enable")))
                .then(Commands.literal("disable").executes(CommandContext -> execute(CommandContext, "rain-disable")))
                .then(Commands.literal("force").executes(CommandContext -> execute(CommandContext, "rain-force")))
                .then(Commands.literal("start").executes(CommandContext -> execute(CommandContext, "rain-start")))
                .then(Commands.literal("stop").executes(CommandContext -> execute(CommandContext, "rain-stop")))
                .executes(CommandContext -> execute(CommandContext, "rain-info"))

        ).then(Commands.literal("storm")
                .then(Commands.literal("enable").executes(CommandContext -> execute(CommandContext, "storm-enable")))
                .then(Commands.literal("disable").executes(CommandContext -> execute(CommandContext, "storm-disable")))
                .then(Commands.literal("force").executes(CommandContext -> execute(CommandContext, "storm-force")))
                .then(Commands.literal("start").executes(CommandContext -> execute(CommandContext, "storm-start")))
                .then(Commands.literal("stop").executes(CommandContext -> execute(CommandContext, "storm-stop")))
                .executes(CommandContext -> execute(CommandContext, "storm-info"))

        ).executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("blank"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/weather rain|storm enable|disable|force");
            return Command.SINGLE_SUCCESS;
        }

        ServerLevel world = getServerPlayer(ctx.getSource()).getLevel();
        String dim = world.dimension().location().toString();

        String[] args = params.split("-");
        WeatherType type = WeatherType.fromString(ctx.getSource(), args[0]);
        if (type == null)
        {
            return Command.SINGLE_SUCCESS;
        }
        String typeName = type.toString().toLowerCase();

        if (args[1].equals("info"))
        {
            WeatherState state = getWeatherState(dim, type);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("%s is %s in world %s",
                    StringUtils.capitalize(typeName), state.toString().toLowerCase(), dim));
            return Command.SINGLE_SUCCESS;
        }

        WeatherState state = WeatherState.fromString(ctx.getSource(), args[1]);
        if (state == null)
        {
            return Command.SINGLE_SUCCESS;
        }

        switch (state)
        {
        case START:
            world.setWeatherParameters(0, 0, true, type != WeatherType.RAIN);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Started %s in world %s", typeName, dim);
            break;
        case STOP:
            if (type == WeatherType.RAIN)
                world.setWeatherParameters(0, 0, false, false);
            else
            {
                boolean rain = world.isRaining();
                world.setWeatherParameters(0, 0, rain, false);
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Stopped %s in world %s", typeName, dim);
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
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("%s %s in world %s",
                    StringUtils.capitalize(state.toString().toLowerCase()), typeName, dim));
            updateWorld(world);
            break;
        }
        return Command.SINGLE_SUCCESS;
    }

    @SubscribeEvent
    public void doWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == Phase.START)
            return;
        ServerLevel world = (ServerLevel) event.world;
        if (world.getGameTime() % 60 == 0)
            updateWorld(world);
    }

    public static void updateWorld(ServerLevel world)
    {
        String dim = world.dimension().location().toString();
        Map<WeatherType, WeatherState> worldData = weatherStates.get(dim);
        if (worldData == null)
            return;

        WeatherState rainState = worldData.get(WeatherType.RAIN);
        if (rainState != null)
        {
            switch (rainState)
            {
            case FORCE:
                world.setWeatherParameters(0, 20 * 70, true, false);
                break;
            case DISABLED:
                world.setWeatherParameters(0, 20 * 70, false, false);
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
                world.setWeatherParameters(0, 20 * 70, true, true);
                break;
            case DISABLED:
                world.setWeatherParameters(0, 20 * 70, false, false);
                break;
            default:
                break;
            }
        }
    }

    public static void save()
    {
        DataManager.getInstance().deleteAll(WeatherData.class);
        for (Entry<String, WeatherData> state : weatherStates.entrySet())
        {
            DataManager.getInstance().save(state.getValue(), state.getKey().toString().replace(":", "-"));
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
                weatherStates.put(state.getKey().replace("-", ":"), state.getValue());
            }
            catch (NumberFormatException e)
            {
                /* do nothing or log message */
            }
        }
    }
}
