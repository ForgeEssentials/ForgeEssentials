package com.forgeessentials.playerlogger.command;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.selections.SelectionHandler;

public class CommandRollback extends ForgeEssentialsCommandBase {
    
    public static final String PERM = ModulePlayerLogger.PERM + ".rollback";
    public static final String PERM_ALL = PERM + Zone.ALL_PERMS;
    public static final String PERM_PREVIEW = PERM + ".preview";

    private static final String[] subCommands = { "help", "start", "cancel", "confirm", "play", "+", "-" };

    protected static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

    private Map<UUID, RollbackInfo> rollbacks = new HashMap<>();
    
    private Timer playbackTimer = new Timer();
    
    @Override
    public String getCommandName()
    {
        return "rollback";
    }

    @Override
    public List<String> getCommandAliases()
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add("rb");
        return list;
    }

    public void parse(CommandParserArgs args)
    {
        if (args.tabComplete(subCommands))
            return;
        
        if (args.isEmpty())
        {
            startRollback(args);
            return;
        }
        
        String arg = args.remove().toLowerCase();
        switch (arg) {
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
            stepRollback(args, true);
            break;
        case "-":
            stepRollback(args, false);
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

    private void startRollback(CommandParserArgs args)
    {
        args.checkPermission(PERM_PREVIEW);
        
        if (args.isTabCompletion)
            return;
        
        if (rollbacks.containsKey(args.senderPlayer.getPersistentID()))
            cancelRollback(args);

        Selection area = SelectionHandler.selectionProvider.getSelection(args.senderPlayer);
        if (area == null)
            throw new TranslatedCommandException("No selection available. Please select a region first.");
        
        RollbackInfo rb = new RollbackInfo(args.senderPlayer, area);
        rollbacks.put(args.senderPlayer.getPersistentID(), rb);
        rb.stepBackward();
        rb.previewChanges();
        
        OutputHandler.chatConfirmation(args.sender, "Showing changes since " + TIME_FORMAT.format(rb.getTime()));
    }

    private void stepRollback(CommandParserArgs args, boolean backward)
    {
        args.checkPermission(PERM_PREVIEW);

        if (args.isTabCompletion)
            return;

        RollbackInfo rb = rollbacks.get(args.senderPlayer.getPersistentID());
        if (rb == null)
            throw new TranslatedCommandException("No rollback in progress. Start with /rollback first.");
        
        if (backward)
            rb.stepBackward();
        else
            rb.stepForward();
        
        rb.previewChanges();
        OutputHandler.chatConfirmation(args.sender, "Showing changes since " + TIME_FORMAT.format(rb.getTime()));
    }

    private void confirmRollback(CommandParserArgs args)
    {
        args.checkPermission(PERM);
        
        if (args.isTabCompletion)
            return;
        
        RollbackInfo rb = rollbacks.remove(args.senderPlayer.getPersistentID());
        if (rb == null)
            throw new TranslatedCommandException("No rollback in progress. Start with /rollback first.");
        
        rb.confirm();
        OutputHandler.chatConfirmation(args.sender, "Successfully restored changes");
    }

    private void cancelRollback(CommandParserArgs args)
    {
        RollbackInfo rb = rollbacks.remove(args.senderPlayer.getPersistentID());
        if (rb == null)
            throw new TranslatedCommandException("No rollback in progress.");
        
        rb.cancel();
        OutputHandler.chatConfirmation(args.sender, "Cancelled active rollback");
    }
    
    private void playRollback(CommandParserArgs args)
    {
        args.checkPermission(PERM_PREVIEW);

        int speed = 1;
        if (!args.isEmpty())
            speed = parseInt(args.sender, args.remove());
        if (speed < 0)
            speed = 1;
        
        if (args.isTabCompletion)
            return;

        RollbackInfo rb = rollbacks.get(args.senderPlayer.getPersistentID());
        if (rb == null)
            throw new TranslatedCommandException("No rollback in progress. Start with /rollback first.");
        
        if (rb.task != null)
        {
            rb.task.cancel();
            rb.task = null;
            OutputHandler.chatConfirmation(args.sender, "Stopped playback");
        }
        else
        {
            rb.task = new RollbackInfo.PlaybackTask(rb, 1);
            playbackTimer.schedule(rb.task, 1000, 1000 / speed);
            OutputHandler.chatConfirmation(args.sender, "Started playback");
        }
    }

    private void stopRollback(CommandParserArgs args)
    {
        args.checkPermission(PERM_PREVIEW);

        int speed = 1;
        if (!args.isEmpty())
            speed = parseInt(args.sender, args.remove());
        if (speed < 0)
            speed = 1;
        
        if (args.isTabCompletion)
            return;

        RollbackInfo rb = rollbacks.get(args.senderPlayer.getPersistentID());
        if (rb == null)
            throw new TranslatedCommandException("No rollback in progress. Start with /rollback first.");
        if (rb.task == null)
            throw new TranslatedCommandException("No playback running");
        
        rb.task.cancel();
        rb.task = null;
        OutputHandler.chatConfirmation(args.sender, "Stopped playback");
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        CommandParserArgs arguments = new CommandParserArgs(this, args, sender, false);
        parse(arguments);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        CommandParserArgs arguments = new CommandParserArgs(this, args, sender, true);
        parse(arguments);
        return arguments.tabCompletion;
    }

    private static void help(ICommandSender sender)
    {
        OutputHandler.chatConfirmation(sender, "/rollback [minutes]: Start rollback");
        OutputHandler.chatConfirmation(sender, "/rollback + [min] [sec]: Go back in time");
        OutputHandler.chatConfirmation(sender, "/rollback - [min] [sec]: Go forward in time");
        OutputHandler.chatConfirmation(sender, "/rollback confirm: Confirm changes");
        OutputHandler.chatConfirmation(sender, "/rollback cancel: Cancel rollback");
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/zone: Displays command help";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

}
