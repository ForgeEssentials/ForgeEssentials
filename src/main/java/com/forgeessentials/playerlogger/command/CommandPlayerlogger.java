package com.forgeessentials.playerlogger.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.TypedQuery;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.playerlogger.FilterConfig;
import com.forgeessentials.playerlogger.FilterConfig.ActionEnum;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.playerlogger.PlayerLogger;
import com.forgeessentials.playerlogger.PlayerLoggerChecker;
import com.forgeessentials.playerlogger.entity.Action;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.questioner.QuestionerException.QuestionerStillActiveException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandPlayerlogger extends ForgeEssentialsCommandBuilder
{

    public CommandPlayerlogger(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "pl";
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
        return baseBuilder.then(Commands.literal("stats").executes(CommandContext -> execute(CommandContext, "stats")))
                .then(Commands.literal("filter")
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "filter-help")))
                        .then(Commands.argument("filterType", StringArgumentType.word()).suggests(SUGGEST_filters)
                                .then(Commands.literal("action")
                                        .then(Commands.literal("reset").executes(
                                                CommandContext -> execute(CommandContext, "filter-action-reset")))
                                        .then(Commands.argument("type", StringArgumentType.greedyString())
                                                .suggests(SUGGEST_actiontabs)
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "filter-action-" + StringArgumentType.getString(CommandContext,
                                                                "type")))))
                                .then(Commands.literal("blockid")
                                        .then(Commands.literal("reset").executes(
                                                CommandContext -> execute(CommandContext, "filter-blockid-reset")))
                                        .then(Commands.argument("block", BlockStateArgument.block()).executes(
                                                CommandContext -> execute(CommandContext, "filter-blockid-block"))))
                                .then(Commands.literal("before")
                                        .then(Commands.literal("reset").executes(
                                                CommandContext -> execute(CommandContext, "filter-before-reset")))
                                        .then(Commands.argument("duration", StringArgumentType.string())
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "filter-before-" + StringArgumentType.getString(CommandContext,
                                                                "duration")))))
                                .then(Commands.literal("after")
                                        .then(Commands.literal("reset").executes(
                                                CommandContext -> execute(CommandContext, "filter-after-reset")))
                                        .then(Commands.argument("duration", StringArgumentType.string())
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "filter-after-" + StringArgumentType.getString(CommandContext,
                                                                "duration")))))
                                .then(Commands.literal("range").then(Commands
                                        .argument("range", IntegerArgumentType.integer())
                                        .executes(CommandContext -> execute(CommandContext,
                                                "filter-range-"
                                                        + IntegerArgumentType.getInteger(CommandContext, "range")))))
                                .then(Commands.literal("player").then(Commands
                                        .argument("name", StringArgumentType.word())
                                        .executes(CommandContext -> execute(CommandContext,
                                                "filter-player-"
                                                        + StringArgumentType.getString(CommandContext, "name")))))))
                .then(Commands.literal("lookup")
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "lookup-help")))
                        .then(Commands.literal("Position").then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.argument("dim", DimensionArgument.dimension()).then(Commands
                                        .literal("PersonalFilter")
                                        .then(Commands.literal("MaxPageSize").executes(
                                                CommandContext -> execute(CommandContext, "lookup-loc:config:max")))
                                        .then(Commands.literal("PageSizeSelect")
                                                .then(Commands.argument("pageSize", IntegerArgumentType.integer())
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "lookup-loc:config:select")))))
                                        .then(Commands.literal("GlobalFilter")
                                                .then(Commands.literal("MaxPageSize")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "lookup-loc:noconfig:max")))
                                                .then(Commands.literal("PageSizeSelect")
                                                        .then(Commands
                                                                .argument("pageSize", IntegerArgumentType.integer())
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "lookup-loc:noconfig:select"))))))))
                        .then(Commands.literal("Player").then(Commands.argument("player", StringArgumentType.word())
                                .then(Commands.literal("PersonalFilter")
                                        .then(Commands.literal("MaxPageSize").executes(
                                                CommandContext -> execute(CommandContext, "lookup-player:config:max")))
                                        .then(Commands.literal("PageSizeSelect")
                                                .then(Commands.argument("pageSize", IntegerArgumentType.integer())
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "lookup-player:config:select")))))
                                .then(Commands.literal("GlobalFilter").then(Commands.literal("MaxPageSize").executes(
                                        CommandContext -> execute(CommandContext, "lookup-player:noconfig:max")))
                                        .then(Commands.literal("PageSizeSelect")
                                                .then(Commands.argument("pageSize", IntegerArgumentType.integer())
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "lookup-player:noconfig:select")))))))
                        .then(Commands.literal("global")
                                .then(Commands.literal("PersonalFilter")
                                        .then(Commands.literal("MaxPageSize").executes(
                                                CommandContext -> execute(CommandContext, "lookup-global:config:max")))
                                        .then(Commands.literal("PageSizeSelect")
                                                .then(Commands.argument("pageSize", IntegerArgumentType.integer())
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "lookup-global:config:select")))))
                                .then(Commands.literal("GlobalFilter")
                                		.then(Commands.literal("MaxPageSize").executes(
                                				CommandContext -> execute(CommandContext, "lookup-global:noconfig:max")))
                                        .then(Commands.literal("PageSizeSelect")
                                                .then(Commands.argument("pageSize", IntegerArgumentType.integer())
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "lookup-global:noconfig:select")))))))
                .then(Commands.literal("purge")
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "purge-help")))
                        .then(Commands.argument("daysToPurge", IntegerArgumentType.integer())
                                .executes(CommandContext -> execute(CommandContext, "purge-days"))))
                .then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "help")));
    }

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_actiontabs = (ctx, builder) -> {
        List<String> actiontabs = new ArrayList<>();
        ActionEnum[] enums = ActionEnum.values();
        for (ActionEnum ae : enums)
        {
            actiontabs.add(ae.name());
        }
        actiontabs.add("reset");
        return SharedSuggestionProvider.suggest(actiontabs, builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_filters = (ctx, builder) -> {
        List<String> filters = new ArrayList<>();
        filters.add("GlobalFilter");
        filters.add("PersonalFilter");
        return SharedSuggestionProvider.suggest(filters, builder);
    };

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/pl stats: Show playerlogger stats");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/pl filter: Sets the players FilterConfig");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/pl gfilter: Global /pl filter");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/pl lookup: Looks up playerlogger data");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/pl purge: Purge old playerData");
            return Command.SINGLE_SUCCESS;
        }
        FilterConfig fc = null;
        String[] subCmd = params.split("-");
        switch (subCmd[0])
        {
        case "filter":
            boolean global = true;
            if (subCmd[1].toLowerCase().equals("help"))
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        "/pl [GlobalConfig | PersonalConfig] [filterConfig] : Filter displayed blocks based on a criteria");
                return Command.SINGLE_SUCCESS;
            }
            // check for valid filter arg
            if (!StringArgumentType.getString(ctx, "filterType").equals("PersonalFilter")
                    && !StringArgumentType.getString(ctx, "filterType").equals("GlobalFilter"))
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Invalid filter type!");
                return Command.SINGLE_SUCCESS;
            }
            // switch for global and personal
            if (StringArgumentType.getString(ctx, "filterType").equals("PersonalFilter"))
            {
                fc = FilterConfig.getDefaultPlayerConfig(getIdent(ctx.getSource())) != null
                        ? FilterConfig.getDefaultPlayerConfig(getIdent(ctx.getSource()))
                        : new FilterConfig();
                global = false;
            }
            else
                fc = FilterConfig.globalConfig;
            // setup config parser
            List<String> arg1 = new ArrayList<>(Arrays.asList(subCmd));
            arg1.remove(0);
            fc.parse(ctx, arg1);
            // save config is personal
            if (!global)
                FilterConfig.setPerPlayerFilters(getIdent(ctx.getSource()), fc);

            ChatOutputHandler.sendMessage(ctx.getSource(),
                    ChatOutputHandler.formatColors((global ? "Global" : getIdent(ctx.getSource()).getUsername() + "'s")
                            + " Picker set: \n" + fc.toReadableString()));

            break;
        case "lookup":
            if (subCmd[1].toLowerCase().equals("help"))
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        "/pl [glookup | lookup] [[[x] [y] [z] [dim]?] | [player]]?  [pageSize]? [filterConfig]?");
                return Command.SINGLE_SUCCESS;
            }
            String[] subCmds = subCmd[1].split(":");
            WorldPoint p = getSenderPoint(ctx.getSource());
            if (subCmds[0].toLowerCase().equals("loc"))
            {
                p.setX(BlockPosArgument.getLoadedBlockPos(ctx, "pos").getX());
                p.setY(BlockPosArgument.getLoadedBlockPos(ctx, "pos").getY());
                p.setZ(BlockPosArgument.getLoadedBlockPos(ctx, "pos").getZ());
                p.setDimension(DimensionArgument.getDimension(ctx, "dim"));
            }
            else if (subCmds[0].toLowerCase().equals("player"))
            {
                Player pl;
                try
                {
                    pl = parsePlayer(StringArgumentType.getString(ctx, "name"), true, true)
                            .getPlayer();
                }
                catch (FECommandParsingException e)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), e.error);
                    return Command.SINGLE_SUCCESS;
                }
                p = new WorldPoint(pl.level, pl.blockPosition());
            }
            else if(subCmds[0].toLowerCase().equals("global")) {
            	p=null;
            }

            int pageSize = 0;
            boolean newCheck = false;
            if (subCmds[1].toLowerCase().equals("config"))
            {
            	fc = FilterConfig.getDefaultPlayerConfig(getIdent(ctx.getSource()));
                if (fc == null)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "You Havent set a filter yet");
                    return Command.SINGLE_SUCCESS;
                }
            }
            else if (subCmds[1].toLowerCase().equals("noconfig"))
            {
                fc = FilterConfig.globalConfig;
            }
            if (subCmds[2].toLowerCase().equals("select"))
            {
                pageSize = IntegerArgumentType.getInteger(ctx, "pageSize");
                newCheck = true;
            }
            else if (subCmds[2].toLowerCase().equals("max"))
            {
                pageSize = 0;
                newCheck = false;
            }
            ChatOutputHandler.sendMessage(ctx.getSource(),
                    ChatOutputHandler.formatColors("Looking up: \n" + fc.toReadableString()));
            PlayerLoggerChecker.instance.CheckBlock(p, fc, ctx.getSource(), pageSize, newCheck);
            break;
        case "stats":
            PlayerLogger logger;
            synchronized (logger = ModulePlayerLogger.getLogger())
            {
                TypedQuery<Long> qActionCount = logger.buildCountQuery(Action.class, null, null);
                long actionCount = qActionCount.getSingleResult();
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        String.format("Logged action count: %s", actionCount));
            }
            break;
        case "purge":
            if (subCmd[1].equals("help"))
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        "/pl purge <duration>: Purge all PL data that is older than <duration> in days");
                return Command.SINGLE_SUCCESS;
            }
            else
            {
                int days = IntegerArgumentType.getInteger(ctx, "daysToPurge");
                final Date startTime = new Date();
                startTime.setTime(startTime.getTime() - TimeUnit.DAYS.toMillis(days));
                final String startTimeStr = startTime.toString();

                QuestionerCallback handler = new QuestionerCallback() {
                    @Override
                    public void respond(Boolean response)
                    {
                        if (response == null || !response)
                        {
                            ChatOutputHandler.chatError(ctx.getSource(), "Cancelled purging playerlogger");
                            return;
                        }
                        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator
                                .format("Purging all PL data before %s. Server could lag for a while!", startTimeStr));
                        TaskRegistry.runLater(new Runnable() {
                            @Override
                            public void run()
                            {
                                if (getServerPlayer(ctx.getSource()) != null)
                                {
                                    ModulePlayerLogger.getLogger().purgeOldData(startTime,
                                            getServerPlayer(ctx.getSource()));
                                }
                                else
                                    ModulePlayerLogger.getLogger().purgeOldData(startTime, null);
                            }
                        });
                    }
                };
                if (GetSource(ctx.getSource()) instanceof MinecraftServer)
                    handler.respond(true);
                else
                    try
                    {
                        Questioner.addChecked(getServerPlayer(ctx.getSource()),
                                Translator.format("Really purge all playerlogger data before %s?", startTimeStr),
                                handler);
                    }
                    catch (QuestionerStillActiveException e)
                    {
                        ChatOutputHandler.chatError(ctx.getSource(),
                                "Cannot run command because player is still answering a question. Please wait a moment");
                        return Command.SINGLE_SUCCESS;
                    }
            }
            break;
        default:
            ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_UNKNOWN_SUBCOMMAND, Arrays.toString(subCmd));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }
}
