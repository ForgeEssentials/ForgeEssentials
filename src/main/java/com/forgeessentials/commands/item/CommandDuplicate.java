package com.forgeessentials.commands.item;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandDuplicate extends ForgeEssentialsCommandBuilder
{

    public CommandDuplicate(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "duplicate";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("size", IntegerArgumentType.integer(0, 64))
                        .executes(CommandContext -> execute(CommandContext, "size")))
                .executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        Player player = (Player) ctx.getSource().getEntity();
        ItemStack stack = player.getMainHandItem();
        if (stack == ItemStack.EMPTY)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "No item equipped");
            return Command.SINGLE_SUCCESS;
        }

        int stackSize = 0;
        if (params.equals("size"))
        {
            stackSize = IntegerArgumentType.getInteger(ctx, "size");
        }
        ItemStack newStack = stack.copy();
        if (stackSize > 0)
            newStack.setCount(stackSize);

        PlayerUtil.give(player, newStack);
        return Command.SINGLE_SUCCESS;
    }
}
