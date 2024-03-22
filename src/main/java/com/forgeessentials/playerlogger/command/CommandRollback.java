package com.forgeessentials.playerlogger.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.selections.SelectionHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull String getPrimaryAlias()
    {
        return "rollback";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "rb" };
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("start")
                		.then(Commands.literal("currentTime")
                				.executes(CommandContext -> execute(CommandContext, "startN")))
                		.then(Commands.literal("customTime")
                				.then(Commands.argument("time", StringArgumentType.greedyString())
                                        .executes(CommandContext -> execute(CommandContext, "start")))))
                .then(Commands.literal("cancel").executes(CommandContext -> execute(CommandContext, "cancel")))
                .then(Commands.literal("confirm").executes(CommandContext -> execute(CommandContext, "confirm")))
                .then(Commands.literal("+")
                        .then(Commands.argument("time", StringArgumentType.string())
                                .executes(CommandContext -> execute(CommandContext, "+"))))
                .then(Commands.literal("-")
                        .then(Commands.argument("time", StringArgumentType.string())
                                .executes(CommandContext -> execute(CommandContext, "-"))))
                .then(Commands.literal("play")
                        .then(Commands.argument("speed", IntegerArgumentType.integer())
                                .executes(CommandContext -> execute(CommandContext, "play"))))
                .then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "help")))
                .then(Commands.literal("stop").executes(CommandContext -> execute(CommandContext, "stop")))
                .executes(CommandContext -> execute(CommandContext, "help"));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        switch (params)
        {
        case "help":
            help(ctx.getSource());
            break;
        case "startN":
        	startRollback(ctx, true);
            break;
        case "start":
            startRollback(ctx, false);
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

    private void startRollback(CommandContext<CommandSourceStack> ctx, boolean useCurrentTime) throws CommandRuntimeException
    {

        if (rollbacks.containsKey(getServerPlayer(ctx.getSource()).getGameProfile().getId()))
            cancelRollback(ctx);

        Selection area = SelectionHandler.getSelection(getServerPlayer(ctx.getSource()));
        if (area == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "No selection available. Please select a region first.");
            return;
        }

        int step = -60;
        if(!useCurrentTime) {
        	String time = StringArgumentType.getString(ctx, "time");
            try
            {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                Date parsedDate = format.parse(time);
                Date currentDate = Date.from(Instant.now());
                Date date = new Date();
                date.setSeconds(parsedDate.getSeconds());
                date.setMinutes(parsedDate.getMinutes());
                date.setHours(parsedDate.getHours());
                step = (int) ((date.getTime() - currentDate.getTime()) / 1000);
            }
            catch (ParseException e)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Invalid time format: %s, use HH:mm:ss format!", time);
                return;
            }
        }

        RollbackInfo rb = new RollbackInfo(getServerPlayer(ctx.getSource()), area);
        rollbacks.put(getServerPlayer(ctx.getSource()).getGameProfile().getId(), rb);
        rb.step(step);
        rb.previewChanges();

        ChatOutputHandler.chatConfirmation(ctx.getSource(),
                "Showing changes since " + FEConfig.FORMAT_DATE_TIME_SECONDS.format(rb.getTime()));
    }

    private void stepRollback(CommandContext<CommandSourceStack> ctx, int sec) throws CommandRuntimeException
    {
        try
        {
            sec = (int) (parseTimeReadable(StringArgumentType.getString(ctx, "time")) / 1000) * sec;
        }
        catch (FECommandParsingException e)
        {
            ChatOutputHandler.chatError(ctx.getSource(), e.error);
            return;
        }

        RollbackInfo rb = rollbacks.get(getServerPlayer(ctx.getSource()).getGameProfile().getId());
        if (rb == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "No rollback in progress. Start with /rollback first.");
            return;
        }

        rb.step(sec);
        rb.previewChanges();
        ChatOutputHandler.chatConfirmation(ctx.getSource(),
                "Showing changes since " + FEConfig.FORMAT_DATE_TIME_SECONDS.format(rb.getTime()));
    }

    private void confirmRollback(CommandContext<CommandSourceStack> ctx) throws CommandRuntimeException
    {
        RollbackInfo rb = rollbacks.remove(getServerPlayer(ctx.getSource()).getGameProfile().getId());
        if (rb == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "No rollback in progress. Start with /rollback first.");
            return;
        }

        rb.confirm();
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Successfully restored changes");
    }

    private void cancelRollback(CommandContext<CommandSourceStack> ctx) throws CommandRuntimeException
    {
        RollbackInfo rb = rollbacks.remove(getServerPlayer(ctx.getSource()).getGameProfile().getId());
        if (rb == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "No rollback in progress.");
            return;
        }

        rb.cancel();
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Cancelled active rollback");
    }

    private void playRollback(CommandContext<CommandSourceStack> ctx) throws CommandRuntimeException
    {

        int speed = 1;
        speed = IntegerArgumentType.getInteger(ctx, "speed");
        if (speed == 0)
            speed = 1;
        if (Math.abs(speed) > 10)
            speed = (int) (Math.signum(speed) * 10);

        RollbackInfo rb = rollbacks.get(getServerPlayer(ctx.getSource()).getGameProfile().getId());
        Timer playbackTimer = playbackTimers.get(getServerPlayer(ctx.getSource()).getGameProfile().getId());
        if (rb == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "No rollback in progress. Start with /rollback first.");
            return;
        }
        if (playbackTimer != null)
        {
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

    private void stopRollback(CommandContext<CommandSourceStack> ctx) throws CommandRuntimeException
    {
        RollbackInfo rb = rollbacks.get(getServerPlayer(ctx.getSource()).getGameProfile().getId());
        Timer playbackTimer = playbackTimers.get(getServerPlayer(ctx.getSource()).getGameProfile().getId());

        if (playbackTimer != null)
        {
            playbackTimer.cancel();
            playbackTimer = null;
        }
        if (rb == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "No rollback in progress. Start with /rollback first.");
            return;
        }
        if (rb.task == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "No playback running");
            return;
        }

        rb.task.cancel();
        rb.task = null;
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Stopped playback");
    }

    private static void help(CommandSourceStack sender)
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
