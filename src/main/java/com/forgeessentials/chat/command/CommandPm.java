package com.forgeessentials.chat.command;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.BaseComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandPm extends ForgeEssentialsCommandBuilder
{

    public CommandPm(boolean enabled)
    {
        super(enabled);
    }

    public static Map<Player, WeakReference<Player>> targetMap = new WeakHashMap<>();

    public static void setTarget(Player sender, Player target)
    {
        targetMap.put(sender, new WeakReference<>(target));
    }

    public static void clearTarget(Player sender)
    {
        targetMap.remove(sender);
    }

    public static Player getTarget(Player sender)
    {
        WeakReference<Player> target = targetMap.get(sender);
        if (target == null)
            return null;
        return target.get();
    }

    /* ------------------------------------------------------------ */

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "pm";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("message-or-target", StringArgumentType.greedyString())
                        .executes(CommandContext -> execute(CommandContext, "message")))
                .then(Commands.literal("clear").executes(CommandContext -> execute(CommandContext, "clear")))
                .executes(CommandContext -> execute(CommandContext, "get"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("clear"))
        {
            clearTarget(getServerPlayer(ctx.getSource()));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Cleared PM target");
            return Command.SINGLE_SUCCESS;
        }
        Player target = getTarget(getServerPlayer(ctx.getSource()));
        if (params.equals("get"))
        {
            if (target != null)
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Current PM target is %s",
                        target.getDisplayName().getString());
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatWarning(ctx.getSource(), "You don't have a PM target set");
            return Command.SINGLE_SUCCESS;
        }
        if (target == null)
        {
            String[] name = StringArgumentType.getString(ctx, "message-or-target").split(" ");
            if (name.length != 1)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "You must first select a target with /pm <player>");
                return Command.SINGLE_SUCCESS;
            }
            UserIdent player;
            try
            {
                player = parsePlayer(name[0], true, true);
            }
            catch (FECommandParsingException e)
            {
                ChatOutputHandler.chatError(ctx.getSource(), e.error);
                return Command.SINGLE_SUCCESS;
            }
            if (getServerPlayer(ctx.getSource()) == player.getPlayer())
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Cant send a pm to yourself");
                return Command.SINGLE_SUCCESS;
            }
            setTarget(getServerPlayer(ctx.getSource()), player.getPlayer());
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set PM target to %s", player.getUsernameOrUuid());
        }
        else
        {
            BaseComponent message = new TextComponent(StringArgumentType.getString(ctx, "message-or-target"));
            ModuleChat.tell(ctx.getSource(), message, target.createCommandSourceStack());
        }
        return Command.SINGLE_SUCCESS;
    }
}
