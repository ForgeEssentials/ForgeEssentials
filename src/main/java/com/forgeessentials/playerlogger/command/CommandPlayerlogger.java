package com.forgeessentials.playerlogger.command;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import javax.persistence.TypedQuery;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.playerlogger.FilterConfig;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.playerlogger.PlayerLogger;
import com.forgeessentials.playerlogger.PlayerLoggerChecker;
import com.forgeessentials.playerlogger.entity.Action;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

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

    private String outputFilterReadable(int filter)
    {
        String out = "Set to filter:";
        if (filter != 0b11111)
        {
            if ((0b10000 & filter) != 0)
            {
                out += " playerEvents, ";
            }
            if ((0b01000 & filter) != 0)
            {
                out += " commandEvents, ";
            }
            if ((0b00100 & filter) != 0)
            {
                out += " blockEvents, ";
            }
            if ((0b00010 & filter) != 0)
            {
                out += " explosionEvents, ";
            }
            if ((0b00001 & filter) != 0)
            {
                out += " burnEvents, ";
            }

        }
        else
        {
            out += " All events, ";
        }
        return out;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("stats")
                        .executes(CommandContext -> execute(CommandContext, "stats")
                                )
                        )
                .then(Commands.literal("filter")
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "filter-help")
                                        )
                                )
                        .executes(CommandContext -> execute(CommandContext, "filter")
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
                        .executes(CommandContext -> execute(CommandContext, "lookup")
                                )
                        )
                .then(Commands.literal("glookup")
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "glookup-help")
                                        )
                                )
                        .executes(CommandContext -> execute(CommandContext, "glookup")
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

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString().equals("help"))
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
        String[] subCmd = params.toString().split("-");
        boolean global = false;
        switch (subCmd[0])
        {
        case "gfilter":
            global = true;
        case "filter":
            if (subCmd[1]=="help")
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/pl [gfilter | filter] [filterConfig] : Filter displayed blocks based on a criteria");
                return Command.SINGLE_SUCCESS;;
            }
            global = getServerPlayer(ctx.getSource()) == null || global;
            fc = global ? FilterConfig.globalConfig : new FilterConfig();
            fc.parse(arguments);
            if (!global)
                FilterConfig.perPlayerFilters.put(getIdent(ctx.getSource()), fc);

            ChatOutputHandler.sendMessage(ctx.getSource(),
                    ChatOutputHandler.formatColors((global ? "Global" : getIdent(ctx.getSource()).getUsername() + "'s") + " Picker set: \n" + fc.toReadableString()));

            break;
        case "glookup":
            global = true;
        case "lookup":
            if (subCmd[1].toLowerCase().equals("help"))
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl [glookup | lookup] [[[x] [y] [z] [dim]?] | [player]]?  [pageSize]? [filterConfig]?");
                return Command.SINGLE_SUCCESS;;
            }
            global = getServerPlayer(ctx.getSource()) == null || global;
            WorldPoint p = getSenderPoint(ctx.getSource());
            String next = arguments.peek();
            if (next != null)
            {
                if (!FilterConfig.keywords.contains(next))
                {
                    try
                    {
                        int tmp = arguments.parseInt();

                        try
                        {
                            next = arguments.args.peek();
                            p.setY(arguments.parseInt());
                            p.setX(tmp);
                            try
                            {
                                p.setZ(arguments.parseInt());

                                try
                                {
                                    next = arguments.peek();
                                    if (next != null)
                                        p.setDimension(arguments.parseInt());
                                }
                                catch (TranslatedCommandException e)
                                {
                                    arguments.args.addFirst(next);
                                }
                            }
                            catch (NoSuchElementException | TranslatedCommandException e)
                            {
                                ChatOutputHandler.chatError(ctx.getSource(),"Point must be in the form [x] [y] [z] [dim]?");
                                break;
                            }
                        }
                        catch (NoSuchElementException | TranslatedCommandException e)
                        {
                            if (next != null)
                                arguments.args.addFirst(next);
                            arguments.args.addFirst(Integer.toString(tmp));
                        }
                    }
                    catch (TranslatedCommandException e)
                    {
                        arguments.args.addFirst(next);
                        PlayerEntity pl = parsePlayer(StringArgumentType.getString(ctx, "name"),ctx.getSource(),true, true).getPlayer();
                        p = new WorldPoint(pl.level, pl.blockPosition());
                    }
                }
            }

            int pageSize = 0;
            boolean newCheck = true;
            if (!arguments.isEmpty())
            {
                next = arguments.peek();
                try
                {
                    pageSize = arguments.parseInt();
                    newCheck = false;
                }
                catch (TranslatedCommandException e)
                {
                    arguments.args.addFirst(next);
                }
            }
            if (arguments.isEmpty())
            {
                if (!global)
                    fc = FilterConfig.getDefaultPlayerConfig(getIdent(ctx.getSource()));
                else
                    fc = FilterConfig.globalConfig;
            }
            else
            {
                fc = new FilterConfig();
                fc.parse(arguments);
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
                    Questioner.addChecked(ctx.getSource(), Translator.format("Really purge all playerlogger data before %s?", startTimeStr), handler);
            }
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd.toString());
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
