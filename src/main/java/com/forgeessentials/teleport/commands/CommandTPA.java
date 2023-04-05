package com.forgeessentials.teleport.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

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
import com.forgeessentials.util.questioner.QuestionerStillActiveException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandTPA extends ForgeEssentialsCommandBuilder
{

    public CommandTPA(boolean enabled)
    {
        super(enabled);
    }

    public static final String PERM_HERE = TeleportModule.PERM_TPA + ".here";
    public static final String PERM_LOCATION = TeleportModule.PERM_TPA + ".loc";

    @Override
    public String getPrimaryAlias()
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
    public String getPermissionNode()
    {
        return TeleportModule.PERM_TPA;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_HERE, DefaultPermissionLevel.ALL, "Allow teleporting other players to your own location (inversed TPA)");
        APIRegistry.perms.registerPermission(PERM_LOCATION, DefaultPermissionLevel.OP, "Allow teleporting other players to any location");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("help")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        )
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("position")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(CommandContext -> execute(CommandContext, "pos")
                                                )
                                        )
                                )
                        .then(Commands.literal("here")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(CommandContext -> execute(CommandContext, "here")
                                                )
                                        )
                                )
                        .executes(CommandContext -> execute(CommandContext, "toP")
                                )
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/tpa <player>: Request being teleported to another player");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/tpa <player> <here|x y z>: Propose another player to be teleported");
            return Command.SINGLE_SUCCESS;
        }

        final UserIdent player = getIdent(EntityArgument.getPlayer(ctx, "player"));
        if (params.equals("toP"))
        {
            try
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Waiting for response by %s", player.getUsernameOrUuid()));
                Questioner.addChecked(player.getPlayer().createCommandSourceStack(),
                        Translator.format("Allow teleporting %s to your location?", getServerPlayer(ctx.getSource()).getDisplayName().getString()),
                        new QuestionerCallback() {
                            @Override
                            public void respond(Boolean response)
                            {
                                if (response == null)
                                    ChatOutputHandler.chatError(ctx.getSource(), "TPA request timed out");
                                else if (response == false)
                                    ChatOutputHandler.chatError(ctx.getSource(), "TPA declined");
                                else
                                    try
                                    {
                                        TeleportHelper.teleport(getServerPlayer(ctx.getSource()), new WarpPoint(player.getPlayer()));
                                    }
                                    catch (CommandException e)
                                    {
                                        ChatOutputHandler.chatError(ctx.getSource(), e.getMessage());
                                    }
                            }
                        }, 20);
            }
            catch (QuestionerStillActiveException.CommandException e)
            {
                throw new QuestionerStillActiveException.CommandException();
            }
            return Command.SINGLE_SUCCESS;
        }


        final WarpPoint point;
        final String locationName;
        if (params.equals("here"))
        {
        	if(hasPermission(ctx.getSource(), PERM_HERE)) {
        		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
        		return Command.SINGLE_SUCCESS;
        	}
            point = new WarpPoint(getServerPlayer(ctx.getSource()));
            locationName = getServerPlayer(ctx.getSource()).getDisplayName().getString();
        }
        else
        {
        	if(hasPermission(ctx.getSource(), PERM_LOCATION)) {
        		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
        		return Command.SINGLE_SUCCESS;
        	}
            point = new WarpPoint(getServerPlayer(ctx.getSource()).getLevel().dimension(), //
                    BlockPosArgument.getLoadedBlockPos(ctx, "pos"), //
                    player.getPlayer().xRot, player.getPlayer().yRot);
            locationName = point.toReadableString();
        }

        try
        {
            Questioner.addChecked(player.getPlayer().createCommandSourceStack(), Translator.format("Do you want to be teleported to %s?", locationName), new QuestionerCallback() {
                @Override
                public void respond(Boolean response)
                {
                    if (response == null)
                        ChatOutputHandler.chatError(ctx.getSource(), "TPA request timed out");
                    else if (response == false)
                        ChatOutputHandler.chatError(ctx.getSource(), "TPA declined");
                    else
                        try
                        {
                            TeleportHelper.teleport(player.getPlayerMP(), point);
                        }
                        catch (CommandException e)
                        {
                            ChatOutputHandler.chatError(ctx.getSource(), e.getMessage());
                        }
                }
            }, 20);
        }
        catch (QuestionerStillActiveException.CommandException e)
        {
            throw new QuestionerStillActiveException.CommandException();
        }
        return Command.SINGLE_SUCCESS;
    }

}
