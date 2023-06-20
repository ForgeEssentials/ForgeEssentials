package com.forgeessentials.playerlogger.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.FECommandParsingException;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.selections.SelectionHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandRollback extends ForgeEssentialsCommandBuilder
{

    public CommandRollback(boolean enabled)
    {
        super(enabled);
    }

    public static final String PERM = ModulePlayerLogger.PERM_COMMAND + ".rollback";
    public static final String PERM_ALL = PERM + Zone.ALL_PERMS;
    public static final String PERM_PREVIEW = PERM + ".preview";

    static private Map<UUID, RollbackInfo> rollbacks = new HashMap<>();

    static private Map<UUID, Timer> playbackTimers = new HashMap<>();


    @Override
    public String getPrimaryAlias()
    {
        return "rollback";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "rb" };
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("start")
                        .then(Commands.argument("time", StringArgumentType.string())
                                .executes(CommandContext -> execute(CommandContext, "start")
                                        )
                                )
                        )
                .then(Commands.literal("cancel")
                        .executes(CommandContext -> execute(CommandContext, "cancel")
                                )
                        )
                .then(Commands.literal("confirm")
                        .executes(CommandContext -> execute(CommandContext, "confirm")
                                )
                        )
                .then(Commands.literal("+")
                        .then(Commands.argument("time", StringArgumentType.string())
                                .executes(CommandContext -> execute(CommandContext, "+")
                                        )
                                )
                        )
                .then(Commands.literal("-")
                        .then(Commands.argument("time", StringArgumentType.string())
                                .executes(CommandContext -> execute(CommandContext, "-")
                                        )
                                )
                        )
                .then(Commands.literal("play")
                        .then(Commands.argument("speed", IntegerArgumentType.integer())
                                .executes(CommandContext -> execute(CommandContext, "play")
                                        )
                                )
                        )
                .then(Commands.literal("help")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        )
                .then(Commands.literal("stop")
                        .executes(CommandContext -> execute(CommandContext, "stop")
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "help")
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        switch (params)
        {
        case "help":
            help(ctx.getSource());
            break;
        case "start":
            startRollback(ctx);
            break;
        case "cancel":
            cancelRollback(ctx);
            break;
        case "confirm":
            confirmRollback(ctx);
            break;
        case "+":
            stepRollback(ctx, 1);
            break;
        case "-":
            stepRollback(ctx, -1);
            break;
        case "play":
            playRollback(ctx);
            break;
        case "stop":
            stopRollback(ctx);
            break;
        default:
        	ChatOutputHandler.chatError(ctx.getSource(), "Unknown subcommand %s", params);
        	return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    private void startRollback(CommandContext<CommandSource> ctx) throws CommandException
    {
        if(!hasPermission(ctx.getSource(),PERM_PREVIEW)) {
        	ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
        	return;
        }


        if (rollbacks.containsKey(getServerPlayer(ctx.getSource()).getUUID()))
            cancelRollback(ctx);

        Selection area = SelectionHandler.getSelection(getServerPlayer(ctx.getSource()));
        if (area == null) {
        	ChatOutputHandler.chatError(ctx.getSource(), "No selection available. Please select a region first.");
        	return;
        }

        int step = -60;
        String time = StringArgumentType.getString(ctx, "time");
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Date parsedDate = format.parse(time);
            Date currentDate = new Date();
            Date date = new Date();
            date.setSeconds(parsedDate.getSeconds());
            date.setMinutes(parsedDate.getMinutes());
            date.setHours(parsedDate.getHours());
            step = (int) ((date.getTime() - currentDate.getTime()) / 1000);
        }
        catch (ParseException e)
        {
        	ChatOutputHandler.chatError(ctx.getSource(), "Invalid time format: %s", time);
        	return;
        }

        RollbackInfo rb = new RollbackInfo(getServerPlayer(ctx.getSource()), area);
        rollbacks.put(getServerPlayer(ctx.getSource()).getUUID(), rb);
        rb.step(step);
        rb.previewChanges();

        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Showing changes since " + FEConfig.FORMAT_DATE_TIME_SECONDS.format(rb.getTime()));
    }

    private void stepRollback(CommandContext<CommandSource> ctx, int sec) throws CommandException
    {
        if(!hasPermission(ctx.getSource(),PERM_PREVIEW)) {
        	ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
        	return;
        }

        try {
			sec = (int) (parseTimeReadable(StringArgumentType.getString(ctx, "time")) / 1000) * sec;
		} catch (FECommandParsingException e) {
			ChatOutputHandler.chatError(ctx.getSource(), e.error);
			return;
		}


        RollbackInfo rb = rollbacks.get(getServerPlayer(ctx.getSource()).getUUID());
        if (rb == null) {
        	ChatOutputHandler.chatError(ctx.getSource(), "No rollback in progress. Start with /rollback first.");
        	return;
        }

        rb.step(sec);
        rb.previewChanges();
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Showing changes since " + FEConfig.FORMAT_DATE_TIME_SECONDS.format(rb.getTime()));
    }

    private void confirmRollback(CommandContext<CommandSource> ctx) throws CommandException
    {
        if(!hasPermission(ctx.getSource(),PERM)) {
        	ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
        	return;
        }

        RollbackInfo rb = rollbacks.remove(getServerPlayer(ctx.getSource()).getUUID());
        if (rb == null) {
        	ChatOutputHandler.chatError(ctx.getSource(), "No rollback in progress. Start with /rollback first.");
        	return;
        }

        rb.confirm();
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Successfully restored changes");
    }

    private void cancelRollback(CommandContext<CommandSource> ctx) throws CommandException
    {
        RollbackInfo rb = rollbacks.remove(getServerPlayer(ctx.getSource()).getUUID());
        if (rb == null) {
        	ChatOutputHandler.chatError(ctx.getSource(), "No rollback in progress.");
        	return;
        }

        rb.cancel();
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Cancelled active rollback");
    }

    private void playRollback(CommandContext<CommandSource> ctx) throws CommandException
    {
        if(!hasPermission(ctx.getSource(),PERM_PREVIEW)) {
        	ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
        	return;
        }

        int speed = 1;
        speed = IntegerArgumentType.getInteger(ctx, "speed");
        if (speed == 0)
            speed = 1;
        if (Math.abs(speed) > 10)
            speed = (int) (Math.signum(speed) * 10);


        RollbackInfo rb = rollbacks.get(getServerPlayer(ctx.getSource()).getUUID());
        Timer playbackTimer = playbackTimers.get(getServerPlayer(ctx.getSource()).getUUID());
        if (rb == null) {
        	ChatOutputHandler.chatError(ctx.getSource(), "No rollback in progress. Start with /rollback first.");
        	return;
        }
        if(playbackTimer != null) {
            playbackTimer.cancel();
            playbackTimer = null;
        }
        if (rb.task != null)
        {
            rb.task.cancel();
            rb.task = null;
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Stopped playback");
        }
        else
        {
            rb.task = new RollbackInfo.PlaybackTask(rb, (int) (Math.signum(speed)));
            playbackTimer = new Timer("FERollbackTimer");
            playbackTimer.schedule(rb.task, 1000, 1000 / Math.abs(speed));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Started playback");
        }
    }

    private void stopRollback(CommandContext<CommandSource> ctx) throws CommandException
    {
        if(!hasPermission(ctx.getSource(),PERM_PREVIEW)) {
        	ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
        	return;
        }


        RollbackInfo rb = rollbacks.get(getServerPlayer(ctx.getSource()).getUUID());
        Timer playbackTimer = playbackTimers.get(getServerPlayer(ctx.getSource()).getUUID());

        if(playbackTimer != null) {
            playbackTimer.cancel();
            playbackTimer = null;
        }
        if (rb == null) {
        	ChatOutputHandler.chatError(ctx.getSource(), "No rollback in progress. Start with /rollback first.");
        	return;
        }
        if (rb.task == null) {
        	ChatOutputHandler.chatError(ctx.getSource(), "No playback running");
        	return;
        }

        rb.task.cancel();
        rb.task = null;
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Stopped playback");
    }

    private static void help(CommandSource sender)
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
