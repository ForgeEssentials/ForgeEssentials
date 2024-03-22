package com.forgeessentials.teleport.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.teleport.TeleportModule;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.questioner.QuestionerException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandTPA extends ForgeEssentialsCommandBuilder
{

    public CommandTPA(boolean enabled)
    {
        super(enabled);
    }

    public static final String PERM_HERE = TeleportModule.PERM_TPA + ".here";
    public static final String PERM_LOCATION = TeleportModule.PERM_TPA + ".loc";

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "tpa";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_HERE, DefaultPermissionLevel.ALL,
                "Allow teleporting other players to your own location (inversed TPA)");
        APIRegistry.perms.registerPermission(PERM_LOCATION, DefaultPermissionLevel.OP,
                "Allow teleporting other players to any location");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "help")))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("position")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(CommandContext -> execute(CommandContext, "pos"))))
                        .then(Commands.literal("here").executes(CommandContext -> execute(CommandContext, "here")))
                        .executes(CommandContext -> execute(CommandContext, "toP")));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/tpa <player>: Request being teleported to another player");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/tpa <player> <here|x y z>: Propose another player to be teleported");
            return Command.SINGLE_SUCCESS;
        }

        final UserIdent player = getIdent(EntityArgument.getPlayer(ctx, "player"));
        if (player.getPlayerMP().equals(getServerPlayer(ctx.getSource())))
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Can't TPA to yourself!");
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("toP"))
        {
            try
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Waiting for response by %s", player.getUsernameOrUuid()));
                Questioner.addChecked(player.getPlayer(),
                        Translator.format("Allow teleporting %s to your location?",
                                getServerPlayer(ctx.getSource()).getDisplayName().getString()),
                        new QuestionerCallback() {
                            @Override
                            public void respond(Boolean response)
                            {
                                if (response == null)
                                    ChatOutputHandler.chatError(ctx.getSource(), "TPA request timed out");
                                else if (!response)
                                    ChatOutputHandler.chatError(ctx.getSource(), "TPA declined");
                                else
                                    try
                                    {
                                        TeleportHelper.teleport(getServerPlayer(ctx.getSource()),
                                                new WarpPoint(player.getPlayer()));
                                    }
                                    catch (CommandRuntimeException e)
                                    {
                                        ChatOutputHandler.chatError(ctx.getSource(), e.getMessage());
                                    }
                            }
                        }, 20);
            }
            catch (QuestionerException.QuestionerStillActiveException e)
            {
                ChatOutputHandler.chatError(ctx.getSource(),
                        "Cannot run command because player is still answering a question. Please wait a moment");
                return Command.SINGLE_SUCCESS;
            }
            return Command.SINGLE_SUCCESS;
        }

        final WarpPoint point;
        final String locationName;
        if (params.equals("here"))
        {
            point = new WarpPoint(getServerPlayer(ctx.getSource()));
            locationName = getServerPlayer(ctx.getSource()).getDisplayName().getString();
        }
        else
        {
            if (!hasPermission(ctx.getSource(), PERM_LOCATION))
            {
                ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
                return Command.SINGLE_SUCCESS;
            }
            point = new WarpPoint(getServerPlayer(ctx.getSource()).getLevel().dimension(),
                    BlockPosArgument.getLoadedBlockPos(ctx, "pos"), player.getPlayer().getXRot(), player.getPlayer().getYRot());
            locationName = point.toReadableString();
        }

        try
        {
            Questioner.addChecked(player.getPlayer(),
                    Translator.format("Do you want to be teleported to %s?", locationName), new QuestionerCallback() {
                        @Override
                        public void respond(Boolean response)
                        {
                            if (response == null)
                                ChatOutputHandler.chatError(ctx.getSource(), "TPA request timed out");
                            else if (!response)
                                ChatOutputHandler.chatError(ctx.getSource(), "TPA declined");
                            else
                                try
                                {
                                    TeleportHelper.teleport(player.getPlayerMP(), point);
                                }
                                catch (CommandRuntimeException e)
                                {
                                    ChatOutputHandler.chatError(ctx.getSource(), e.getMessage());
                                }
                        }
                    }, 20);
        }
        catch (QuestionerException.QuestionerStillActiveException e)
        {
            ChatOutputHandler.chatError(ctx.getSource(),
                    "Cannot run command because player is still answering a question. Please wait a moment");
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

}
