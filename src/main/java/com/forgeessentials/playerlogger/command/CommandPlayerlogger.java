package com.forgeessentials.playerlogger.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.TypedQuery;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.FECommandParsingException;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.playerlogger.FilterConfig;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.playerlogger.PlayerLogger;
import com.forgeessentials.playerlogger.PlayerLoggerChecker;
import com.forgeessentials.playerlogger.FilterConfig.ActionEnum;
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

public class CommandPlayerlogger extends ForgeEssentialsCommandBuilder
{

    public CommandPlayerlogger(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "pl";
    }

    @Override
    public String getPermissionNode()
    {
        return ModulePlayerLogger.PERM_COMMAND + ".pl";
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("stats")
                        .executes(CommandContext -> execute(CommandContext, "stats")
                                )
                        )
                .then(Commands.literal("filter")
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "filter-help")
                                        )
                                )
                        .then(Commands.literal("action")
                                .then(Commands.literal("reset")
                                        .executes(CommandContext -> execute(CommandContext, "filter-action-reset")
                                                )
                                        )
                                .then(Commands.argument("type", StringArgumentType.greedyString())
                                        .suggests(SUGGEST_actiontabs)
                                        .executes(CommandContext -> execute(CommandContext, "filter-action-"+StringArgumentType.getString(CommandContext, "type"))
                                                )
                                        )
                                )
                        .then(Commands.literal("blockid")
                                .then(Commands.literal("reset")
                                        .executes(CommandContext -> execute(CommandContext, "filter-blockid-reset")
                                                )
                                        )
                                .then(Commands.argument("block", BlockStateArgument.block())
                                        .executes(CommandContext -> execute(CommandContext, "filter-blockid-block")
                                                )
                                        )
                                )
                        .then(Commands.literal("before")
                                .then(Commands.literal("reset")
                                        .executes(CommandContext -> execute(CommandContext, "filter-before-reset")
                                                )
                                        )
                                .then(Commands.argument("duration", StringArgumentType.string())
                                        .executes(CommandContext -> execute(CommandContext, "filter-before-"+StringArgumentType.getString(CommandContext, "duration"))
                                                )
                                        )
                                )
                        .then(Commands.literal("after")
                                .then(Commands.literal("reset")
                                        .executes(CommandContext -> execute(CommandContext, "filter-after-reset")
                                                )
                                        )
                                .then(Commands.argument("duration", StringArgumentType.string())
                                        .executes(CommandContext -> execute(CommandContext, "filter-after-"+StringArgumentType.getString(CommandContext, "duration"))
                                                )
                                        )
                                )
                        .then(Commands.literal("range")
                                .then(Commands.argument("range", IntegerArgumentType.integer())
                                        .executes(CommandContext -> execute(CommandContext, "filter-range-"+IntegerArgumentType.getInteger(CommandContext, "range"))
                                                )
                                        )
                                )
                        .then(Commands.literal("whitelist")
                                .then(Commands.literal("actions")
                                        .executes(CommandContext -> execute(CommandContext, "filter-whitelist-actions")
                                                )
                                        )
                                .then(Commands.literal("blocks")
                                        .executes(CommandContext -> execute(CommandContext, "filter-whitelist-blocks")
                                                )
                                        )
                                )
                        .then(Commands.literal("blacklist")
                                .then(Commands.literal("actions")
                                        .executes(CommandContext -> execute(CommandContext, "filter-blacklist-actions")
                                                )
                                        )
                                .then(Commands.literal("blocks")
                                        .executes(CommandContext -> execute(CommandContext, "filter-blacklist-blocks")
                                                )
                                        )
                                )
                        .then(Commands.literal("player")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .executes(CommandContext -> execute(CommandContext, "filter-player-"+StringArgumentType.getString(CommandContext, "name"))
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("gfilter")
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "gfilter-help")
                                        )
                                )
                        .executes(CommandContext -> execute(CommandContext, "gfilter")
                                )
                        )
                .then(Commands.literal("lookup")
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "lookup-help")
                                        )
                                )
                        .then(Commands.literal("Position")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .then(Commands.argument("dim", DimensionArgument.dimension())
                                                .then(Commands.argument("pageSize", IntegerArgumentType.integer())
                                                        .then(Commands.literal("Config")
                                                                .executes(CommandContext -> execute(CommandContext, "lookup-loc:dim:page:config")
                                                                        )
                                                                )
                                                        .executes(CommandContext -> execute(CommandContext, "lookup-loc:dim:page:noconfig")
                                                                )
                                                        )
                                                .then(Commands.literal("Config")
                                                        .executes(CommandContext -> execute(CommandContext, "lookup-loc:dim:nopage:config")
                                                                )
                                                        )
                                                .executes(CommandContext -> execute(CommandContext, "lookup-loc:dim:nopage:noconfig")
                                                        )
                                                )
                                        .then(Commands.argument("pageSize", IntegerArgumentType.integer())
                                                .then(Commands.literal("Config")
                                                        .executes(CommandContext -> execute(CommandContext, "lookup-loc:nodim:page:config")
                                                                )
                                                        )
                                                .executes(CommandContext -> execute(CommandContext, "lookup-loc:nodim:page:noconfig")
                                                        )
                                                )
                                        .then(Commands.literal("Config")
                                                .executes(CommandContext -> execute(CommandContext, "lookup-loc:nodim:nopage:config")
                                                        )
                                                )
                                        .executes(CommandContext -> execute(CommandContext, "lookup-loc:nodim:mopage:noconfig")
                                                )
                                        )
                                )
                        .then(Commands.literal("Player")
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .then(Commands.argument("pageSize", IntegerArgumentType.integer())
                                                .then(Commands.literal("Config")
                                                        .executes(CommandContext -> execute(CommandContext, "lookup-player:nodim:page:config")
                                                                )
                                                        )
                                                .executes(CommandContext -> execute(CommandContext, "lookup-player:nodim:page:noconfig")
                                                        )
                                                )
                                        .then(Commands.literal("Config")
                                                .executes(CommandContext -> execute(CommandContext, "lookup-player:nodim:nopage:config")
                                                        )
                                                )
                                        .executes(CommandContext -> execute(CommandContext, "lookup-player:nodim:nopage:noconfig")
                                                )
                                        )
                                )
                        .then(Commands.literal("Page")
                                .then(Commands.argument("pageSize", IntegerArgumentType.integer())
                                        .then(Commands.literal("Config")
                                                .executes(CommandContext -> execute(CommandContext, "lookup-blank:nodim:page:config")
                                                        )
                                                )
                                        .executes(CommandContext -> execute(CommandContext, "lookup-blank:nodim:page:noconfig")
                                                )
                                        )
                                )
                        .then(Commands.literal("Config")
                                .executes(CommandContext -> execute(CommandContext, "lookup-blank:nodim:nopage:config")
                                        )
                                )
                        .executes(CommandContext -> execute(CommandContext, "lookup-blank:nodim:nopage:noconfig")
                                )
                        )
                .then(Commands.literal("glookup")
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "glookup-help")
                                        )
                                )
                        .then(Commands.literal("Position")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .then(Commands.argument("dim", DimensionArgument.dimension())
                                                .then(Commands.argument("pageSize", IntegerArgumentType.integer())
                                                        .then(Commands.literal("Config")
                                                                .executes(CommandContext -> execute(CommandContext, "glookup-loc:dim:page:config")
                                                                        )
                                                                )
                                                        .executes(CommandContext -> execute(CommandContext, "glookup-loc:dim:page:noconfig")
                                                                )
                                                        )
                                                .then(Commands.literal("Config")
                                                        .executes(CommandContext -> execute(CommandContext, "glookup-loc:dim:nopage:config")
                                                                )
                                                        )
                                                .executes(CommandContext -> execute(CommandContext, "glookup-loc:dim:nopage:noconfig")
                                                        )
                                                )
                                        .then(Commands.argument("pageSize", IntegerArgumentType.integer())
                                                .then(Commands.literal("Config")
                                                        .executes(CommandContext -> execute(CommandContext, "glookup-loc:nodim:page:config")
                                                                )
                                                        )
                                                .executes(CommandContext -> execute(CommandContext, "glookup-loc:nodim:page:noconfig")
                                                        )
                                                )
                                        .then(Commands.literal("Config")
                                                .executes(CommandContext -> execute(CommandContext, "glookup-loc:nodim:nopage:config")
                                                        )
                                                )
                                        .executes(CommandContext -> execute(CommandContext, "glookup-loc:nodim:mopage:noconfig")
                                                )
                                        )
                                )
                        .then(Commands.literal("Player")
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .then(Commands.argument("pageSize", IntegerArgumentType.integer())
                                                .then(Commands.literal("Config")
                                                        .executes(CommandContext -> execute(CommandContext, "glookup-player:nodim:page:config")
                                                                )
                                                        )
                                                .executes(CommandContext -> execute(CommandContext, "glookup-player:nodim:page:noconfig")
                                                        )
                                                )
                                        .then(Commands.literal("Config")
                                                .executes(CommandContext -> execute(CommandContext, "glookup-player:nodim:nopage:config")
                                                        )
                                                )
                                        .executes(CommandContext -> execute(CommandContext, "glookup-player:nodim:nopage:noconfig")
                                                )
                                        )
                                )
                        .then(Commands.literal("Page")
                                .then(Commands.argument("pageSize", IntegerArgumentType.integer())
                                        .then(Commands.literal("Config")
                                                .executes(CommandContext -> execute(CommandContext, "glookup-blank:nodim:page:config")
                                                        )
                                                )
                                        .executes(CommandContext -> execute(CommandContext, "glookup-blank:nodim:page:noconfig")
                                                )
                                        )
                                )
                        .then(Commands.literal("Config")
                                .executes(CommandContext -> execute(CommandContext, "glookup-blank:nodim:nopage:config")
                                        )
                                )
                        .executes(CommandContext -> execute(CommandContext, "glookup-blank:nodim:nopage:noconfig")
                                )
                        )
                .then(Commands.literal("purge")
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "purge-help")
                                        )
                                )
                        .then(Commands.argument("daysToPurge", IntegerArgumentType.integer())
                                .executes(CommandContext -> execute(CommandContext, "purge-days")
                                        )
                                )
                        )
                .then(Commands.literal("help")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        );
    }

     public static final SuggestionProvider<CommandSource> SUGGEST_actiontabs = (ctx, builder) -> {
         List<String> actiontabs = new ArrayList<>();
         ActionEnum[] enums = ActionEnum.values();
         for (ActionEnum ae : enums)
         {
             actiontabs.add(ae.name());
         }
         actiontabs.add("reset");
         return ISuggestionProvider.suggest(actiontabs, builder);
      };

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl stats: Show playerlogger stats");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl filter: Sets the players FilterConfig");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl gfilter: Global /pl filter");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl lookup: Looks up playerlogger data");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl glookup: Global /pl lookup");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl purge: Purge old playerData");
            return Command.SINGLE_SUCCESS;
        }
        FilterConfig fc = null;
        String[] subCmd = params.split("-");
        boolean global = false;
        switch (subCmd[0])
        {
        case "gfilter":
            global = true;
        case "filter":
            if (subCmd[1]=="help")
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/pl [gfilter | filter] [filterConfig] : Filter displayed blocks based on a criteria");
                return Command.SINGLE_SUCCESS;
            }
            global = getServerPlayer(ctx.getSource()) == null || global;
            fc = global ? FilterConfig.globalConfig : new FilterConfig();
            List<String> arg1 = new ArrayList<String>(Arrays.asList(subCmd)); 
            arg1.remove(0);
            fc.parse(ctx, arg1);
            if (!global)
                FilterConfig.perPlayerFilters.put(getIdent(ctx.getSource()), fc);

            ChatOutputHandler.sendMessage(ctx.getSource(),
                    ChatOutputHandler.formatColors((global ? "Global" : getIdent(ctx.getSource()).getUsername() + "'s") + " Picker set: \n" + fc.toReadableString()));

            break;
        case "glookup":
            global = true;
        case "lookup":
            if (subCmd[0].toLowerCase().equals("help"))
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl [glookup | lookup] [[[x] [y] [z] [dim]?] | [player]]?  [pageSize]? [filterConfig]?");
                return Command.SINGLE_SUCCESS;
            }
            String[] subCmds = subCmd[1].split(":");
            global = getServerPlayer(ctx.getSource()) == null || global;
            WorldPoint p = getSenderPoint(ctx.getSource());
            if (subCmds[0].toLowerCase().equals("loc"))
            {
                p.setX(BlockPosArgument.getLoadedBlockPos(ctx, "pos").getX());
                p.setY(BlockPosArgument.getLoadedBlockPos(ctx, "pos").getY());
                p.setZ(BlockPosArgument.getLoadedBlockPos(ctx, "pos").getZ());
                if(subCmds[1].toLowerCase().equals("dim")) {
                    p.setDimension(DimensionArgument.getDimension(ctx, "dim"));
                }
            }
            else if (subCmd[0].toLowerCase().equals("player")) {
                PlayerEntity pl;
				try {
					pl = parsePlayer(StringArgumentType.getString(ctx, "name"),ctx.getSource(),true, true).getPlayer();
				} catch (FECommandParsingException e) {
					ChatOutputHandler.chatError(ctx.getSource(), e.error);
					return Command.SINGLE_SUCCESS;
				}
                p = new WorldPoint(pl.level, pl.blockPosition());
            }

            int pageSize = 0;
            boolean newCheck = true;
            if (subCmds[2].toLowerCase().equals("page"))
            {
                pageSize = IntegerArgumentType.getInteger(ctx, "pageSize");
                newCheck = false;
            }
            if (subCmds[2].toLowerCase().equals("noconfig"))
            {
                if (!global)
                    fc = FilterConfig.getDefaultPlayerConfig(getIdent(ctx.getSource()));
                else
                    fc = FilterConfig.globalConfig;
            }
            else
            {
                ChatOutputHandler.chatWarning(ctx.getSource(), "This subCommand has not been ported yet!");
                fc = new FilterConfig();
                List<String> arg2 = new ArrayList<String>(Arrays.asList(subCmd)); 
                arg2.remove(0);
                arg2.remove(1);
                fc.parse(ctx, arg2);
            }
            ChatOutputHandler.sendMessage(ctx.getSource(), ChatOutputHandler.formatColors("Looking up: \n" + fc.toReadableString()));
            PlayerLoggerChecker.instance.CheckBlock(p, fc, ctx.getSource(), pageSize, newCheck);
            break;
        case "stats":
            showStats(ctx.getSource());
            break;
        case "purge":
            if (subCmd[1]=="help")
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl purge <duration>: Purge all PL data that is older than <duration> in days");
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
                            ChatOutputHandler.chatError(ctx.getSource(),"Cancelled purging playerlogger");
                            return;
                        }
                        ChatOutputHandler.chatConfirmation(ctx.getSource(),Translator.format("Purging all PL data before %s. Server could lag for a while!", startTimeStr));
                        TaskRegistry.runLater(new Runnable() {
                            @Override
                            public void run()
                            {
                                ModulePlayerLogger.getLogger().purgeOldData(startTime);
                            }
                        });
                    }
                };
                if (GetSource(ctx.getSource()) instanceof MinecraftServer)
                    handler.respond(true);
				else
					try {
						Questioner.addChecked(ctx.getSource(), Translator.format("Really purge all playerlogger data before %s?", startTimeStr), handler);
					} catch (QuestionerStillActiveException e) {
						ChatOutputHandler.chatError(ctx.getSource(), "Cannot run command because player is still answering a question. Please wait a moment");
		            	return Command.SINGLE_SUCCESS;
					}
            }
            break;
        default:
        	ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd.toString());
        	return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    public static void showStats(CommandSource sender)
    {
        PlayerLogger logger;
        synchronized (logger = ModulePlayerLogger.getLogger())
        {
            TypedQuery<Long> qActionCount = logger.buildCountQuery(Action.class, null, null);
            long actionCount = qActionCount.getSingleResult();
            ChatOutputHandler.chatConfirmation(sender, String.format("Logged action count: %s", actionCount));
        }
    }
}
