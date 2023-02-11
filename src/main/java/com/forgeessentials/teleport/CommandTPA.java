package com.forgeessentials.teleport;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
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
        return null;
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString().equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/tpa <player>: Request being teleported to another player");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/tpa <player> <here|x y z>: Propose another player to be teleported");
            return Command.SINGLE_SUCCESS;
        }

        final UserIdent player = arguments.parsePlayer(true, true);
        if (arguments.isEmpty())
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
            return;
        }

        arguments.tabComplete("here");

        final WarpPoint point;
        final String locationName;
        if (arguments.peek().equalsIgnoreCase("here"))
        {
            arguments.checkPermission(PERM_HERE);
            point = new WarpPoint(getServerPlayer(ctx.getSource()));
            locationName = getServerPlayer(ctx.getSource()).getDisplayName().getString();
            arguments.remove();
        }
        else
        {
            arguments.checkPermission(PERM_LOCATION);
            point = new WarpPoint((ServerWorld) getServerPlayer(ctx.getSource()).getLevel(), //
                    arguments.parseDouble(), arguments.parseDouble(), arguments.parseDouble(), //
                    player.getPlayer().yRot, player.getPlayer().xRot);
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
    }

}
