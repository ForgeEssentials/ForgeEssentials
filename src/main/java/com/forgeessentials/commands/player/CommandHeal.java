package com.forgeessentials.commands.player;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandHeal extends ForgeEssentialsCommandBuilder
{

    public CommandHeal(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "heal";
    }

    public String getUsage(ServerPlayer sender)
    {
        if (sender instanceof Player)
        {
            return "/heal <player> Heal yourself or other players (if you have permission).";
        }
        else
        {
            return "/heal <player> Heal a player.";
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(ModuleCommands.PERM + ".heal.others", DefaultPermissionLevel.OP, "Heal others");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(CommandContext -> execute(CommandContext, "others")))
                .executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("blank"))
        {
            heal(ctx.getSource().getPlayerOrException());
        }
        else if (params.equals("others"))
        {
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            if (!player.hasDisconnected())
            {
                heal(player);
            }
            else
            {
                ChatOutputHandler.chatError(ctx.getSource(), String
                        .format("Player %s does not exist, or is not online.", player.getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
            }
        }
        else
        {
            ChatOutputHandler.chatError(ctx.getSource(), getUsage(getServerPlayer(ctx.getSource())));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("others"))
        {
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            if (!player.hasDisconnected())
            {
                heal(player);
            }
            else
            {
                ChatOutputHandler.chatError(ctx.getSource(), String
                        .format("Player %s does not exist, or is not online.", player.getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
            }
        }
        else
        {
            ChatOutputHandler.chatError(ctx.getSource(), getUsage(getServerPlayer(ctx.getSource())));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    public void heal(Player target)
    {
        float toHealBy = target.getMaxHealth() - target.getHealth();
        target.heal(toHealBy);
        target.clearFire();
        target.getFoodData().eat(20, 1.0F);
        ChatOutputHandler.chatConfirmation(target, "You were healed.");
    }
}
