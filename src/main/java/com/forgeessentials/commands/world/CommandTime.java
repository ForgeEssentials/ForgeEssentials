package com.forgeessentials.commands.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.core.commands.registration.FECommandManager.ConfigurableCommand;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandTime extends ForgeEssentialsCommandBuilder implements ConfigurableCommand
{

    public CommandTime(boolean enabled)
    {
        super(enabled);
    }

    public static final int dayTimeStart = 1;
    public static final int dayTimeEnd = 11;
    public static final int nightTimeStart = 14;
    public static final int nightTimeEnd = 22;

    public static class TimeData
    {
        Long frozenTime;
    }

    protected static HashMap<String, TimeData> timeData = new HashMap<>();

    protected static TimeData getTimeData(String worldname)
    {
        TimeData td = timeData.get(worldname);
        if (td == null)
        {
            td = new TimeData();
            timeData.put(worldname, td);
        }
        return td;
    }

    protected static TimeData getTimeData(ResourceKey<Level> dimension)
    {
        return getTimeData(dimension.location().toString());
    }
    /* ------------------------------------------------------------ */

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "time";
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("set")
                        .then(Commands.literal("day")
                                .then(Commands.literal("all")
                                        .executes(CommandContext -> execute(CommandContext, "set-day-all")))
                                .then(Commands.argument("dim", DimensionArgument.dimension())
                                        .executes(CommandContext -> execute(CommandContext, "set-day-one"))))
                        .then(Commands.literal("midday")
                                .then(Commands.literal("all")
                                        .executes(CommandContext -> execute(CommandContext, "set-midday-all")))
                                .then(Commands.argument("dim", DimensionArgument.dimension())
                                        .executes(CommandContext -> execute(CommandContext, "set-midday-one"))))
                        .then(Commands.literal("dusk")
                                .then(Commands.literal("all")
                                        .executes(CommandContext -> execute(CommandContext, "set-dusk-all")))
                                .then(Commands.argument("dim", DimensionArgument.dimension())
                                        .executes(CommandContext -> execute(CommandContext, "set-dusk-one"))))
                        .then(Commands.literal("night")
                                .then(Commands.literal("all")
                                        .executes(CommandContext -> execute(CommandContext, "set-night-all")))
                                .then(Commands.argument("dim", DimensionArgument.dimension())
                                        .executes(CommandContext -> execute(CommandContext, "set-night-one"))))
                        .then(Commands.literal("midnight")
                                .then(Commands.literal("all")
                                        .executes(CommandContext -> execute(CommandContext, "set-midnight-all")))
                                .then(Commands.argument("dim", DimensionArgument.dimension())
                                        .executes(CommandContext -> execute(CommandContext, "set-midnight-one"))))
                        .then(Commands.argument("time", StringArgumentType.string())
                                .then(Commands.literal("all")
                                        .executes(CommandContext -> execute(CommandContext, "set-time-all")))
                                .then(Commands.argument("dim", DimensionArgument.dimension())
                                        .executes(CommandContext -> execute(CommandContext, "set-time-one")))))
                .then(Commands.literal("add")
                        .then(Commands.argument("time", StringArgumentType.string())
                                .then(Commands.literal("all")
                                        .executes(CommandContext -> execute(CommandContext, "add-time-all")))
                                .then(Commands.argument("dim", DimensionArgument.dimension())
                                        .executes(CommandContext -> execute(CommandContext, "add-time-one")))))
                .then(Commands.literal("freeze")
                        .then(Commands.literal("all").executes(CommandContext -> execute(CommandContext, "freeze-all")))
                        .then(Commands.argument("dim", DimensionArgument.dimension())
                                .executes(CommandContext -> execute(CommandContext, "freeze-one"))))
                .executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("blank"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/time set|add <t> [dim]");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/time freeze [dim]");
            return Command.SINGLE_SUCCESS;
        }

        String[] arg = params.split("-");
        switch (arg[0])
        {
        case "freeze":
            parseFreeze(ctx, arg[1]);
            break;
        case "set":
            parseTime(ctx, false, arg);
            break;
        case "add":
            parseTime(ctx, true, arg);
            break;
        default:
            ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_UNKNOWN_SUBCOMMAND, arg[0]);
        }
        return Command.SINGLE_SUCCESS;
    }

    public static void parseFreeze(CommandContext<CommandSourceStack> ctx, String arg) throws CommandSyntaxException
    {

        if (arg.equals("all"))
        {
            boolean freeze = getTimeData(ServerLevel.OVERWORLD).frozenTime == null;
            for (ServerLevel w : ServerLifecycleHooks.getCurrentServer().getAllLevels())
            {
                TimeData td = getTimeData(w.dimension());
                td.frozenTime = freeze ? w.getLevelData().getDayTime() : null;
            }
            if (freeze)
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Froze time in all worlds");
            else
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Unfroze time in all worlds");
        }
        else
        {
            ServerLevel world = DimensionArgument.getDimension(ctx, "dim");
            TimeData td = getTimeData(world.dimension());
            td.frozenTime = (td.frozenTime == null) ? world.getLevelData().getDayTime() : null;
            if (td.frozenTime != null)
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Froze time in %s", world.dimension());
            else
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Unfroze time in %s", world.dimension());
        }
        save();
    }

    public static void parseTime(CommandContext<CommandSourceStack> ctx, boolean addTime, String[] arg)
            throws CommandSyntaxException
    {
        long time;
        if (arg[1].equals("time"))
        {
            try
            {
                time = mcParseTimeReadable(StringArgumentType.getString(ctx, "time"));
            }
            catch (FECommandParsingException e)
            {
                ChatOutputHandler.chatError(ctx.getSource(), e.error);
                return;
            }
        }
        else
        {
            if (addTime)
            {
                ChatOutputHandler.chatError(ctx.getSource(),
                        "Add time does not accept time values in the form of day, midday, etc");
                return;
            }
            switch (arg[1])
            {
            case "day":
                time = 1000;
                break;
            case "midday":
                time = 6 * 1000;
                break;
            case "dusk":
                time = 12 * 1000;
                break;
            case "night":
                time = 14 * 1000;
                break;
            case "midnight":
                time = 18 * 1000;
                break;
            default:
                ChatOutputHandler.chatError(ctx.getSource(), "Invalid Time format");
                return;
            }
        }

        if (arg[2].equals("all"))
        {
            for (ServerLevel w : ServerLifecycleHooks.getCurrentServer().getAllLevels())
            {
                if (addTime)
                    w.setDayTime(w.getDayTime() + time);
                else
                    w.setDayTime(time);
                TimeData td = getTimeData(w.dimension());
                if (td.frozenTime != null)
                    td.frozenTime = w.getDayTime();
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set time to %s in all worlds", time);
        }
        else
        {
            ServerLevel world = DimensionArgument.getDimension(ctx, "dim");
            if (addTime)
                world.setDayTime(world.getDayTime() + time);
            else
                world.setDayTime(time);
            TimeData td = getTimeData(world.dimension());
            if (td.frozenTime != null)
                td.frozenTime = world.getDayTime();
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set time to %s", time);
        }
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void doWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == Phase.START)
            return;
        ServerLevel world = (ServerLevel) event.world;
        if (world.getGameTime() % 10 == 0)
            updateWorld(world);
    }

    public static void updateWorld(ServerLevel world)
    {
        TimeData td = getTimeData(world.dimension());
        if (td.frozenTime != null)
            world.setDayTime(td.frozenTime);
    }

    public static void save()
    {
        DataManager.getInstance().deleteAll(TimeData.class);
        for (Entry<String, TimeData> state : timeData.entrySet())
        {
            DataManager.getInstance().save(state.getValue(), state.getKey().toString().replace(":", "-"));
        }
    }

    @Override
    public void loadData()
    {
        Map<String, TimeData> states = DataManager.getInstance().loadAll(TimeData.class);
        timeData.clear();
        for (Entry<String, TimeData> state : states.entrySet())
        {
            if (state.getValue() == null)
                continue;
            try
            {
                timeData.put(state.getKey().replace("-", ":"), state.getValue());
            }
            catch (NumberFormatException e)
            {
                /* do nothing or log message */
            }
        }
    }
}