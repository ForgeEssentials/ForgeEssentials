package com.forgeessentials.playerlogger.command;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.persistence.TypedQuery;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commons.MessageConstants;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.playerlogger.PlayerLogger;
import com.forgeessentials.playerlogger.PlayerLoggerEventHandler;
import com.forgeessentials.playerlogger.entity.Action;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.FeCommandParserArgs;
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
    public void parse(final FeCommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/pl stats: Show playerlogger stats");
            arguments.confirm("/pl picker: Picker Control");
            arguments.confirm("/pl search: Area Search Function");
            arguments.confirm("/pl purge: Purge old playerData");
            return;
        }
        arguments.tabComplete("stats");
        String subCmd = arguments.remove().toLowerCase();
        switch (subCmd)
        {
        case "picker":
            if (arguments.isEmpty())
            {
                arguments.confirm("/pl picker range [int] : Set picker range");
                arguments.confirm(
                        "/pl picker filter [player | command | block | explosion | burn | all] [searchCriteria] : Filter displayed blocks based on a criteria");
                break;
            }
            String subCmd2 = arguments.remove().toLowerCase();
            switch (subCmd2)
            {
            case "range":
                // Set the lookup range of the picker tool (Clock)
                int oldRange = PlayerLoggerEventHandler.pickerRange;
                PlayerLoggerEventHandler.pickerRange = arguments.parseInt();
                ChatUtil.sendMessage(arguments.sender,
                        ChatUtil.formatColors("Range changed from " + oldRange + " to " + PlayerLoggerEventHandler.pickerRange));
                break;
            case "filter":
                // filter event type shown (player, command, block, explosion)
                PlayerLoggerEventHandler.eventType = arguments.isEmpty() ? 0b11111 : 0;
                PlayerLoggerEventHandler.searchCriteria = "";
                while (!arguments.isEmpty())
                {
                    String subCmd3 = arguments.remove().toLowerCase();
                    switch (subCmd3)
                    {
                    case "player":
                        PlayerLoggerEventHandler.eventType |= 0b10000;
                        break;
                    case "command":
                        PlayerLoggerEventHandler.eventType |= 0b01000;
                        break;
                    case "block":
                        PlayerLoggerEventHandler.eventType |= 0b00100;
                        break;
                    case "explosion":
                        PlayerLoggerEventHandler.eventType |= 0b00010;
                        break;
                    case "burn":
                        PlayerLoggerEventHandler.eventType |= 0b00001;
                        break;
                    case "all":
                        PlayerLoggerEventHandler.eventType = 0b11111;
                        break;
                    default:
                        if (arguments.isEmpty())
                        {
                            PlayerLoggerEventHandler.searchCriteria = subCmd3;
                            if (PlayerLoggerEventHandler.eventType == 0)
                                PlayerLoggerEventHandler.eventType = 0b11111;
                        }
                        else
                            throw new TranslatedCommandException(MessageConstants.MSG_UNKNOWN_SUBCOMMAND, subCmd3);
                    }
                }
                ChatUtil.sendMessage(arguments.sender, ChatUtil.formatColors(outputFilterReadable(PlayerLoggerEventHandler.eventType)
                        + (PlayerLoggerEventHandler.searchCriteria.equals("") ? "" : "with SearchCriteria " + PlayerLoggerEventHandler.searchCriteria)));
                break;
            default:
                throw new TranslatedCommandException(MessageConstants.MSG_UNKNOWN_SUBCOMMAND, subCmd2);
            }

            break;
        case "search":
            if (arguments.isEmpty())
            {
                arguments.confirm("/pl search [time] [player | command | block | explosion | burn | all] [searchCriteria]");
                break;
            }
            long duration = arguments.parseTimeReadable();
            // filter event type shown (player, command, block, explosion)
            int eventType = 0;
            String searchCriteria;
            while (!arguments.isEmpty())
            {
                String subCmd3 = arguments.remove().toLowerCase();
                switch (subCmd3)
                {
                case "player":
                    eventType |= 0b10000;
                    break;
                case "command":
                    eventType |= 0b01000;
                    break;
                case "block":
                    eventType |= 0b00100;
                    break;
                case "explosion":
                    eventType |= 0b00010;
                    break;
                case "burn":
                    eventType |= 0b00001;
                    break;
                case "all":
                    eventType = 0b11111;
                    break;
                default:
                    if (arguments.isEmpty())
                        searchCriteria = subCmd3;
                    else
                        throw new TranslatedCommandException(MessageConstants.MSG_UNKNOWN_SUBCOMMAND, subCmd3);
                }
            }

            throw new TranslatedCommandException("Command %s is not implemented yet", getCommandName());

            // break;
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
            throw new TranslatedCommandException(MessageConstants.MSG_UNKNOWN_SUBCOMMAND, subCmd);
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
