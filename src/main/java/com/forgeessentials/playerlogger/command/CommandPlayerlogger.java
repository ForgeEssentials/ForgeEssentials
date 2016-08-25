package com.forgeessentials.playerlogger.command;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.persistence.TypedQuery;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.PlayerLoggerEventHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.playerlogger.PlayerLogger;
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

    @Override
    public void parse(final CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/pl stats: Show playerlogger stats");
            return;
        }
        arguments.tabComplete("stats");
        String subCmd = arguments.remove().toLowerCase();
        switch (subCmd)
        {
        case "picker":
            String subCmd2 = arguments.remove().toLowerCase();
            switch (subCmd2)
            {
            case "range":
                //Set the lookup range of the picker tool (Clock)
                PlayerLoggerEventHandler.pickerRange = arguments.parseInt();

                break;
            case "filter":
                //filter event type shown (player, command, block, explosion)
                PlayerLoggerEventHandler.eventType = 0;
                while (!arguments.isEmpty())
                {
                    String subCmd3 = arguments.remove().toLowerCase();
                    switch (subCmd3)
                    {
                    case "player":
                        PlayerLoggerEventHandler.eventType |= 0b1;
                        break;
                    case "command":
                        PlayerLoggerEventHandler.eventType |= 0b01;
                        break;
                    case "block":
                        PlayerLoggerEventHandler.eventType |= 0b001;
                        break;
                    case "explosion":
                        PlayerLoggerEventHandler.eventType |= 0b0001;
                        break;
                    case "all":
                        PlayerLoggerEventHandler.eventType = 0b1111;
                        break;
                    default:
                        if (arguments.isEmpty())
                            PlayerLoggerEventHandler.searchCriteria = subCmd3;
                        else
                            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd3);
                    }
                }
                break;
            default:
                throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd2);
            }
            break;
        case "search":
            long duration = arguments.parseTimeReadable();
            //filter event type shown (player, command, block, explosion)
            PlayerLoggerEventHandler.eventType = 0;
            while (!arguments.isEmpty())
            {
                String subCmd3 = arguments.remove().toLowerCase();
                switch (subCmd3)
                {
                case "player":
                    PlayerLoggerEventHandler.eventType |= 0b1;
                    break;
                case "command":
                    PlayerLoggerEventHandler.eventType |= 0b01;
                    break;
                case "block":
                    PlayerLoggerEventHandler.eventType |= 0b001;
                    break;
                case "explosion":
                    PlayerLoggerEventHandler.eventType |= 0b0001;
                    break;
                case "all":
                    PlayerLoggerEventHandler.eventType = 0b1111;
                    break;
                default:
                    if (arguments.isEmpty())
                        PlayerLoggerEventHandler.searchCriteria = subCmd3;
                    else
                        throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd3);
                }
            }


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
