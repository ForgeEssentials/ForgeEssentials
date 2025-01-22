package com.forgeessentials.playerlogger.command;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import javax.persistence.TypedQuery;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ParserCommandBase;
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

public class CommandPlayerlogger extends ParserCommandBase
{

    @Override
    public String getCommandName()
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
    public String getCommandUsage(ICommandSender sender)
    {
        return "/pl stats";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
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
    public void parse(final CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/pl stats: Show playerlogger stats");
            arguments.confirm("/pl filter: Sets the players FilterConfig");
            arguments.confirm("/pl gfilter: Global /pl filter");
            arguments.confirm("/pl lookup: Looks up playerlogger data");
            arguments.confirm("/pl glookup: Global /pl lookup");
            arguments.confirm("/pl purge: Purge old playerData");
            return;
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
            if (!global) FilterConfig.perPlayerFilters.put(arguments.ident,fc);

            ChatOutputHandler.sendMessage(arguments.sender, ChatOutputHandler.formatColors((global ? "Global": arguments.ident.getUsername() + "'s") + " Picker set: \n" + fc.toReadableString()));

            break;
        case "glookup":
            global = true;
        case "lookup":
            if (!arguments.isEmpty() && arguments.peek().toLowerCase().equals("help"))
            {
                arguments.confirm("/pl [glookup | lookup] [[[x] [y] [z] [dim]?] | [player]]?  [pageSize]? [filterConfig]?");
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
                                arguments.error("Point must be in the form [x] [y] [z] [dim]?");
                                break;
                            }
                        }
                        catch (NoSuchElementException | TranslatedCommandException e)
                        {
                            if (next != null) arguments.args.addFirst(next);
                            arguments.args.addFirst(Integer.toString(tmp));
                        }
                    }
                    catch (TranslatedCommandException e)
                    {
                        arguments.args.addFirst(next);
                        EntityPlayer pl = arguments.parsePlayer(true, true).getPlayer();
                        p = new WorldPoint(pl.getEntityWorld(), pl.playerLocation);
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
                    fc = FilterConfig.getDefaultPlayerConfig(arguments.ident);
                else
                    fc = FilterConfig.globalConfig;
            }
            else
            {
                fc = new FilterConfig();
                fc.parse(arguments);
            }
            //ChatOutputHandler.sendMessage(arguments.sender, ChatOutputHandler.formatColors("Looking up: \n" + fc.toReadableString()));
            PlayerLoggerChecker.instance.CheckBlock(p,fc, arguments.sender, pageSize, newCheck);

            break;
        case "stats":
            if (arguments.isTabCompletion)
                return;
            showStats(arguments.sender);
            break;
        case "purge":
            if (arguments.isEmpty())
            {
                arguments.confirm("/pl purge <duration>: Purge all PL data that is older than <duration> in days");
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
                            arguments.error("Cancelled purging playerlogger");
                            return;
                        }
                        arguments.confirm("Purging all PL data before %s. Server could lag for a while!", startTimeStr);
                        TaskRegistry.runLater(new Runnable() {
                            @Override
                            public void run()
                            {
                                ModulePlayerLogger.getLogger().purgeOldData(startTime);
                            }
                        });
                    }
                };
                if (arguments.sender instanceof MinecraftServer)
                    handler.respond(true);
                else
                    Questioner.addChecked(arguments.sender, Translator.format("Really purge all playerlogger data before %s?", startTimeStr), handler);
            }
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd);
        }
    }

    public static void showStats(ICommandSender sender)
    {
        PlayerLogger logger = ModulePlayerLogger.getLogger();
        synchronized (logger)
        {
            TypedQuery<Long> qActionCount = logger.buildCountQuery(Action.class, null, null);
            long actionCount = qActionCount.getSingleResult();
            ChatOutputHandler.chatConfirmation(sender, String.format("Logged action count: %s", actionCount));
        }
    }

}
