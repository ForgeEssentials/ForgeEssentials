package com.forgeessentials.teleport.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.teleport.TeleportModule;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandHome extends ForgeEssentialsCommandBuilder
{

    public CommandHome(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "home";
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
        return TeleportModule.PERM_HOME;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("set")
                        .executes(CommandContext -> execute(CommandContext, "set")
                                )
                        )
                .then(Commands.literal("setPlayer")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(CommandContext -> execute(CommandContext, "setOthers")
                                        )
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "goHome")
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("goHome"))
        {
            WarpPoint home = PlayerInfo.get(getServerPlayer(ctx.getSource()).getUUID()).getHome();
            if (home == null){
            	ChatOutputHandler.chatError(ctx.getSource(), "No home set. Use \"/home set\" first.");
        		return Command.SINGLE_SUCCESS;
            }
            TeleportHelper.teleport(getServerPlayer(ctx.getSource()), home);
        }
        if (params.equals("set"))
        {
            ServerPlayerEntity player = getServerPlayer(ctx.getSource());

            if (!hasPermission(player, TeleportModule.PERM_HOME_SET)){
            	ChatOutputHandler.chatError(ctx.getSource(), "You don't have the permission to set your home location.");
        		return Command.SINGLE_SUCCESS;
            }

            WarpPoint p = new WarpPoint(player);
            PlayerInfo info = PlayerInfo.get(player.getUUID());
            info.setHome(p);
            info.save();
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Home set to: %1.0f, %1.0f, %1.0f", p.getX(), p.getY(), p.getZ()));
        }
        if (params.equals("setOthers"))
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            if (player!= getServerPlayer(ctx.getSource())&&!hasPermission(getServerPlayer(ctx.getSource()), TeleportModule.PERM_HOME_OTHER)){
            	ChatOutputHandler.chatError(ctx.getSource(), "You don't have the permission to access other players home.");
        		return Command.SINGLE_SUCCESS;
            }

            WarpPoint p = new WarpPoint(player);
            PlayerInfo info = PlayerInfo.get(player.getUUID());
            info.setHome(p);
            info.save();
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Home set to: %1.0f, %1.0f, %1.0f", p.getX(), p.getY(), p.getZ()));
        }
        return Command.SINGLE_SUCCESS;
    }

}
