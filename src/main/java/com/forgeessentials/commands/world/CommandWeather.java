package com.forgeessentials.commands.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandWeather extends ForgeEssentialsCommandBuilder implements ConfigurableCommand
{

    public CommandWeather(boolean enabled)
    {
        super(enabled);
    }

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

    protected static Map<String, WeatherData> weatherStates = new HashMap<>();

    @Override
    public String getPrimaryAlias()
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

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".weather";
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("rain")
                        .then(Commands.argument("enable", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "rain-enable")
                                        )
                                )
                        .then(Commands.argument("disable", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "rain-disable")
                                        )
                                )
                        .then(Commands.argument("force", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "rain-force")
                                        )
                                )
                        .then(Commands.argument("start", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "rain-start")
                                        )
                                )
                        .then(Commands.argument("stop", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "rain-stop")
                                        )
                                )
                        .executes(CommandContext -> execute(CommandContext, "rain-info")
                                )

                        )
                .then(Commands.literal("storm")
                        .then(Commands.argument("enable", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "storm-enable")
                                        )
                                )
                        .then(Commands.argument("disable", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "storm-disable")
                                        )
                                )
                        .then(Commands.argument("force", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "storm-force")
                                        )
                                )
                        .then(Commands.argument("start", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "storm-start")
                                        )
                                )
                        .then(Commands.argument("stop", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "storm-stop")
                                        )
                                )
                        .executes(CommandContext -> execute(CommandContext, "storm-info")
                                )

                        )
                .executes(CommandContext -> execute(CommandContext, "blank")
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString().equals("blank"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/weather rain|storm enable|disable|force");
            return Command.SINGLE_SUCCESS;
        }

        ServerWorld world = getServerPlayer(ctx.getSource()).getLevel();
        String dim = world.dimension().location().toString();

        String[] args = params.toString().split("-");
        WeatherType type = WeatherType.fromString(args[0]);
        String typeName = type.toString().toLowerCase();

        if (args[1]=="info")
        {
            WeatherState state = getWeatherState(dim, type);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("%s is %s in world %d", StringUtils.capitalize(typeName), state.toString().toLowerCase(), dim));
            return Command.SINGLE_SUCCESS;
        }

        WeatherState state = WeatherState.fromString(args[1]);

        switch (state)
        {
        case START:
            if (type == WeatherType.RAIN)
                world.setWeatherParameters(0, 0, true, false);
            else
            {
                world.setWeatherParameters(0, 0, true, true);
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Started %s in world %d", typeName, dim);
            break;
        case STOP:
            if (type == WeatherType.RAIN)
                world.setWeatherParameters(0, 0, false, false);
            else
            {
                boolean rain = world.isRaining();
                world.setWeatherParameters(0, 0, rain, false);
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Stopped %s in world %d", typeName, dim);
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
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("%s %s in world %d", StringUtils.capitalize(state.toString().toLowerCase()), typeName, dim));
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
        ServerWorld world = (ServerWorld) event.world;
        if (world.getGameTime() % 60 == 0)
            updateWorld(world);
    }

    public static void updateWorld(ServerWorld world)
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
                weatherStates.put(state.getKey(), state.getValue());
            }
            catch (NumberFormatException e)
            {
                /* do nothing or log message */
            }
        }
    }

    @Override
    public void loadConfig(ForgeConfigSpec.Builder BUILDER, String category)
    {
        /* do nothing */
    }

    //@Override
    public void bakeConfig(boolean reload)
    {
    	/* do nothing */
    }

}
