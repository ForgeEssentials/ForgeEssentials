package com.forgeessentials.playerlogger.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.selections.SelectionHandler;

public class CommandRollback extends ParserCommandBase
{

    public static final String PERM = ModulePlayerLogger.PERM_COMMAND + ".rollback";
    public static final String PERM_ALL = PERM + Zone.ALL_PERMS;
    public static final String PERM_PREVIEW = PERM + ".preview";

    private static final String[] subCommands = { "help", "start", "cancel", "confirm", "play", "stop", "+", "-" };

    private Map<UUID, RollbackInfo> rollbacks = new HashMap<>();

    private Timer playbackTimer = new Timer();

    @Override
    public String getCommandName()
    {
        return "rollback";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "rb" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/rb: Rollback changes in the world with playerlogger";
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void parse(CommandParserArgs args) throws CommandException
    {
        if (args.isEmpty())
        {
            startRollback(args);
            return;
        }

        args.tabComplete(subCommands);
        String arg = args.remove().toLowerCase();
        switch (arg)
        {
        case "help":
            help(args.sender);
            break;
        case "start":
            startRollback(args);
            break;
        case "cancel":
            cancelRollback(args);
            break;
        case "confirm":
            confirmRollback(args);
            break;
        case "+":
            stepRollback(args, 1);
            break;
        case "-":
            stepRollback(args, -1);
            break;
        case "play":
            playRollback(args);
            break;
        case "stop":
            stopRollback(args);
            break;
        default:
            throw new TranslatedCommandException("Unknown subcommand");
        }
    }

    @SuppressWarnings("deprecation")
    private void startRollback(CommandParserArgs args) throws CommandException
    {
        args.checkPermission(PERM_PREVIEW);

        if (args.isTabCompletion)
            return;

        if (rollbacks.containsKey(args.senderPlayer.getPersistentID()))
            cancelRollback(args);

        Selection area = SelectionHandler.getSelection(args.senderPlayer);
        if (area == null)
            throw new TranslatedCommandException("No selection available. Please select a region first.");
        
        int step = -60;
        if (!args.isEmpty()) {
            String time = args.remove();
            String day = null;
            if (!args.isEmpty()) {
                day = time;
                time = args.remove();
            }
            try
            {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                Date parsedDate = format.parse(time);

                Date currentDate = new Date();
                Date date;

                if (day != null)
                {
                    SimpleDateFormat dayFormat = new SimpleDateFormat("y-M-d");
                    date = dayFormat.parse(day);
                } else {
                    date = new Date();
                }
                date.setSeconds(parsedDate.getSeconds());
                date.setMinutes(parsedDate.getMinutes());
                date.setHours(parsedDate.getHours());
                step = (int)((date.getTime() - currentDate.getTime()) / 1000);
            }
            catch (ParseException e)
            {
                throw new TranslatedCommandException("Invalid time format: %s", time);
            }
        }

        RollbackInfo rb = new RollbackInfo(args.senderPlayer, area);
        rollbacks.put(args.senderPlayer.getPersistentID(), rb);
        rb.step(step);
        rb.previewChanges();

        ChatOutputHandler.chatConfirmation(args.sender, "Showing changes since " + FEConfig.FORMAT_DATE_TIME_SECONDS.format(rb.getTime()));
    }

    private void stepRollback(CommandParserArgs args, int sec) throws CommandException
    {
        args.checkPermission(PERM_PREVIEW);

        if (!args.isEmpty())
            sec = (int) (args.parseTimeReadable() / 1000) * sec;

        if (args.isTabCompletion)
            return;

        RollbackInfo rb = rollbacks.get(args.senderPlayer.getPersistentID());
        if (rb == null)
            throw new TranslatedCommandException("No rollback in progress. Start with /rollback first.");

        rb.step(sec);
        rb.previewChanges();
        ChatOutputHandler.chatConfirmation(args.sender, "Showing changes since " + FEConfig.FORMAT_DATE_TIME_SECONDS.format(rb.getTime()));
    }

    private void confirmRollback(CommandParserArgs args) throws CommandException
    {
        args.checkPermission(PERM);

        if (args.isTabCompletion)
            return;

        RollbackInfo rb = rollbacks.remove(args.senderPlayer.getPersistentID());
        if (rb == null)
            throw new TranslatedCommandException("No rollback in progress. Start with /rollback first.");

        rb.confirm();
        ChatOutputHandler.chatConfirmation(args.sender, "Successfully restored changes");
    }

    private void cancelRollback(CommandParserArgs args) throws CommandException
    {
        RollbackInfo rb = rollbacks.remove(args.senderPlayer.getPersistentID());
        if (rb == null)
            throw new TranslatedCommandException("No rollback in progress.");

        rb.cancel();
        ChatOutputHandler.chatConfirmation(args.sender, "Cancelled active rollback");
    }

    private void playRollback(CommandParserArgs args) throws CommandException
    {
        args.checkPermission(PERM_PREVIEW);

        int speed = 1;
        if (!args.isEmpty())
            speed = parseInt(args.remove());
        if (speed == 0)
            speed = 1;
        if (Math.abs(speed) > 10)
            speed = (int) (Math.signum(speed) * 10);

        if (args.isTabCompletion)
            return;

        RollbackInfo rb = rollbacks.get(args.senderPlayer.getPersistentID());
        if (rb == null)
            throw new TranslatedCommandException("No rollback in progress. Start with /rollback first.");

        if (rb.task != null)
        {
            rb.task.cancel();
            rb.task = null;
            ChatOutputHandler.chatConfirmation(args.sender, "Stopped playback");
        }
        else
        {
            rb.task = new RollbackInfo.PlaybackTask(rb, (int) (Math.signum(speed)));
            playbackTimer.schedule(rb.task, 1000, 1000 / Math.abs(speed));
            ChatOutputHandler.chatConfirmation(args.sender, "Started playback");
        }
    }

    private void stopRollback(CommandParserArgs args) throws CommandException
    {
        args.checkPermission(PERM_PREVIEW);

        if (args.isTabCompletion)
            return;

        RollbackInfo rb = rollbacks.get(args.senderPlayer.getPersistentID());
        if (rb == null)
            throw new TranslatedCommandException("No rollback in progress. Start with /rollback first.");
        if (rb.task == null)
            throw new TranslatedCommandException("No playback running");

        rb.task.cancel();
        rb.task = null;
        ChatOutputHandler.chatConfirmation(args.sender, "Stopped playback");
    }

    private static void help(ICommandSender sender) throws CommandException
    {
        ChatOutputHandler.chatConfirmation(sender, "/rollback: Start rollback");
        ChatOutputHandler.chatConfirmation(sender, "/rollback start [time]: Start rollback at specified time");
        ChatOutputHandler.chatConfirmation(sender, "/rollback + [duration]: Go forward in time");
        ChatOutputHandler.chatConfirmation(sender, "/rollback - [duration]: Go back in time");
        ChatOutputHandler.chatConfirmation(sender, "/rollback play [speed]: Playback changes like a video");
        ChatOutputHandler.chatConfirmation(sender, "/rollback stop: Stop playback");
        ChatOutputHandler.chatConfirmation(sender, "/rollback confirm: Confirm changes");
        ChatOutputHandler.chatConfirmation(sender, "/rollback cancel: Cancel rollback");
    }

}
