package com.forgeessentials.playerlogger.command;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import javax.persistence.TypedQuery;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (arguments.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl stats: Show playerlogger stats");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl filter: Sets the players FilterConfig");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl gfilter: Global /pl filter");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl lookup: Looks up playerlogger data");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl glookup: Global /pl lookup");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl purge: Purge old playerData");
            return Command.SINGLE_SUCCESS;
        }
        arguments.tabComplete("stats", "gfilter", "filter", "glookup", "lookup", "purge");
        FilterConfig fc = null;
        String subCmd = arguments.remove().toLowerCase();
        boolean global = false;
        switch (subCmd)
        {
        case "gfilter":
            global = true;
        case "filter":
            if (arguments.isEmpty())
            {
                arguments.confirm("/pl [gfilter | filter] [filterConfig] : Filter displayed blocks based on a criteria");
                break;
            }
            global = arguments.senderPlayer == null || global;
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
            if (!arguments.isEmpty() && arguments.peek().toLowerCase().equals("help"))
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl [glookup | lookup] [[[x] [y] [z] [dim]?] | [player]]?  [pageSize]? [filterConfig]?");
                break;
            }
            global = arguments.senderPlayer == null || global;
            WorldPoint p = arguments.getSenderPoint();
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
                        PlayerEntity pl = arguments.parsePlayer(true, true).getPlayer();
                        p = new WorldPoint(pl.getEntityWorld(), pl.blockPosition());
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
            if (arguments.isEmpty())
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(),"/pl purge <duration>: Purge all PL data that is older than <duration> in days");
            }
            else
            {
                int days = arguments.parseInt();
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
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd);
        }
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
